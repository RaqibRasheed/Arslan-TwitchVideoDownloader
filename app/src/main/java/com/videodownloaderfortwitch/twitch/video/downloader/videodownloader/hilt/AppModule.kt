package com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.hilt

import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.repository.VideoRepository
import com.videodownloaderfortwitch.twitch.video.downloader.videodownloader.repository.VideoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindVideoRepository(impl: VideoRepositoryImpl): VideoRepository

}