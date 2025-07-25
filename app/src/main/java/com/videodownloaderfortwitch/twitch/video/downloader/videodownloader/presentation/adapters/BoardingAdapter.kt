package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R

class BoardingAdapter(private val context: Context) : PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null

    private val pagerImages = arrayOf(R.drawable.board_1, R.drawable.board_2, R.drawable.board_3)

    override fun getCount(): Int {
        return pagerImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater!!.inflate(R.layout.pager_layout, null)
        val imageView = view.findViewById<View>(R.id.imageView) as ImageView
        imageView.setImageResource(pagerImages[position])


        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}