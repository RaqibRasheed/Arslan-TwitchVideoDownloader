package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.BuildConfig
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.ActivityUtilityBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.dialogs.TwitchAppCommonDialog
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst.TWITCH_DIALOG_TAG
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers

class UtilityActivity : AppCompatActivity() {

    private val binding : ActivityUtilityBinding by lazy { ActivityUtilityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupComponents()
        setupDropdownAnimation()
        setupClickEvents()

    }

    private fun setupComponents() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            insets
        }
        Glide.with(this).load(R.drawable.splash_icon).into(binding.drawerIc)
        binding.captionText.isAllCaps = true

    }


    private fun setupClickEvents() {

        binding.apply {
            homeText.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnClose.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            rateUsText.setOnClickListener {
                val dialog = TwitchAppCommonDialog.newInstance(TwitchAppCommonDialog.DialogType.RATE_US)
                dialog.setCallback {
                    finishAffinity()
                }
                dialog.show(supportFragmentManager, TWITCH_DIALOG_TAG)
            }
            shareText.setOnClickListener {
                appInvitation()
            }
            privacyPolicyText.setOnClickListener {
                appPrivacyPolicyContent(
                    this@UtilityActivity,
                    "https://sites.google.com/view/videodownloaderfortwitch001/home"
                )

            }
            exitText.setOnClickListener {
                val dialog = TwitchAppCommonDialog.newInstance(TwitchAppCommonDialog.DialogType.EXIT)
                dialog.setCallback {
                    finishAffinity()
                }
                dialog.show(supportFragmentManager, TWITCH_DIALOG_TAG)
            }
        }
    }

    private fun setupDropdownAnimation() {
        val views = listOf(
            binding.drawerIc, binding.captionText, binding.homeText, binding.rateUsText,
            binding.shareText, binding.privacyPolicyText, binding.exitText
        )
        views.forEachIndexed { index, view ->
            view.translationY = -200f
            view.alpha = 0f
            view.animate()
                .translationY(0f)
                .alpha(1f)
                .setStartDelay((index * 50).toLong())
                .setDuration(400)
                .start()
        }
    }

    private fun appInvitation() {
        this.let {
            try {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, it.getString(R.string.app_name))
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                )
                sendIntent.type = "text/plain"
                it.startActivity(sendIntent)
            } catch (_: Exception) {

            }
        }
    }

    private fun appPrivacyPolicyContent(context: Context, link: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, link.toUri())
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            VdLoggers.d("privacy: ${e.localizedMessage}")
        }

    }
}