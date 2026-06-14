package com.library.android.di

import com.library.android.data.recommend.UniffiRecommender
import com.library.android.domain.recommend.Recommender
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecommenderModule {

    @Binds
    @Singleton
    abstract fun bindRecommender(impl: UniffiRecommender): Recommender
}
