package com.andreisemyonov.exchangerates.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteCurrency::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){
    abstract fun dbCurrencyDao(): CurrencyDao
}