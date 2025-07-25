package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoFormat(
    val resolution: String,
    val url: String,
    val size: String
) : Parcelable
