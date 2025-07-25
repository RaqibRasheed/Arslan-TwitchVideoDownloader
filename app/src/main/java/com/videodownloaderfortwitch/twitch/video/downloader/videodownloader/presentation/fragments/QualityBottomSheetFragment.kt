package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoMediaModel
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoFormat
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.FramentQualityBottomSheetBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.showToast
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.adapters.QualityAdapter
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdDownloadHelper
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdDownloadHelper.Companion.downloadTwitchMutableList
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdDownloadHelper.Companion.downloadTwitchMutableListLiveData
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdUtils.loadVideoThumbnailForTwitch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QualityBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FramentQualityBottomSheetBinding? = null

    private val binding get() = _binding!!

    private var videoList: List<VideoFormat> = emptyList()

    private lateinit var qualityAdapter: QualityAdapter

    private var videoTitle = ""

    private var thumbNailUrl = ""

    private var downloadableLink = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FramentQualityBottomSheetBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            videoList = it.getParcelableArrayList(KEY_VIDEO_FORMATS) ?: emptyList()
            videoTitle = it.getString(VD_TITLE).toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vdTitle.setText(videoTitle)

        thumbNailUrl = videoList[0].url

        if ( videoList.isNotEmpty()){
            downloadableLink = videoList[0].url
        }else{
            println("")
        }

        isCancelable = false
        val fallbackLowQuality: List<VideoFormat> = try {
            val lowQualityList = videoList.filter {
                val res = it.resolution.removeSuffix("p").toIntOrNull()
                res != null && res <= 360
            }

            lowQualityList.ifEmpty {
                // fallback: return the lowest available resolution
                val sortedByRes = videoList.sortedBy {
                    it.resolution.removeSuffix("p").toIntOrNull() ?: Int.MAX_VALUE
                }
                listOfNotNull(sortedByRes.firstOrNull())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }

        val fallbackLowQualityUrls = fallbackLowQuality.map { it.url }

        cleanUrl(fallbackLowQualityUrls.toString())
        VdLoggers.d("fallbackLowQualityUrls => ${cleanUrl(fallbackLowQualityUrls.toString())}")

       // Glide.with(requireContext()).load(thumbNailUrl).placeholder(R.drawable.place_holder).into(binding.thumbNailImage)

        loadVideoThumbnailForTwitch(thumbNailUrl,binding.thumbNailImage)

        setupRecyclerView()
        binding.renameText.setOnClickListener {
            binding.vdTitle.setText("")
            with(binding.vdTitle) {
                isFocusableInTouchMode = true
                isFocusable = true
                isCursorVisible = true
                requestFocus()
            }

            // Optionally show keyboard
            val imm = context?.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
            imm?.showSoftInput(binding.vdTitle, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }

        binding.crossImage.setOnClickListener {
            dismiss()
        }


        binding.downloadBt.setOnClickListener {

            dismiss()
            videoTitle = binding.vdTitle.text.toString() + ".mp4"



            if (downloadableLink.isNotEmpty()){

                val downloadHelper = VdDownloadHelper(requireContext())
                val dmManagerId = downloadHelper.startTwitchVideoDownloading(downloadableLink, videoTitle)
                val fileReadableSize = downloadHelper.getTwitchFormattedFileSize(dmManagerId)
                val newModel = VideoMediaModel(
                    id = videoTitle,
                    thumbnailUrl = downloadableLink,
                    videoName = videoTitle,
                    sizeText = fileReadableSize,
                    dateText = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    progress = 0,
                    statusText = "Pending",
                    downloadId = dmManagerId
                )
                downloadTwitchMutableList.add(newModel)
                downloadTwitchMutableListLiveData.postValue(downloadTwitchMutableList.size)
                val bundle = Bundle()
                bundle.putString(VdCst.TWITCH_VD_DOWNLOAD_URL_KEY,downloadableLink)
                bundle.putString(VdCst.TWITCH_TITLE_VD,videoTitle)
                findNavController().navigate(R.id.showProgressFragment)

            }else{
                requireContext().showToast("Please select any video quality.")
                println("")
            }

        }
    }

    private fun setupRecyclerView() {
        qualityAdapter = QualityAdapter { selected ->
            downloadableLink = selected.url

        }
        binding.qualityRv.adapter = qualityAdapter
        qualityAdapter.submitList(videoList)
    }

    fun cleanUrl(rawUrl: String): String {
        return rawUrl.trim().removePrefix("[").removeSuffix("]")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_VIDEO_FORMATS = "video_formats"
        private const val VD_TITLE = "vd_title"

        fun newInstance(videoFormats: List<VideoFormat>,title : String): QualityBottomSheetFragment {
            val fragment = QualityBottomSheetFragment()
            val bundle = Bundle().apply {
                putParcelableArrayList(KEY_VIDEO_FORMATS, ArrayList(videoFormats))
                putString(VD_TITLE, title)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}