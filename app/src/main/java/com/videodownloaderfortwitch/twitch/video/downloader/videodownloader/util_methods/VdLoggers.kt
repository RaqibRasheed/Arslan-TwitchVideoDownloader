package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods

import android.util.Log

object VdLoggers {

    private const val TAG = "DEV TAG"

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun i(message: String) {
        Log.i(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }

    fun v(message: String) {
        Log.v(TAG, message)
    }

    fun w(message: String) {
        Log.w(TAG, message)
    }
}
