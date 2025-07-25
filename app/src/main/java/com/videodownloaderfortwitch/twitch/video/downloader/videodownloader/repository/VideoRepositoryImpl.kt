package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.repository

import android.os.Environment
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoDownloadDetailModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor() : VideoRepository {

    override suspend fun getVimeoDownloads(): Flow<List<VideoDownloadDetailModel>> = flow {
        val downloadDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            VdCst.DOWNLOAD_FOLDER_NAME
        )

        val videos = if (downloadDir.exists() && downloadDir.isDirectory) {
            downloadDir.listFiles { file ->
                val ext = file.extension.lowercase()
                ext == "mp4" || ext == "jpg"
            }?.map { file ->
                VideoDownloadDetailModel(
                    fileName = file.name,
                    filePath = file.absolutePath,
                    fileSize = file.length()
                )
            } ?: emptyList()
        } else {
            emptyList()
        }

        emit(videos)
    }.flowOn(Dispatchers.IO)
}