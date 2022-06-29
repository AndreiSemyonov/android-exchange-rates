package com.andreisemyonov.exchangerates.network

import com.andreisemyonov.exchangerates.constants.Constants
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @GET("/exchangerates_data/latest")
    @Headers("apikey: ${Constants.API_KEY}")
    suspend fun getRates(
        @Query("base") base: String
    ): Response<JsonObject>

    @GET("/exchangerates_data/latest")
    @Headers("apikey: ${Constants.API_KEY}")
    suspend fun getRatesFromFavorites(
        @Query("symbols") symbols: String,
        @Query("base") base: String
    ): Response<JsonObject>
}