package com.andreisemyonov.exchangerates.di

import com.andreisemyonov.exchangerates.database.AppDatabase
import com.andreisemyonov.exchangerates.network.ApiService
import com.andreisemyonov.exchangerates.repository.DatabaseRepository
import com.andreisemyonov.exchangerates.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(api: ApiService): MainRepository = MainRepository(api)

    @Singleton
    @Provides
    fun provideDatabaseRepository(db: AppDatabase): DatabaseRepository = DatabaseRepository(db)
}