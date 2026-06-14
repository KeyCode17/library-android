package com.library.android.di

import com.library.android.data.device.AndroidWifiProvisioner
import com.library.android.data.device.ZxingQrEncoder
import com.library.android.domain.device.QrEncoder
import com.library.android.domain.device.WifiProvisioner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceModule {

    @Binds
    @Singleton
    abstract fun bindQrEncoder(impl: ZxingQrEncoder): QrEncoder

    @Binds
    @Singleton
    abstract fun bindWifiProvisioner(impl: AndroidWifiProvisioner): WifiProvisioner
}
