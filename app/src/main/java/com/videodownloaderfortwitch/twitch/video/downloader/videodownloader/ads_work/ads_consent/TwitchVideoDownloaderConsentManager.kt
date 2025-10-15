package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ads_work.ads_consent

import android.annotation.SuppressLint
import android.app.Activity
import android.provider.Settings
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation.ConsentStatus
import com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class TwitchVideoDownloaderConsentManager(private val activity: Activity) {

    private val twitchConsentInformation by lazy { UserMessagingPlatform.getConsentInformation(activity) }

    private var twitchOnConsentResponse: TwitchVideoDownloaderConsentResponse? = null


    fun initTwitchVideoDownloaderDebugConsent(deviceId: String = getTwitchVideoDownloaderDeviceId(), twitchOnConsentResponse: TwitchVideoDownloaderConsentResponse) {
        this.twitchOnConsentResponse = twitchOnConsentResponse
        val debugSettings = ConsentDebugSettings.Builder(activity)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId(deviceId)
            .build()

        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
            .build()
        requestTwitchVideoDownloaderConsent(params)
    }

    fun initTwitchVideoDownloaderReleaseConsent(twitchOnConsentResponse: TwitchVideoDownloaderConsentResponse) {
        this.twitchOnConsentResponse = twitchOnConsentResponse
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()
        requestTwitchVideoDownloaderConsent(params)
    }

    private fun requestTwitchVideoDownloaderConsent(params: ConsentRequestParameters) {
        twitchConsentInformation.requestConsentInfoUpdate(activity, params, {

            if (twitchConsentInformation.isConsentFormAvailable && twitchConsentInformation.consentStatus == ConsentStatus.REQUIRED) {
                Log.i("DEV TAG", "initConsent: Available & Required")
                loadPintTwitchDownloaderConsentForm()
            } else {
                Log.i("DEV TAG", "initConsent: Neither Available nor Required")
                twitchOnConsentResponse?.onConsentResponse()
            }
        }, { error ->
            // Handle the error.
            Log.e("DEV TAG", "requestConsent: $error")
            twitchOnConsentResponse?.onConsentResponse(error.message)
        })
    }

    private fun loadPintTwitchDownloaderConsentForm() {
        //  Must be called on the main thread.
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
            formError?.let {
                twitchOnConsentResponse?.onConsentResponse(it.message)
            } ?: run {
                twitchOnConsentResponse?.onConsentResponse()
               // checkForPrivacyOptions()
            }
        }
    }

    private fun checkForPrivacyOptions() {
        val isRequired = twitchConsentInformation?.privacyOptionsRequirementStatus == PrivacyOptionsRequirementStatus.REQUIRED
        twitchOnConsentResponse?.onConsentPolicyRequired(isRequired)
    }

    @SuppressLint("HardwareIds")
    private fun getTwitchVideoDownloaderDeviceId(): String {
        return try {
            val androidId = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
            val digest = MessageDigest.getInstance("MD5")
            digest.update(androidId.toByteArray())
            val messageDigest = digest.digest()
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(
                java.lang.String.format("%02X", 0xFF and messageDigest[i].toInt())
            )
            hexString.toString().uppercase()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            ""
        }
    }
}