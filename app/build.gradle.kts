plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.devtools.ksp")
    id ("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.videodownloaderfortwitch.twitch.video.downloader.videodownloader"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.videodownloaderfortwitch.twitch.video.downloader.videodownloader"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //ViewModels Delegation
    implementation (libs.androidx.activity.ktx)

    // ssp,sdp
    implementation (libs.ssp.android)
    implementation (libs.sdp.android)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //lotti
    implementation (libs.lottie)

    // Hilt
    implementation(libs.hilt.android)
    ksp (libs.hilt.android.compiler)

    //Glide
    implementation(libs.glide)
    ksp (libs.compiler)

    // Shimmer
    implementation (libs.shimmer)

    // Exoplayer
    implementation (libs.androidx.media3.exoplayer)
    implementation (libs.androidx.media3.ui)

    //OkHttp
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")


    // Google Billing
    implementation(libs.billing)
}