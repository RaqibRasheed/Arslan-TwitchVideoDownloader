package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoFormat
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.ItemQualityBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.gone
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.invisible
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.visible
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers

class QualityAdapter(private val onItemClick: (VideoFormat) -> Unit)
    : ListAdapter<VideoFormat, QualityAdapter.QualityViewHolder>(DiffCallback()) {

    private var selectedPosition = 0

    inner class QualityViewHolder(private val binding: ItemQualityBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoFormat, isSelected: Boolean) = with(binding) {
            qualityText.text = item.resolution
            sizeText.text = item.size
            VdLoggers.d("Video Size => ${item.size}")
            binding.sizeText.gone()
           /* if (item.size == "0.00 MB"){
                binding.sizeText.invisible()
            }else{
                binding.sizeText.visible()
            }*/

            val context = root.context
            val selectedColor = ContextCompat.getColor(context, R.color.app_second_color)
            val defaultColor = ContextCompat.getColor(context, R.color.black)

            dotImage.setColorFilter(
                if (isSelected) selectedColor else defaultColor,
                PorterDuff.Mode.SRC_IN
            )

            if (isSelected){
                dotImage.setImageResource( R.drawable.dot_selected)
            }else{
                dotImage.setImageResource( R.drawable.dot_unselect)

            }

            qualityText.setTextColor(if (isSelected) selectedColor else defaultColor)
            sizeText.setTextColor(if (isSelected) selectedColor else defaultColor)


            // Handle click
            root.setOnClickListener {
                val previous = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityViewHolder {
        val binding = ItemQualityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QualityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QualityViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)

    }

    class DiffCallback : DiffUtil.ItemCallback<VideoFormat>() {
        override fun areItemsTheSame(oldItem: VideoFormat, newItem: VideoFormat): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: VideoFormat, newItem: VideoFormat): Boolean {
            return oldItem == newItem
        }
    }
}
