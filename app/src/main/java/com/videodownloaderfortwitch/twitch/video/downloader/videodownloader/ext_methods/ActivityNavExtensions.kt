package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle

fun Activity.navigateToActivity(targetActivity: Class<out Activity>, extras: Bundle? = null) {
    try {
        val intent = Intent(this, targetActivity).apply {
            extras?.let { putExtras(it) }
        }
        val options = ActivityOptions
            .makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
            ?.toBundle()
        if (options != null) {
            startActivity(intent, options)
        } else {
            startActivity(intent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
