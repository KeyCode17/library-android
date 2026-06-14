package com.library.android.di

import com.library.android.data.scan.MlKitBarcodeScanner
import com.library.android.domain.scan.BarcodeScanner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScanModule {

    @Binds
    @Singleton
    abstract fun bindBarcodeScanner(impl: MlKitBarcodeScanner): BarcodeScanner
}
