package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoDownloadDetailModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(private val repository: VideoRepository)  : ViewModel(){

    suspend fun getTwitchDownloads(): Flow<List<VideoDownloadDetailModel>> {
        return repository.getVimeoDownloads()
    }
}