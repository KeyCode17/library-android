package com.library.android.di

import com.library.android.data.remote.CatalogRemoteRepository
import com.library.android.domain.repo.CatalogRepository
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
}
