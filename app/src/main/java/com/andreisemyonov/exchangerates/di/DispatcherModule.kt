package com.andreisemyonov.exchangerates.di

import com.andreisemyonov.exchangerates.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Singleton
    @Provides
    fun provideDispatcher(): DispatcherProvider = object : DispatcherProvider {
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
    }
}


