package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.DialogWaitBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.setWidthPercent

class LoadingDialog : DialogFragment() {

    private var _binding: DialogWaitBinding? = null

    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogWaitBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.setWidthPercent()
            it.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),android.R.color.transparent))
        }
        isCancelable = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): LoadingDialog {
            val dialog = LoadingDialog()
            return dialog
        }
    }
}