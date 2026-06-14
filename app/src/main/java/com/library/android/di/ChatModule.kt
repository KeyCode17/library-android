package com.library.android.di

import com.library.android.data.remote.OkHttpChatSocket
import com.library.android.domain.chat.ChatSocket
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {

    @Binds
    @Singleton
    abstract fun bindChatSocket(impl: OkHttpChatSocket): ChatSocket
}
