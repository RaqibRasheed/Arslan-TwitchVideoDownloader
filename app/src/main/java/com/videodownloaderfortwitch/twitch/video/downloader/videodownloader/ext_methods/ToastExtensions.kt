package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
