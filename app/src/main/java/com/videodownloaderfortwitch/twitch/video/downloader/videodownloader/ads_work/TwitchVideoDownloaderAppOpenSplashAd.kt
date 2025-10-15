package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ads_work

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers
import java.util.Date


class TwitchVideoDownloaderAppOpenSplashAd {

     companion object{
         var appOpenSplashAdTwitchVideoDownloader: AppOpenAd? = null
     }

    private var isAppOpenSplashLoading = false

    var boolIsShowingAd = false

    private var loadTimeAppOpenSplashAd: Long = 0

    fun loadTwitchVideoDownloaderAppOpenSplash(context: Context, appOpenSplashID: String, loadAdCallback: (isLoad:Boolean) -> Unit) {

        if (isAppOpenSplashLoading || isAdAvailable()) {
            return
        }

        isAppOpenSplashLoading = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(context,
            appOpenSplashID,
            request,
          //  AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenSplashAdTwitchVideoDownloader = ad
                    isAppOpenSplashLoading = false
                    loadTimeAppOpenSplashAd = Date().time
                    loadAdCallback(true)
                    VdLoggers.d("TwitchVideoDownloaderAppOpenSplashAd => App Open Splash onAdLoaded.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isAppOpenSplashLoading = false
                    loadAdCallback(false)
                   VdLoggers.d("TwitchVideoDownloaderAppOpenSplashAd => App Open Splash onAdFailedToLoad: cause " + loadAdError.cause)
                }
            })
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long = 4): Boolean {
        val dateDifference: Long = Date().time - loadTimeAppOpenSplashAd
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenSplashAdTwitchVideoDownloader != null && wasLoadTimeLessThanNHoursAgo()
    }

    fun showTwitchAppOpenAdIfAvailable(activity: Activity, showAdCallbacks: () -> Unit) {
        if (boolIsShowingAd) {
           VdLoggers.d("TwitchVideoDownloaderAppOpenSplashAd => App Open Splash The app open ad is already showing.")
            return
        }

        if (!isAdAvailable()) {
           VdLoggers.d("TwitchVideoDownloaderAppOpenSplashAd => App Open Splash The app open ad is not ready yet.")
            loadTwitchVideoDownloaderAppOpenSplash(activity, activity.getString(R.string.app_open_splash_id)) { }
            return
        }

       VdLoggers.d("TwitchVideoDownloaderAppOpenSplashAd => App Open Splash Will show ad.")

        appOpenSplashAdTwitchVideoDownloader!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenSplashAdTwitchVideoDownloader = null
                boolIsShowingAd = false
               VdLoggers.d("TwitchVideoDownloaderAppOpenSplashAd => App Open Splash onAdDismissedFullScreenContent.")
                showAdCallbacks()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenSplashAdTwitchVideoDownloader = null
                boolIsShowingAd = false
                Log.d(
                    "DEV TAG TwitchVideoDownloaderAppOpenSplashAd",
                    "TwitchVideoDownloaderAppOpenSplashAd => App Open Splash onAdFailedToShowFullScreenContent: " + adError.message
                )
                showAdCallbacks()
            }
            override fun onAdShowedFullScreenContent() {
               VdLoggers.d("TwitchVideoDownloaderAppOpenSplashAd => pp Open Splash onAdShowedFullScreenContent.")
            }
        }
        boolIsShowingAd = true
        appOpenSplashAdTwitchVideoDownloader!!.show(activity)

    }


}