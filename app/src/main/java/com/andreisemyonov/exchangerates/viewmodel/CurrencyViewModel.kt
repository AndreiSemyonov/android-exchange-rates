package com.andreisemyonov.exchangerates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreisemyonov.exchangerates.constants.Constants
import com.andreisemyonov.exchangerates.database.FavoriteCurrency
import com.andreisemyonov.exchangerates.domain.Currency
import com.andreisemyonov.exchangerates.repository.DatabaseRepository
import com.andreisemyonov.exchangerates.repository.MainRepository
import com.andreisemyonov.exchangerates.util.DispatcherProvider
import com.andreisemyonov.exchangerates.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    val databaseRepository: DatabaseRepository,
    private val dispatcher: DispatcherProvider
): ViewModel() {

    private var _mutableStateFlow = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    var stateFlow: StateFlow<CurrencyEvent> = _mutableStateFlow

    sealed class CurrencyEvent {
        class CurrenciesListLoaded(val allCurrenciesList: MutableList<Currency>): CurrencyEvent()
        class FavoriteCurrenciesListLoaded(val favoriteCurrenciesList: MutableList<Currency>): CurrencyEvent()
        object LoadingAllCurrencies: CurrencyEvent()
        object LoadingFavoritesCurrencies: CurrencyEvent()
        object FavoriteCurrenciesListChanged: CurrencyEvent()
        object FavoriteCurrenciesListIsEmpty: CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        object Empty: CurrencyEvent()
    }

    var allCurrenciesList: MutableList<Currency> = ArrayList()
    var favoriteCurrenciesList: MutableList<Currency> = ArrayList()
    var allCurrenciesFragmentSelectedBaseCurrency = ""
    var favoritesFragmentSelectedBaseCurrency = ""
    var allCurrenciesFragmentBaseCurrencyItemIndex = Constants.defaultBaseCurrencyItemIndex
    var favoritesFragmentBaseCurrencyItemIndex = Constants.defaultBaseCurrencyItemIndex

    init {
        loadCurrencyList(allCurrenciesFragmentSelectedBaseCurrency)
        loadFavoriteCurrenciesList(favoritesFragmentSelectedBaseCurrency)
    }

    fun loadCurrencyList(base: String) {

        viewModelScope.launch(dispatcher.io) {

            _mutableStateFlow.value = CurrencyEvent.LoadingAllCurrencies

            when(val currencyResponse = mainRepository.getRates(base)) {

                is Resource.Error -> {
                    _mutableStateFlow.value = CurrencyEvent.Failure(currencyResponse.message!!)
                }
                is Resource.Success -> {
                    allCurrenciesList = mainRepository.parseJsonObject(currencyResponse.data.toString())

                    if(allCurrenciesList.isNotEmpty())
                        _mutableStateFlow.value = CurrencyEvent.CurrenciesListLoaded(allCurrenciesList)
                }
            }
        }
    }

    fun loadFavoriteCurrenciesList(base: String){

        viewModelScope.launch(dispatcher.io) {

            _mutableStateFlow.value = CurrencyEvent.LoadingFavoritesCurrencies

            databaseRepository.getFavorites().collect { favoritesList ->

                _mutableStateFlow.value = CurrencyEvent.FavoriteCurrenciesListChanged

                if(favoritesList.isNotEmpty()){

                    val symbols = favoritesList.joinToString {
                        it.currencyName
                    }

                    when(val favoritesCurrencyResponse =
                        mainRepository.getRates(symbols, base, true)) {
                        is Resource.Error -> {
                            _mutableStateFlow.value = CurrencyEvent.Failure(favoritesCurrencyResponse.message!!)
                        }
                        is Resource.Success -> {

                            favoriteCurrenciesList =
                                mainRepository.parseJsonObject(
                                    favoritesCurrencyResponse.data.toString()
                                )

                            if(favoriteCurrenciesList.isNotEmpty())
                                _mutableStateFlow.value =
                                    CurrencyEvent.FavoriteCurrenciesListLoaded(favoriteCurrenciesList)
                        }
                    }
                }
                else _mutableStateFlow.value = CurrencyEvent.FavoriteCurrenciesListIsEmpty
            }
        }
    }

    fun addCurrencyToFavorites(currency: FavoriteCurrency){
        viewModelScope.launch(dispatcher.io) {
            databaseRepository.addToFavorites(currency)
            loadFavoriteCurrenciesList(favoritesFragmentSelectedBaseCurrency)
        }
    }

    fun deleteCurrencyFromFavorites(currency: FavoriteCurrency){
        viewModelScope.launch(dispatcher.io) {
            databaseRepository.deleteFromFavorites(currency)
            loadFavoriteCurrenciesList(favoritesFragmentSelectedBaseCurrency)
        }
    }
}