package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.FragmentDownloadedFilesBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.gone
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.navigateToActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.visible
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.UtilityActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.WatchVideoActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.adapters.VideoDownloadAdapter
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.dialogs.TwitchAppCommonDialog
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.viewmodels.VideoViewModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst.TWITCH_DIALOG_TAG
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class DownloadedFilesFragment : Fragment() {

    private var _binding: FragmentDownloadedFilesBinding? = null

    private val binding get() = _binding!!

    private val viewModel : VideoViewModel by viewModels()

    @Inject
    lateinit var videoDownloadAdapter: VideoDownloadAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDownloadedFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupComponent()
        setupDownloadedVideos()
        setupAdapterClickEvents()

    }

    private fun setupComponent(){
        binding.apply {
            Glide.with(requireContext()).load(R.drawable.no_vd_ic).into(noMediaIc)
            drawer.setOnClickListener {
                requireActivity().navigateToActivity(UtilityActivity::class.java)
            }
        }
    }

    private fun setupAdapterClickEvents(){

        videoDownloadAdapter.onItemDeleteClicked = { model ->
            val dialog = TwitchAppCommonDialog.newInstance(TwitchAppCommonDialog.DialogType.DELETE)
            dialog.setCallback {
                VdUtils.deleteSavedTwitchVideoFile(model.filePath,requireContext()){
                    setupDownloadedVideos()
                }
            }
            dialog.show(childFragmentManager, TWITCH_DIALOG_TAG)

        }

        videoDownloadAdapter.onItemShareClicked = { model ->
            VdUtils.shareSavedTwitchVideoFile(requireContext(),model.filePath)
        }

        videoDownloadAdapter.onItemClicked = { model ->
            val intent = Intent(requireContext(), WatchVideoActivity::class.java).apply {
                putExtra(VdCst.PLAYER_KEY_VIDEO_PLAYER, model)
            }
            val options = ActivityOptions
                .makeCustomAnimation(requireContext(), android.R.anim.fade_in, android.R.anim.fade_out)
                .toBundle()
            startActivity(intent, options)
        }
    }

    private fun setupDownloadedVideos(){

        viewModel.viewModelScope.launch {
            viewModel.getTwitchDownloads().collect{ list ->
                if (list.isNotEmpty()){
                    val sortedList = list
                        .sortedByDescending { File(it.filePath).lastModified() }
                    val animation: LayoutAnimationController =
                        AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.anim_layout_animation_scale_in)
                    binding.recycler.layoutAnimation = animation
                    videoDownloadAdapter.submitList(sortedList)
                    binding.apply {
                        recycler.visible()
                        noMediaGroup.gone()
                        recycler.adapter = videoDownloadAdapter
                    }
                }else{
                    binding.apply {
                        noMediaGroup.visible()
                        recycler.gone()
                    }
                }
            }
        }
    }
}
