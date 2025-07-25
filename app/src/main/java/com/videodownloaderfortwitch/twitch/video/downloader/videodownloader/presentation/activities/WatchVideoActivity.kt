package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoDownloadDetailModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.ActivityWatchVideoBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.gone
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.visible
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.StatusBarHelper
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers

class WatchVideoActivity : AppCompatActivity() {

    private val binding : ActivityWatchVideoBinding by lazy { ActivityWatchVideoBinding.inflate(layoutInflater) }

    private var twitchAppExoPlayer: ExoPlayer?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(binding.root)
        setupComponents()
        setupClickEvents()

    }

    private fun setupComponents(){

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            v.setBackgroundColor(ContextCompat.getColor(this,R.color.black))

            insets
        }
        StatusBarHelper.setLightStatusBarText(this)
        val model = intent.getParcelableExtra<VideoDownloadDetailModel>(VdCst.PLAYER_KEY_VIDEO_PLAYER)
        twitchAppExoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = twitchAppExoPlayer
        if (model != null) {
            binding.apply {
                titleText.text = model.fileName
                if (model.filePath.contains(".mp4")){
                    playerView.visible()
                    mediaImage.gone()
                    val videoUri = model.filePath.toUri()
                    val mediaItem = MediaItem.fromUri(videoUri)
                    twitchAppExoPlayer?.apply {
                        setMediaItem(mediaItem)
                        prepare()
                        playWhenReady = true
                    }
                }else{
                    playerView.gone()
                    mediaImage.visible()
                    mediaImage.setImageURI(model.filePath.toUri())
                }
            }
        }else{
            VdLoggers.d("DownloadModel is null")
        }
    }

    private fun setupClickEvents(){
        binding.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onResume() {
        twitchAppExoPlayer?.playWhenReady = true
        twitchAppExoPlayer?.play()
        super.onResume()

    }

    override fun onPause() {
        twitchAppExoPlayer?.playWhenReady = false
        twitchAppExoPlayer?.pause()
        super.onPause()

    }

    override fun onDestroy() {
        twitchAppExoPlayer?.release()
        super.onDestroy()
    }
}