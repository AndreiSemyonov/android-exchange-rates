package com.andreisemyonov.exchangerates.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: FavoriteCurrency)

    @Delete
    suspend fun delete(currency: FavoriteCurrency)

    @Query("SELECT * FROM currencies ORDER BY currencyName")
    fun getAll(): Flow<MutableList<FavoriteCurrency>>

    @Query("SELECT EXISTS (SELECT 1 FROM currencies WHERE currencyName=:currencyName)")
    suspend fun isFavorite(currencyName: String?): String
}