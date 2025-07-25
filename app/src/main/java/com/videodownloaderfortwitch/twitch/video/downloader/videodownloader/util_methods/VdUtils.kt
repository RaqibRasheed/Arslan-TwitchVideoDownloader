package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.BuildConfig
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.showToast
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File

object VdUtils {

    fun shareSavedTwitchVideoFile(context: Context, filePath: String) {
        val file = File(filePath)

        if (!file.exists()) {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
            return
        }

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share via"))
    }


    fun deleteSavedTwitchVideoFile(filePath: String, context: Context, onSuccess: () -> Unit) {
        val file = File(filePath)
        val isDeleted = file.delete()
        if (isDeleted) {
            context.showToast("File deleted successfully")
            onSuccess()
        } else {
            context.showToast("Failed to delete the file")
        }
    }

    fun loadVideoThumbnailForTwitch(videoUrl: String, imageView: ImageView) {
        Thread {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(videoUrl, HashMap())
                val bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST)
                retriever.release()

                bitmap?.let {
                    Handler(Looper.getMainLooper()).post {
                        imageView.setImageBitmap(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun loadVideoThumbnailSafely(context: Context, videoUrl: String, imageView: ImageView) {
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(videoUrl)
                    .header("User-Agent", "Mozilla/5.0") // Some servers require this
                    .build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val file = File.createTempFile("thumb_temp", ".mp4", context.cacheDir)
                    val sink = file.sink().buffer()
                    sink.writeAll(response.body!!.source())
                    sink.close()

                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(file.absolutePath)
                    val bitmap = retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST)
                    retriever.release()

                    Handler(Looper.getMainLooper()).post {
                        imageView.setImageBitmap(bitmap)
                    }

                    file.deleteOnExit()
                } else {
                    Log.e("Thumb", "Video download failed: ${response.code}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }


    fun closeOpenedInputQueryKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun rateOurTwitchDownloaderApp(context : Context, star : Int, dialog: DialogFragment) {
        try {

            if (star > 3) {

                val uri: Uri = "market://details?id=${BuildConfig.APPLICATION_ID}".toUri()

                val goToMarket = Intent(Intent.ACTION_VIEW, uri)

                goToMarket.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
                try {

                    context.startActivity(goToMarket)
                    dialog.dismiss()

                } catch (e: ActivityNotFoundException) {

                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".toUri()
                        )
                    )
                    dialog.dismiss()

                }

            } else {
                context.showToast("Thanks for the feedback")
                dialog.dismiss()
            }
        }catch (_:Exception){

        }
    }

    fun pasteVideoLinkFromClipBoard(context : Context, editText: EditText) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val clip: ClipData? = clipboard.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val pasteData: CharSequence = clip.getItemAt(0).coerceToText(context)
                if (pasteData.isNotEmpty()){
                    editText.setText(pasteData)
                }else{
                    context.showToast("You have not copied any text")
                }
            }else{
                context.showToast("You have not copied any text")
            }

        }else{
            context.showToast("You have not copied any text")
        }
    }

}