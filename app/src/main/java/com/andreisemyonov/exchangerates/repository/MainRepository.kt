package com.andreisemyonov.exchangerates.repository

import com.andreisemyonov.exchangerates.domain.Currency
import com.andreisemyonov.exchangerates.network.ApiService
import com.andreisemyonov.exchangerates.util.Resource
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MainRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getRates(base: String = "", symbols: String = "", favorites: Boolean = false): Resource<String> {

        return if(!favorites){
            val response = api.getRates(base)
            val result = response.body().toString()

            if(response.isSuccessful) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        }
        else{
            val response = api.getRatesFromFavorites(base, symbols)
            val result = response.body().toString()

            if(response.isSuccessful) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        }
    }

    fun parseJsonObject(rawJson: String): MutableList<Currency> {

        val currencyList: ArrayList<Currency> = ArrayList()
        val parser = JsonParser()
        val rootObj = parser.parse(rawJson).asJsonObject
        val rootKeys = rootObj.keySet()
        var currencyObj = JsonObject()
        var currencyKeys: Set<String> = HashSet()
        for (key in rootKeys) {
            if (key == "rates") {
                currencyObj = rootObj.get(key).asJsonObject
                currencyKeys = currencyObj.keySet()
            }
        }

        for (key2 in currencyKeys) {
            currencyList.add(Currency(key2, currencyObj.get(key2).asFloat))
        }
        return currencyList
    }
}