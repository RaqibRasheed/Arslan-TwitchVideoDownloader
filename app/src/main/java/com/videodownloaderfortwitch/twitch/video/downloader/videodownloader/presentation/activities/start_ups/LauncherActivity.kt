package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.start_ups

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.ActivityLauncherBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.navigateToActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.MainActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.StatusBarHelper
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst.IS_NAVIGATE_TO_MAIN
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VideoPurchasedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    private val binding : ActivityLauncherBinding by lazy { ActivityLauncherBinding.inflate(layoutInflater) }

    @Inject
    lateinit var videoPurchasedPreference: VideoPurchasedPreference

    private var isNavigateToMain = false

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
        setupDropdownAnimation()
        setupClickListeners()

    }

    private fun init(){
        Glide.with(this).load(R.drawable.splash_icon).into(binding.splashIcon)
        isNavigateToMain = videoPurchasedPreference.getBoolean(IS_NAVIGATE_TO_MAIN)
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
}