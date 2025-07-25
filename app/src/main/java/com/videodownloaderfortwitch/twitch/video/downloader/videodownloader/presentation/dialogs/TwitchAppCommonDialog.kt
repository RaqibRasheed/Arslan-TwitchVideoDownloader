package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.R
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.DialogCommonBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.gone
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.setWidthPercent
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.visible
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdUtils


class TwitchAppCommonDialog : DialogFragment() {

    private var dialogType: DialogType? = null

    private var callBack: (() -> Unit)? = null

    private var binding : DialogCommonBinding? = null

    private var star : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogType = arguments?.getSerializable(DIALOG_ARGUMENT_TYPE) as? DialogType
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCommonBinding.inflate(layoutInflater)

        when (dialogType) {
            DialogType.DELETE -> {
                binding?.apply {
                    rateLl.gone()
                    deleteContentLl.visible()
                    image.setImageResource(R.drawable.image_delete)
                    cancel.text = resources.getString(R.string.no)
                    delete.text = resources.getString(R.string.yes)
                    caption.text = resources.getString(R.string.delete)
                    description.text = resources.getString(R.string.delete_cap)
                }
            }
            DialogType.EXIT -> {
                binding?.apply {
                    rateLl.gone()
                    deleteContentLl.visible()
                    image.setImageResource(R.drawable.exit_image)
                    cancel.text = resources.getString(R.string.no)
                    delete.text = resources.getString(R.string.yes)
                    caption.text = resources.getString(R.string.exit)
                    description.text = resources.getString(R.string.are_you_sure_you_want_to_exit)
                }
            }
            DialogType.RATE_US -> {
                binding?.apply {
                    rateLl.visible()
                    deleteContentLl.gone()
                }
            }

            null -> {

            }
        }

        binding?.apply {
            cancel.setOnClickListener {
                dismiss()
            }
            delete.setOnClickListener {
                when (dialogType) {
                    DialogType.DELETE -> callBack?.invoke()
                    DialogType.EXIT -> callBack?.invoke()
                    DialogType.RATE_US -> callBack?.invoke()
                    else -> {}
                }
                dismiss()
            }

            star1.setOnClickListener {
                star1.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.star_selected))
                star2.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star3.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star4.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star5.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star =1
                Log.d("rateValue", "rateValue: $star")
            }
            star2.setOnClickListener {
                star1.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.star_selected))
                star2.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star3.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star4.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star5.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star =2
                Log.d("rateValue", "rateValue: $star")
            }
            star3.setOnClickListener {
                star1.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star2.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star3.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star4.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star5.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star =3
                Log.d("rateValue", "rateValue: $star")
            }
            star4.setOnClickListener {
                star1.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star2.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star3.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star4.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star5.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_unselect))
                star =4
                Log.d("rateValue", "rateValue: $star")
            }
            star5.setOnClickListener {
                star1.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star2.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star3.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star4.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star5.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.star_selected))
                star =5
                Log.d("rateValue", "rateValue: $star")
            }
            rateNowBtn.setOnClickListener {
                VdUtils.rateOurTwitchDownloaderApp(requireContext(),star,this@TwitchAppCommonDialog)
                dismiss()
            }


        }
        return binding?.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.setWidthPercent(90)
            it.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),android.R.color.transparent))
        }
    }

    fun setCallback(callback: () -> Unit) {
        this.callBack = callback
    }

    companion object {
        private const val DIALOG_ARGUMENT_TYPE = "DIALOG_ARGUMENT"

        fun newInstance(dialogType: DialogType): TwitchAppCommonDialog {
            val dialog = TwitchAppCommonDialog()
            val args = Bundle()
            args.putSerializable(DIALOG_ARGUMENT_TYPE, dialogType)
            dialog.arguments = args
            return dialog
        }
    }

    enum class DialogType {
        DELETE,
        EXIT,
        RATE_US
    }
}