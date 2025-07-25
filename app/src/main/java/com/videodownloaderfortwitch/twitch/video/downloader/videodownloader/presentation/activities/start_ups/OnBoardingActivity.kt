package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.start_ups

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.ActivityOnBoardingBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.navigateToActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.MainActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.adapters.BoardingAdapter
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst.IS_NAVIGATE_TO_MAIN
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VideoPurchasedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private val binding : ActivityOnBoardingBinding by lazy { ActivityOnBoardingBinding.inflate(layoutInflater) }

    private lateinit var sliderDots: Array<ImageView?>

    private val sliderCaptions = arrayOf(
        "Easily Download Videos from your\nTwitch video in High Quality.\nEnjoy Offline anytime, any where.",
        "Play Videos in multiple formats with our\npowerful built in HD videos Player. No\nExtra Apps Required",
        "Your downloads are secure, and the\napp is designed with simplicity in mind.\njust paste link and start downloading"
    )

    private val sliderImages = arrayOf(R.drawable.board_1, R.drawable.board_2, R.drawable.board_3)

    @Inject
    lateinit var videoPurchasedPreference: VideoPurchasedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
        //    v.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            insets
        }
        setupViewPager()
        setupClickListener()

    }

    private fun setupViewPager(){

        val boardingAdapter = BoardingAdapter(this)
        binding.viewPager.setAdapter(boardingAdapter)
        addSliderDotsIndicator(0)
        updateSliderCaptions(0)

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                addSliderDotsIndicator(position)
                updateSliderCaptions(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun addSliderDotsIndicator(currentPosition: Int) {

        sliderDots = arrayOfNulls(3)
        binding.sliderDots.removeAllViews()

        for (i in sliderDots.indices) {
            sliderDots[i] = ImageView(this)
            sliderDots[i]?.setImageResource(
                if (i == currentPosition) R.drawable.active_dot else R.drawable.non_active_dot
            )

            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(8, 0, 8, 0)
            }
            binding.sliderDots.addView(sliderDots[i], params)
        }
    }


    private fun setupClickListener(){
        binding.apply {
            nextBt.setOnClickListener {
                val nextPage = binding.viewPager.currentItem + 1
                if (nextPage < sliderImages.size) {
                    binding.viewPager.currentItem = nextPage
                } else {

                    videoPurchasedPreference.putBoolean(IS_NAVIGATE_TO_MAIN,true)
                    navigateToActivity(MainActivity::class.java)
                }
            }
        }
    }

    private fun updateSliderCaptions(position: Int) {
        binding.sliderText.text = sliderCaptions[position]
    }

}