package com.library.android.di

import com.library.android.data.remote.AuthRemoteRepository
import com.library.android.data.remote.CatalogRemoteRepository
import com.library.android.data.remote.ChatRemoteRepository
import com.library.android.data.remote.LoanRemoteRepository
import com.library.android.data.remote.NotificationRemoteRepository
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.repo.ChatRepository
import com.library.android.domain.repo.LoanRepository
import com.library.android.domain.repo.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(impl: CatalogRemoteRepository): CatalogRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRemoteRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLoanRepository(impl: LoanRemoteRepository): LoanRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRemoteRepository): ChatRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRemoteRepository,
    ): NotificationRepository
}
