package com.andreisemyonov.exchangerates.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
class FavoriteCurrency(
    @PrimaryKey @ColumnInfo(name = "currencyName") val currencyName: String
)