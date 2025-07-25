package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Rect
import android.view.ViewGroup

fun Dialog.setWidthPercent(percentage: Int = 80) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent
    window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}
