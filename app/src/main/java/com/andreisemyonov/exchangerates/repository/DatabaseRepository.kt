package com.andreisemyonov.exchangerates.repository

import com.andreisemyonov.exchangerates.database.AppDatabase
import com.andreisemyonov.exchangerates.database.FavoriteCurrency
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val appDatabase: AppDatabase
)  {

    suspend fun addToFavorites(currency: FavoriteCurrency){
        appDatabase.dbCurrencyDao().insert(currency)
    }

    suspend fun deleteFromFavorites(currency: FavoriteCurrency){
        appDatabase.dbCurrencyDao().delete(currency)
    }

    fun getFavorites(): Flow<MutableList<FavoriteCurrency>> {
        return appDatabase.dbCurrencyDao().getAll()
    }

    suspend fun isFavorite(isFavorite: String): String {
        return appDatabase.dbCurrencyDao().isFavorite(isFavorite)
    }
}


