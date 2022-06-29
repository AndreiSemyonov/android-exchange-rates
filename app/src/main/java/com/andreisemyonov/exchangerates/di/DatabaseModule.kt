package com.andreisemyonov.exchangerates.di

import android.content.Context
import androidx.room.Room
import com.andreisemyonov.exchangerates.R
import com.andreisemyonov.exchangerates.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            context.getString(R.string.database)
        ).build()
    }
}