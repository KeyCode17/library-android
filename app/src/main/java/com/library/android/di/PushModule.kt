package com.library.android.di

import com.library.android.data.push.FirebasePushTokenProvider
import com.library.android.domain.push.PushTokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PushModule {

    @Binds
    @Singleton
    abstract fun bindPushTokenProvider(impl: FirebasePushTokenProvider): PushTokenProvider
}
