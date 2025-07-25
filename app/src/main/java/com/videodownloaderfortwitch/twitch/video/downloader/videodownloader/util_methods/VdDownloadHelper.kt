package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoMediaModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst.DOWNLOAD_FOLDER_NAME
import java.io.File

class VdDownloadHelper(private val context: Context) {

    private val dmManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun startTwitchVideoDownloading(url: String, fileName: String): Long {

        val downloadsDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            DOWNLOAD_FOLDER_NAME)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val file = File(downloadsDir, fileName)

        val request = DownloadManager.Request(url.toUri())
            .setTitle(fileName)
            .setDescription("Downloading video...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(file))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setMimeType("video/mp4")
        return dmManager.enqueue(request)
    }

    fun getVideoDownloadProgress(downloadId: Long): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        dmManager.query(query).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val bytesDownloaded =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                return if (bytesTotal > 0) {
                    (bytesDownloaded * 100L / bytesTotal).toInt()
                } else 0
            }
        }
        return 0
    }

    fun getTwitchDownloadStatus(downloadId: Long): String {
        val query = DownloadManager.Query().setFilterById(downloadId)
        dmManager.query(query).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val status =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                return when (status) {
                    DownloadManager.STATUS_FAILED -> "Failed"
                    DownloadManager.STATUS_PAUSED -> "Paused"
                    DownloadManager.STATUS_PENDING -> "Pending"
                    DownloadManager.STATUS_RUNNING -> "Downloading"
                    DownloadManager.STATUS_SUCCESSFUL -> "Completed"
                    else -> "Unknown"
                }
            }
        }
        return "Not Found"
    }


    fun deleteTwitchDownload(downloadId: Long) {
        dmManager.remove(downloadId)
    }

    private fun getTwitchFileSize(downloadId: Long): Long {
        val query = DownloadManager.Query().setFilterById(downloadId)
        dmManager.query(query).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            }
        }
        return -1L
    }

    fun getTwitchFormattedFileSize(downloadId: Long): String {
        val sizeBytes = getTwitchFileSize(downloadId)
        return if (sizeBytes > 0)
            android.text.format.Formatter.formatFileSize(context, sizeBytes)
        else
            "Unknown size"
    }

    companion object{

        val downloadTwitchMutableList = mutableListOf<VideoMediaModel>()

        val downloadTwitchMutableListLiveData = MutableLiveData<Int>()

    }

}
