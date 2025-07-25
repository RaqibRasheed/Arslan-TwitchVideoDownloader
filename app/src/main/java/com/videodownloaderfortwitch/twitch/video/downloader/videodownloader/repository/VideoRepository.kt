package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.repository

import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoDownloadDetailModel
import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    suspend fun getVimeoDownloads(): Flow<List<VideoDownloadDetailModel>>
}