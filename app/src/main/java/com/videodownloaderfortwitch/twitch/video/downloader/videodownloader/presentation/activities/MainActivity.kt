package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.ActivityMainBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.dialogs.TwitchAppCommonDialog
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.StatusBarHelper
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst.TWITCH_DIALOG_TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private  var mNavController: NavController?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       enableEdgeToEdge()
        setContentView(binding.root)
        StatusBarHelper.setLightStatusBarText(this)
        setupViewComponents()
        setupNavController()

    }

    private fun setupViewComponents(){
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
        //    v.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            insets
        }

       /* ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom + resources.getDimensionPixelSize(R.dimen._20sdp)
            }
            insets
        }
       */
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { _, insets ->
            insets
        }
    }

    private fun setupNavController(){

        val navHostFragment = this.supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        mNavController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(mNavController!!)

    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            if (mNavController?.currentDestination?.id == R.id.landingFragment) {
                val twitchAppCommonDialog = TwitchAppCommonDialog.newInstance(TwitchAppCommonDialog.DialogType.EXIT)
                twitchAppCommonDialog.setCallback {
                    finishAffinity()
                }
                twitchAppCommonDialog.show(supportFragmentManager, TWITCH_DIALOG_TAG)
            } else {
                mNavController?.navigateUp()
            }
        }
    }
}