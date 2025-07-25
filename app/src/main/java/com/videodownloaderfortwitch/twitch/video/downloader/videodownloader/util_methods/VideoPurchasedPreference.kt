package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class VideoPurchasedPreference @Inject constructor(@ApplicationContext private var context: Context) {


    private val VIDEO_PREFERENCS = "video_purchased_preference"
    var vdSp: SharedPreferences = context.getSharedPreferences(VIDEO_PREFERENCS, Context.MODE_PRIVATE)


    fun putString(key: String, value: String): Boolean {
        val editor = vdSp.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun putInt(key: String, value: Int): Boolean {
        val editor = vdSp.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun putLong(key: String, value: Long): Boolean {
        val editor = vdSp.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    fun getLong(key: String): Long {
        return vdSp.getLong(key, 0)
    }

    fun putBoolean(key: String, value: Boolean): Boolean {
        val editor = vdSp.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }


    fun getString(key: String): String {
        return vdSp.getString(key, "")!!
    }

    fun getInt(key: String): Int {
        return vdSp.getInt(key, 0)
    }

    fun getBoolean(key: String): Boolean {
        return vdSp.getBoolean(key, false)
    }

}