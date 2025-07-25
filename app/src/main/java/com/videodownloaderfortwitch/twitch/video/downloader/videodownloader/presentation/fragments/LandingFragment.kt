package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.data_classes.VideoFormat
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.databinding.FragmentLandingBinding
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.navigateToActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ext_methods.showToast
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.activities.UtilityActivity
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.presentation.dialogs.LoadingDialog
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdCst
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdLoggers
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdUtils
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.util_methods.VdUtils.closeOpenedInputQueryKeyboard
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class LandingFragment : Fragment() {

    private var _binding: FragmentLandingBinding? = null

    private val binding get() = _binding!!

    private var twitchLikToDownload: String = ""

    private var twitchVideoTitle = System.currentTimeMillis().toString()

    private var isFirstTimeTwitchLink = true

    private var isLoadWebViewOnce = true

   private var loadingDialog : LoadingDialog?= null

    private lateinit var requiredStoragePermissionHandlerLauncher: ActivityResultLauncher<Array<String>>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog.newInstance()
        setupClickEvents()
        setupStoragePermissionsForPintDownloaderApp()

    }

    private fun setupClickEvents(){

        binding.apply {

            generateBt.setOnClickListener {
                closeOpenedInputQueryKeyboard(generateBt)
                val inputText = enterLinkEt.text.toString()
                val extractedLink = extractTwitchClipUrl(inputText)
              //  twitchLikToDownload = enterLinkEt.text.toString()
                if (inputText.isNotEmpty()){
                    if (!extractedLink.isNullOrEmpty()){
                        twitchLikToDownload = extractedLink
                        VdLoggers.d("twitchLikToDownload => $twitchLikToDownload")

                        if (isValidTwitchClipLink(twitchLikToDownload)){
                            twitchVideoTitle = extractTwitchClipTitle(twitchLikToDownload).toString()
                            enterLinkEt.setText("")
                            isFirstTimeTwitchLink = true
                            isLoadWebViewOnce = true
                            loadingDialog?.show(parentFragmentManager, "LoadingDialog")
                            setupAppWebView()

                        }else{
                            enterLinkEt.setText("")
                            requireContext().showToast("The link provided is not a link for clip. Please try with valid clip link")
                        }
                    }else{
                        requireContext().showToast("The link provided is not a link for clip. Please try with valid clip link")
                    }
                }else{
                    requireContext().showToast("This field cannot be empty. Enter a valid link.")
                }

            }

            pasteImage.setOnClickListener {
                VdUtils.pasteVideoLinkFromClipBoard(requireContext(),enterLinkEt)
            }
            drawer.setOnClickListener {
                requireActivity().navigateToActivity(UtilityActivity::class.java)
            }

        }

    }



    private fun isValidTwitchClipLink(input: String): Boolean {
        val urlRegex = Regex("""https:\/\/www\.twitch\.tv\/[^\/]+\/clip\/[A-Za-z0-9_-]+""")
        val match = urlRegex.find(input)
        return match != null
    }

   private fun extractTwitchClipTitle(url: String): String? {
        val regex = Regex("""^https:\/\/www\.twitch\.tv\/[^\/]+\/clip\/([A-Za-z0-9_-]+)$""")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1)
    }

    private fun extractTwitchClipUrl(input: String): String? {
        val regex = Regex("""https:\/\/www\.twitch\.tv\/[^\/]+\/clip\/[A-Za-z0-9_-]+""")
        return regex.find(input)?.value
    }



    @SuppressLint("SetJavaScriptEnabled")
    private fun setupAppWebView() {
        binding.webView.apply {
            clearFormData()
            settings.saveFormData = true
            layoutParams = RelativeLayout.LayoutParams(-1, -1)
            webViewClient = SafeAppWebViewClient()
            settings.allowFileAccess = true
            settings.javaScriptEnabled = true
            settings.defaultTextEncodingName = "UTF-8"
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            settings.databaseEnabled = true
            settings.builtInZoomControls = false
            settings.setSupportZoom(true)
            settings.useWideViewPort = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.loadWithOverviewMode = true
            settings.loadsImagesAutomatically = true
            settings.blockNetworkImage = false
            settings.blockNetworkLoads = false
            settings.loadWithOverviewMode = true
           // loadUrl(VdCst.VD_SCRAPPING_SITE_LINK)
            loadUrl(VdCst.VD_SCRAPPING_SITE_LINK_NEW)
        }
    }

    private inner class SafeAppWebViewClient : WebViewClient() {

        override fun onPageFinished(webView: WebView?, url: String?) {
            super.onPageFinished(webView, url)

        //  scrappingForOldTwitch(webView)
            scrappingForNewTwitch(webView)

        }

    }

    private fun scrappingForOldTwitch(webView: WebView?){
        if (isLoadWebViewOnce){
            isLoadWebViewOnce = false

            val jsSetUrl = """
            javascript:(function() {
                document.getElementById('url').value = '$twitchLikToDownload';
            })();
        """.trimIndent()
            webView?.loadUrl(jsSetUrl)

            Handler().postDelayed({
                val jsClickButton = """
                javascript:(function() {
                var btn = document.getElementsByClassName("btn--primary")[0];
                     if(btn) { btn.click(); }
                })();
            """.trimIndent()
                webView?.loadUrl(jsClickButton)
            }, 1000)

            waitUntilDownloadAppears(webView!!) { links ->
                val videoList = mutableListOf<VideoFormat>()

                for ((resolution, url) in links) {
                    fetchVideoSize(url) { size ->
                        val format = VideoFormat(resolution, url, size.toString())
                        videoList.add(format)

                        // Optional: log as each is added
                        VdLoggers.d("Video Link: $format")

                        // Optional: wait for all sizes to be fetched
                        if (videoList.size == links.size) {
                            // All done
                            if (loadingDialog?.isVisible == true) {
                                loadingDialog?.dismiss()
                            }
                            VdLoggers.d("Final videoList: $videoList")

                            val fragment = QualityBottomSheetFragment.newInstance(videoList,twitchVideoTitle)
                            fragment.show(childFragmentManager, "QualityBottomSheetFragment")
                        }
                    }
                }
            }


        }
    }

    private fun scrappingForNewTwitch(webView: WebView?){
        if (isLoadWebViewOnce){
            isLoadWebViewOnce = false

            val jsSetUrl = """
    javascript:(function() {
        var input = document.querySelector('.clip-url-input');
        if (input) {
            input.value = '$twitchLikToDownload';
        }
    })();
""".trimIndent()
            webView?.loadUrl(jsSetUrl)

            Handler().postDelayed({
                val jsClickButton = """
        javascript:(function() {
            var btns = document.getElementsByClassName("get-download-link-button");
            if (btns.length > 0) {
                btns[0].click();
            }
        })();
    """.trimIndent()
                webView?.loadUrl(jsClickButton)
            }, 1000)

            waitUntilDownloadAppearsForNewSite(webView!!) { links ->


                val videoList = mutableListOf<VideoFormat>()

                for ((rawResolution, url) in links) {
               //      VdLoggers.d("links => $links")
                    val resolution = rawResolution.replace("Download Clip", "").trim()

                    fetchVideoSize(url) { size ->
                        val format = VideoFormat(resolution, url, size.toString())
                        videoList.add(format)

                        // Log each video format as it's added
                        VdLoggers.d("Video Link: $format")

                        // Check if all formats have been processed
                        if (videoList.size == links.size) {
                            if (loadingDialog?.isVisible == true) {
                                loadingDialog?.dismiss()
                            }

                            VdLoggers.d("Final videoList: $videoList")

                            val fragment = QualityBottomSheetFragment.newInstance(videoList, twitchVideoTitle)
                            fragment.show(childFragmentManager, "QualityBottomSheetFragment")
                        }
                    }
                }
            }


        }
    }

    fun waitUntilDownloadAppears(webView: WebView, retries: Int = 35, delayMillis: Long = 1000,
                                 onFound: (List<Pair<String, String>>) -> Unit
                                 ) {
        if (retries <= 0) {
            VdLoggers.d("Extracted Element not found after retries. $retries")
            requireActivity().showToast("The request could not be completed in time. Please retry.")
            if (loadingDialog?.isVisible == true) {
                loadingDialog?.dismiss()
            }
            return
        }

        webView.evaluateJavascript(
            """
        (function() {
            var result = [];
            var rows = document.querySelectorAll("table tbody tr");
            for (var i = 0; i < rows.length; i++) {
                var tds = rows[i].getElementsByTagName("td");
                if (tds.length >= 2) {
                    var quality = tds[0].innerText.trim();
                    var link = tds[1].querySelector("a")?.href;
                    if (quality && link) {
                        result.push({quality: quality, url: link});
                    }
                }
            }
            return JSON.stringify(result);
        })();
        """.trimIndent()
        ) { result ->
            result?.let {
                if (it != "null") {
                    try {
                        if (isFirstTimeTwitchLink){
                            if (!result.isNullOrEmpty() && result != "null") {
                                try {
                                    val cleanedJson = result.removeSurrounding("\"").replace("\\", "")
                                    val jsonArray = JSONArray(cleanedJson)
                                    val extracted = mutableListOf<Pair<String, String>>()

                                    for (i in 0 until jsonArray.length()) {
                                        val obj = jsonArray.getJSONObject(i)
                                        val quality = obj.getString("quality")
                                        val url = obj.getString("url")
                                        extracted.add(quality to url)
                                    }

                                    if (extracted.isNotEmpty()) {
                                        onFound(extracted)
                                    } else {
                                        retryTable(webView, retries, delayMillis, onFound)
                                    }

                                } catch (e: Exception) {
                                    Log.e("Extracted", "Parsing error: ${e.localizedMessage}")
                                    retryTable(webView, retries, delayMillis, onFound)
                                }
                            } else {
                                retryTable(webView, retries, delayMillis, onFound)
                            }
                        }


                    } catch (e: JSONException) {
                        e.printStackTrace()
                        if(loadingDialog?.isVisible == true){
                            loadingDialog?.dismiss()
                        }
                        VdLoggers.d("Extracted => JSONException ${e.localizedMessage}")
                        requireActivity().showToast("An exception occurred during request. Please try later.")

                    }
                } else {
                    VdLoggers.d("Extracted => Retrying... Remaining attempts: $retries")
                    Handler(Looper.getMainLooper()).postDelayed({
                        waitUntilDownloadAppears(webView, retries - 1, delayMillis,onFound)
                    }, delayMillis)
                }
            }
        }
    }

    private fun retryTable(webView: WebView, retries: Int, delayMillis: Long,
                           onFound: (List<Pair<String, String>>) -> Unit) {
        Log.d("Extracted", "Retrying (table)... Remaining: $retries")
        Handler(Looper.getMainLooper()).postDelayed({
            waitUntilDownloadAppears(webView, retries - 1, delayMillis, onFound)
        }, delayMillis)
    }

    fun waitUntilDownloadAppearsForNewSite(webView: WebView, retries: Int = 30, delayMillis: Long = 1000,
                                 onFound: (List<Pair<String, String>>) -> Unit
                                 ) {
        if (retries <= 0) {
            VdLoggers.d("Extracted Element not found after retries. $retries")
            requireActivity().showToast("The request could not be completed in time. Please retry.")
            if (loadingDialog?.isVisible == true) {
                loadingDialog?.dismiss()
            }
            return
        }

        webView.evaluateJavascript(
            """
        (function() {
            var result = [];
            var links = document.querySelectorAll("a.download-clip-button.button");
            for (var i = 0; i < links.length; i++) {
                var el = links[i];
                var link = el.getAttribute("href") || el.href;
                var quality = el.textContent.trim();
                if (link && quality && link !== "https://clipsey.com/") {
                    result.push({ quality: quality, url: link });
                }
            }
            return JSON.stringify(result);
        })();
    """.trimIndent()
        ) { rawResult ->
            parseDownloadLinks(rawResult)?.let { links ->
                if (links.isNotEmpty()) {
                    onFound(links)
                } else {
                    retryTableForNewSite(webView, retries, delayMillis, onFound)
                }
            } ?: run {
                retryTableForNewSite(webView, retries, delayMillis, onFound)
            }
        }
    }

    private fun parseDownloadLinks(rawResult: String): List<Pair<String, String>>? {
        return try {
            if (rawResult.isBlank() || rawResult == "null") return null
            val cleanedJson = rawResult.removeSurrounding("\"").replace("\\", "")
            val jsonArray = JSONArray(cleanedJson)
            List(jsonArray.length()) { i ->
                val obj = jsonArray.getJSONObject(i)
                obj.getString("quality") to obj.getString("url")
            }
        } catch (e: Exception) {
            VdLoggers.d("Extracted => JSONException ${e.localizedMessage}")
            if(loadingDialog?.isVisible == true){
                loadingDialog?.dismiss()
            }
            null
        }
    }

    private fun retryTableForNewSite(webView: WebView, retries: Int, delayMillis: Long,
                           onFound: (List<Pair<String, String>>) -> Unit) {
        Log.d("Extracted", "Retrying (table)... Remaining: $retries")
        Handler(Looper.getMainLooper()).postDelayed({
            waitUntilDownloadAppearsForNewSite(webView, retries - 1, delayMillis, onFound)
        }, delayMillis)
    }

    private fun setupStoragePermissionsForPintDownloaderApp(){
        requiredStoragePermissionHandlerLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            VdLoggers.d(
                if (granted) {
                    "Permissions Granted"
                } else {
                    "No Permissions Granted"
                }
            )
        }
        checkAndRequestStoragePermissionsForPintDownloaderApp()
    }

    private fun checkAndRequestStoragePermissionsForPintDownloaderApp() {

        val permissionsNeededMutableList = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeededMutableList.add(android.Manifest.permission.READ_MEDIA_VIDEO)
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeededMutableList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeededMutableList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if (permissionsNeededMutableList.isNotEmpty()) {
            requiredStoragePermissionHandlerLauncher.launch(permissionsNeededMutableList.toTypedArray())
        } else {
            VdLoggers.d("permissionsNeededMutableList list is empty")
        }
    }

    fun fetchVideoSize(url: String, onSizeReady: (String) -> Unit) {
        val client = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onSizeReady("Unknown")
            }

            override fun onResponse(call: Call, response: Response) {
                val contentLength = response.body?.contentLength()
                response.close() // prevent full download

                if (contentLength != null && contentLength > 0) {
                    val sizeMB = contentLength / (1024.0 * 1024.0)
                    onSizeReady(String.format("%.2f MB", sizeMB))
                } else {
                    onSizeReady("Unknown")
                }
            }
        })
    }

}
