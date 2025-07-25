package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoDownloadDetailModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.ItemTwitchDownloadBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class VideoDownloadAdapter @Inject constructor() :
    ListAdapter<VideoDownloadDetailModel, VideoDownloadAdapter.ViewHolder>(DiffCallback()) {

    var onItemDeleteClicked: ((VideoDownloadDetailModel) -> Unit)? = null
    var onItemShareClicked: ((VideoDownloadDetailModel) -> Unit)? = null
    var onItemClicked: ((VideoDownloadDetailModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTwitchDownloadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemTwitchDownloadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(model: VideoDownloadDetailModel) {

            val fileSizeBytes = getPintFileSize(model.filePath)
            val readableSize = formatPintFileSize(fileSizeBytes)
            val lastModified = File(model.filePath).lastModified()
            val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(lastModified))

            VdLoggers.d("FileSize File size => $readableSize")

            Glide.with(binding.root.context).load(model.filePath).into(binding.thumbnail)
            binding.fileNameText.text = model.fileName

            binding.fileSizeText.text = readableSize
            binding.creationDateText.text = formattedDate
            binding.menuIcon.setOnClickListener {
                onItemDeleteClicked?.invoke(model)
            }

            binding.shareIcon.setOnClickListener {
                onItemShareClicked?.invoke(model)
            }

            binding.root.setOnClickListener {
                onItemClicked?.invoke(model)
            }
        }
    }

    fun formatPintFileSize(sizeInBytes: Long): String {
        val kb = sizeInBytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$sizeInBytes Bytes"
        }
    }

    fun getPintFileSize(filePath: String): Long {
        val file = File(filePath)
        return if (file.exists()) file.length() else 0L
    }

    class DiffCallback : DiffUtil.ItemCallback<VideoDownloadDetailModel>() {
        override fun areItemsTheSame(oldItem: VideoDownloadDetailModel, newItem: VideoDownloadDetailModel): Boolean {
            return oldItem.filePath == newItem.filePath
        }

        override fun areContentsTheSame(oldItem: VideoDownloadDetailModel, newItem: VideoDownloadDetailModel): Boolean {
            return oldItem == newItem
        }
    }
}
