package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class StatusBarHelper {

    companion object {

        /**
         * Set status bar text color to dark (for light backgrounds)
         * @param activity The activity instance
         */
        fun setDarkStatusBarText(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11 (API 30) and above
                activity.window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0 (API 23) and above
                activity.window.decorView.systemUiVisibility =
                    activity.window.decorView.systemUiVisibility or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        /**
         * Set status bar text color to light (for dark backgrounds)
         * @param activity The activity instance
         */
        fun setLightStatusBarText(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11 (API 30) and above
                activity.window.insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0 (API 23) and above
                activity.window.decorView.systemUiVisibility =
                    activity.window.decorView.systemUiVisibility and
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

        /**
         * Set status bar background color and text color
         * @param activity The activity instance
         * @param backgroundColor Status bar background color
         * @param lightText True for light text, false for dark text
         */
        fun setStatusBarColor(activity: Activity, backgroundColor: Int, lightText: Boolean = true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = backgroundColor

                if (lightText) {
                    setLightStatusBarText(activity)
                } else {
                    setDarkStatusBarText(activity)
                }
            }
        }

        /**
         * Make status bar transparent with text color
         * @param activity The activity instance
         * @param lightText True for light text, false for dark text
         */
        fun setTransparentStatusBar(activity: Activity, lightText: Boolean = true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = Color.TRANSPARENT
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )

                if (lightText) {
                    setLightStatusBarText(activity)
                } else {
                    setDarkStatusBarText(activity)
                }
            }
        }

        /**
         * Using WindowCompat (recommended for new projects)
         * @param activity The activity instance
         * @param lightText True for light text, false for dark text
         */
        fun setStatusBarTextColorCompat(activity: Activity, lightText: Boolean = true) {
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
            val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            controller.isAppearanceLightStatusBars = !lightText
        }
    }
}

