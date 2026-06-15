package com.library.android.di

import android.content.Context
import androidx.room.Room
import com.library.android.data.local.BookDao
import com.library.android.data.local.CatalogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Provides the Room catalog database (ADR 0002) and its DAOs. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCatalogDatabase(@ApplicationContext context: Context): CatalogDatabase =
        Room.databaseBuilder(context, CatalogDatabase::class.java, "catalog.db").build()

    @Provides
    fun provideBookDao(database: CatalogDatabase): BookDao = database.bookDao()
}
