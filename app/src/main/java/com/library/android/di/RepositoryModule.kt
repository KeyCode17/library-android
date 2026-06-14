package com.library.android.di

import com.library.android.data.remote.AuthRemoteRepository
import com.library.android.data.remote.CatalogRemoteRepository
import com.library.android.data.remote.LoanRemoteRepository
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.repo.LoanRepository
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
}
