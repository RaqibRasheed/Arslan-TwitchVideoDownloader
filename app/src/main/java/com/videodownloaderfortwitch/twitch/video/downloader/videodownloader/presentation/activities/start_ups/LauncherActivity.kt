package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.start_ups

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.ads.MobileAds
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.BuildConfig
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ads_work.TwitchVideoDownloaderAppOpenSplashAd
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ads_work.ads_consent.TwitchVideoDownloaderConsentManager
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ads_work.ads_consent.TwitchVideoDownloaderConsentResponse
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.ActivityLauncherBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.navigateToActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.MainActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.StatusBarHelper
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst.IS_NAVIGATE_TO_MAIN
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VideoPurchasedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    private val binding : ActivityLauncherBinding by lazy { ActivityLauncherBinding.inflate(layoutInflater) }

    @Inject
    lateinit var videoPurchasedPreference: VideoPurchasedPreference

    private var isNavigateToMain = false

    private val twitchVideoDownloaderConsentManager by lazy { TwitchVideoDownloaderConsentManager(this) }

    private var twitchVideoDownloaderAppOpenSplashAd: TwitchVideoDownloaderAppOpenSplashAd?= null

    private var adSplashHandler : Handler?= null

    private var boolIsAdCompleted: Boolean = false

    private var boolIsTwitchAppOpenLoaded: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(binding.root)
        StatusBarHelper.setLightStatusBarText(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
         //   v.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            insets
        }
        init()
        configPinterestDownloaderAppConsent()
        setupDropdownAnimation()
        setupClickListeners()

    }

    private fun init(){
        Glide.with(this).load(R.drawable.splash_icon).into(binding.splashIcon)
        isNavigateToMain = videoPurchasedPreference.getBoolean(IS_NAVIGATE_TO_MAIN)
        adSplashHandler = Handler(Looper.getMainLooper())
        twitchVideoDownloaderAppOpenSplashAd = TwitchVideoDownloaderAppOpenSplashAd()

    }

    private fun setupClickListeners(){
        binding.apply {
            startBt.setOnClickListener {
                if (isNavigateToMain){
                    navigateToActivity(MainActivity::class.java)
                }else{
                    navigateToActivity(OnBoardingActivity::class.java)
                }
            }
        }
    }

    private fun setupDropdownAnimation() {
        val views = listOf(binding.splashIcon, binding.title1, binding.title2, binding.startBt,)
        views.forEachIndexed { index, view ->
            view.translationY = -200f
            view.alpha = 0f
            view.animate()
                .translationY(0f)
                .alpha(1f)
                .setStartDelay((index * 100).toLong())
                .setDuration(1000)
                .start()
        }
    }

    private fun configPinterestDownloaderAppConsent() {
        when (BuildConfig.DEBUG) {
            true -> twitchVideoDownloaderConsentManager.initTwitchVideoDownloaderDebugConsent(twitchOnConsentResponse = onConsentResponse)
            false -> twitchVideoDownloaderConsentManager.initTwitchVideoDownloaderReleaseConsent(twitchOnConsentResponse = onConsentResponse)
        }
    }

    private val onConsentResponse = object : TwitchVideoDownloaderConsentResponse {
        override fun onConsentResponse(errorMessage: String?) {
            errorMessage?.let {
                VdLoggers.d("onResponse: Error: $it")

            }
            MobileAds.initialize(this@LauncherActivity) {
                VdLoggers.d("onConsentResponse: ${it}")
            }
            loadTwitchDownloaderAppOpenSplashAd()
            setSplashScreenTimer()

        }

        override fun onConsentPolicyRequired(isRequired: Boolean) {
            Log.d("TAG", "onPolicyRequired: Is-Required: $isRequired")
        }

    }

    private fun loadTwitchDownloaderAppOpenSplashAd(){
        twitchVideoDownloaderAppOpenSplashAd?.loadTwitchVideoDownloaderAppOpenSplash(
            this@LauncherActivity,
            getString(R.string.app_open_splash_id)
        ) {
            if (it) {

                if (!boolIsAdCompleted) {
                    boolIsTwitchAppOpenLoaded = true
                    twitchVideoDownloaderAppOpenSplashAd?.showTwitchAppOpenAdIfAvailable(this) {
                        boolIsAdCompleted = true
                        navigateToNextActivity()

                    }
                }

            } else {
                boolIsAdCompleted = true
                navigateToNextActivity()
            }
        }
    }

    private fun navigateToNextActivity() {
        if (isNavigateToMain){
            navigateToActivity(MainActivity::class.java)
        }else{
            navigateToActivity(OnBoardingActivity::class.java)
        }
    }

    private fun setSplashScreenTimer(){

        adSplashHandler?.postDelayed({
            if (TwitchVideoDownloaderAppOpenSplashAd.appOpenSplashAdTwitchVideoDownloader == null && !boolIsAdCompleted) {
                boolIsAdCompleted = true
                navigateToNextActivity()
            }else{
                VdLoggers.d("else case" )
            }
        }, 11000)

    }

}