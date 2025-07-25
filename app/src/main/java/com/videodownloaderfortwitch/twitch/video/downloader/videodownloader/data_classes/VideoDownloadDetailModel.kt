package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoDownloadDetailModel(val fileName: String, val filePath: String, val fileSize: Long) :
    Parcelable