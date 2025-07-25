package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes

data class VideoMediaModel(
    val id: String,
    val thumbnailUrl: String,
    val videoName: String,
    val sizeText: String,
    val dateText: String,
    val progress: Int,
    val statusText: String,
    var downloadId: Long = -1L
)