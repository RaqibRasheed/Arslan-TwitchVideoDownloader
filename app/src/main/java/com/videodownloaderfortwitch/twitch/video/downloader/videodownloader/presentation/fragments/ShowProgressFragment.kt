package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoMediaModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.FragmentShowProgressBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.gone
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.navigateToActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.visible
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.UtilityActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.adapters.VideoDownloadProgressAdapter
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.dialogs.TwitchAppCommonDialog
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst.TWITCH_DIALOG_TAG
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdDownloadHelper
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdDownloadHelper.Companion.downloadTwitchMutableList
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdDownloadHelper.Companion.downloadTwitchMutableListLiveData
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers

class ShowProgressFragment : Fragment() {

    private var _binding: FragmentShowProgressBinding? = null

    private val binding get() = _binding!!

    private var twitchDownloadLink = ""

    private var twitchDownloadVideoTitle = ""

    private val progressDelayHandler = Handler(Looper.getMainLooper())

    private lateinit var vdDownloadHelper: VdDownloadHelper

    private lateinit var videoDownloadProgressAdapter: VideoDownloadProgressAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShowProgressBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupComponents()
        setupDownloadComponents()

    }

    private fun setupComponents(){

        Glide.with(requireContext()).load(R.drawable.no_vd_ic).into(binding.noMediaIc)
        twitchDownloadLink = arguments?.getString(VdCst.TWITCH_VD_DOWNLOAD_URL_KEY).toString()
        twitchDownloadVideoTitle = arguments?.getString(VdCst.TWITCH_TITLE_VD).toString()
        VdLoggers.d("downloadMutableList size onViewCreated => ${downloadTwitchMutableList.size}")
        vdDownloadHelper = VdDownloadHelper(requireContext())
        videoDownloadProgressAdapter = VideoDownloadProgressAdapter { model ->
            val dialog = TwitchAppCommonDialog.newInstance(TwitchAppCommonDialog.DialogType.DELETE)
            dialog.setCallback {
                discardActiveDownload(model)
            }
            dialog.show(childFragmentManager, TWITCH_DIALOG_TAG)
        }
        binding.progressRv.adapter = videoDownloadProgressAdapter

        binding.drawer.setOnClickListener {
            requireActivity().navigateToActivity(UtilityActivity::class.java)
        }
    }

    private fun setupDownloadComponents(){
        if (downloadTwitchMutableList.isNotEmpty()) {
            binding.progressRv.visible()
            binding.noMediaGroup.gone()
            videoDownloadProgressAdapter.submitList(downloadTwitchMutableList.toList())
        } else {
            binding.noMediaGroup.visible()
            binding.progressRv.gone()
            Glide.with(requireContext()).load(R.drawable.no_vd_ic).into(binding.noMediaIc)
        }

        progressDelayHandler.post(updateDownloaderProgressTask)

        downloadTwitchMutableListLiveData.observe(viewLifecycleOwner){
            if (it <= 0){
                binding.noMediaGroup.visible()
                binding.progressRv.gone()
            }else{
                binding.noMediaGroup.gone()
                binding.progressRv.visible()
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun discardActiveDownload(model: VideoMediaModel) {
        vdDownloadHelper.deleteTwitchDownload(model.downloadId)
        val removed = downloadTwitchMutableList.removeAll { it.downloadId == model.downloadId }
        VdLoggers.d("downloadMutableList size Removed: $removed, New size => ${downloadTwitchMutableList.size}")
        VdLoggers.d("downloadMutableList size => ${downloadTwitchMutableList.size}")
        videoDownloadProgressAdapter.submitList(downloadTwitchMutableList.toList())
        downloadTwitchMutableListLiveData.postValue(downloadTwitchMutableList.size)

    }

    private val updateDownloaderProgressTask = object : Runnable {

        override fun run() {
            var updated = false
            val iterator = downloadTwitchMutableList.iterator()
            while (iterator.hasNext()) {
                val model = iterator.next()

                if (model.downloadId != -1L) {
                    val progress = vdDownloadHelper.getVideoDownloadProgress(model.downloadId)
                    val status = vdDownloadHelper.getTwitchDownloadStatus(model.downloadId)
                    if (status == "Completed") {
                        iterator.remove()
                        downloadTwitchMutableListLiveData.postValue(downloadTwitchMutableList.size)
                        updated = true
                        break
                    } else if (progress != model.progress || status != model.statusText) {
                        val index = downloadTwitchMutableList.indexOf(model)
                        downloadTwitchMutableList[index] = model.copy(progress = progress, statusText = status)
                        updated = true
                    }
                }
            }
            if (updated) {
                videoDownloadProgressAdapter.submitList(downloadTwitchMutableList.toList())
                if (downloadTwitchMutableList.isEmpty()) {
                    binding.noMediaGroup.visible()
                    binding.progressRv.gone()
                    Glide.with(requireContext()).load(R.drawable.no_vd_ic).into(binding.noMediaIc)
                }
            }
            progressDelayHandler.postDelayed(this, 1000)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressDelayHandler.removeCallbacks(updateDownloaderProgressTask)
    }

}
