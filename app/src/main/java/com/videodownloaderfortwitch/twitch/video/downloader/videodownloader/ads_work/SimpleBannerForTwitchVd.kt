package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ads_work

import android.app.Activity
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.getSystemService
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdUtils

object SimpleBannerForTwitchVd {


    private var isCurrentlyLoading: Boolean = false

    fun loadSimpleBannerAd(activity: Activity, adViewLayout: FrameLayout, bannerAdID: String) {

        if (isCurrentlyLoading) {
            return
        }

        if (VdUtils.isInternetConnected(activity)) {

            isCurrentlyLoading = true
            loadAdMobSimpleBannerAd(activity, adViewLayout, bannerAdID)

        } else {

            adViewLayout.removeAllViews()
            adViewLayout.visibility = View.GONE
            VdLoggers.d("SimpleBanner not loaded")

        }
    }

    private fun getSimpleBannerAdSize(activity: Activity, adViewLayout: FrameLayout): AdSize {
        var simpleBannerAdWidthPix: Float = adViewLayout.width.toFloat()
        val simpleBannerDensity = activity.resources.displayMetrics.density

        if (simpleBannerAdWidthPix == 0f) {

            simpleBannerAdWidthPix = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bounds = activity.windowManager.currentWindowMetrics.bounds
                bounds.width().toFloat()
            } else {
                val display: Display? =
                    activity.getSystemService<DisplayManager>()?.getDisplay(Display.DEFAULT_DISPLAY)

                val outMetrics = DisplayMetrics()

                display?.getMetrics(outMetrics)
                outMetrics.widthPixels.toFloat()
            }

        }
        val width = (simpleBannerAdWidthPix / simpleBannerDensity).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, width)
    }


    private fun loadAdMobSimpleBannerAd(activity: Activity, adViewLayout: FrameLayout, bannerAdID: String) {
        var bannerAdView=AdView(activity)
        bannerAdView = AdView(activity).also {
            it.adUnitId = bannerAdID
            it.setAdSize(getSimpleBannerAdSize(activity, adViewLayout))
            it.loadAd(AdRequest.Builder().build())

            it.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()

                    isCurrentlyLoading = false

                    adViewLayout.apply {
                        removeAllViews()
                        visibility = View.VISIBLE
                        addView(bannerAdView)
                    }
                    VdLoggers.d("SimpleBanner loaded")

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)

                    adViewLayout.apply {
                        adViewLayout.removeAllViews()
                        adViewLayout.visibility = View.GONE
                    }
                    isCurrentlyLoading = false
                    VdLoggers.d("SimpleBanner failed")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    isCurrentlyLoading = false
                    VdLoggers.d( "SimpleBanner impression")

                }

            }

        }
    }
}