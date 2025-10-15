package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.ads_work.ads_consent

interface TwitchVideoDownloaderConsentResponse {
    fun onConsentResponse(errorMessage: String? = null)
    fun onConsentPolicyRequired(isRequired: Boolean)
}