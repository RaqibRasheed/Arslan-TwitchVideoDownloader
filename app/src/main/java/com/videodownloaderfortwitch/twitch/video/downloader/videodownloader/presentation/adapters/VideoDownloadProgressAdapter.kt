package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoMediaModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.ItemDownloadProgressBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdUtils


class VideoDownloadProgressAdapter(private val onItemClick: (VideoMediaModel) -> Unit)
    : ListAdapter<VideoMediaModel, VideoDownloadProgressAdapter.ProgressViewHolder>(DownloadDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val binding = ItemDownloadProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProgressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindData(item)
    }

    inner class ProgressViewHolder(private val binding: ItemDownloadProgressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(item: VideoMediaModel) {

            VdUtils.loadVideoThumbnailForTwitch(item.thumbnailUrl,binding.thumbNail)
            binding.apply {
                vdNameText.text = item.videoName
                progressBar.progress = item.progress
                statusText.text = item.statusText

                menuIcon.setOnClickListener {
                    onItemClick(item)
                }
            }

        }
    }

    class DownloadDiffCallback : DiffUtil.ItemCallback<VideoMediaModel>() {
        override fun areItemsTheSame(oldItem: VideoMediaModel, newItem: VideoMediaModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoMediaModel, newItem: VideoMediaModel): Boolean {
            return oldItem == newItem
        }
    }

}
