package com.andreisemyonov.exchangerates.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.andreisemyonov.exchangerates.R
import com.andreisemyonov.exchangerates.adapter.CurrencyAdapter
import com.andreisemyonov.exchangerates.databinding.FavoritesCurrenciesFragmentBinding
import com.andreisemyonov.exchangerates.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FavoritesCurrenciesFragment: Fragment() {

    private lateinit var binding: FavoritesCurrenciesFragmentBinding

    private val viewModel by viewModels<CurrencyViewModel>()

    private lateinit var adapter: CurrencyAdapter

    private lateinit var sortDialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FavoritesCurrenciesFragmentBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        adapter = CurrencyAdapter(requireActivity(), viewModel, true)

        binding.recyclerView.adapter = adapter

        binding.favoritesFragmentSelectedCurrency.selectItemByIndex(viewModel.favoritesFragmentBaseCurrencyItemIndex)

        sortDialog = Dialog(requireActivity())

        if(viewModel.favoriteCurrenciesList.isNotEmpty()){
            adapter.setCurrencyList(viewModel.favoriteCurrenciesList)
        }

        setOnClickListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collect { event ->
                when(event) {

                    is CurrencyViewModel.CurrencyEvent.LoadingFavoritesCurrencies -> {
                        binding.progressBar.isVisible = true
                    }
                    is CurrencyViewModel.CurrencyEvent.FavoriteCurrenciesListLoaded -> {
                        binding.progressBar.isVisible = false
                        adapter.setCurrencyList(event.favoriteCurrenciesList)
                    }
                    is CurrencyViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                    }
                    is CurrencyViewModel.CurrencyEvent.FavoriteCurrenciesListChanged -> {
                        adapter.notifyDataSetChanged()
                    }
                    is CurrencyViewModel.CurrencyEvent.FavoriteCurrenciesListIsEmpty -> {
                        binding.progressBar.isVisible = false
                        adapter.currencyList.clear()
                        adapter.notifyDataSetChanged()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setOnClickListeners(){
        binding.getExchangeRates.setOnClickListener {
            viewModel.loadFavoriteCurrenciesList(viewModel.favoritesFragmentSelectedBaseCurrency)
        }

        binding.favoritesFragmentSelectedCurrency.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            viewModel.loadFavoriteCurrenciesList(newText)
            viewModel.favoritesFragmentSelectedBaseCurrency = newText
            viewModel.favoritesFragmentBaseCurrencyItemIndex = newIndex
        }

        binding.sort.setOnClickListener {
            showSortDialog()
        }
    }

    private fun showSortDialog(){
        sortDialog.setContentView(R.layout.sort_menu_layout)
        sortDialog.setCancelable(true)

        val alphabeticalSortByAscending =
            sortDialog.findViewById<TextView>(R.id.alphabetical_sort_by_ascending)
        alphabeticalSortByAscending.setOnClickListener {
            sortDialog.cancel()
            viewModel.favoriteCurrenciesList.sortBy {
                it.currencyCode
            }
            adapter.setCurrencyList(viewModel.favoriteCurrenciesList)
        }

        val alphabeticalSortByDescending =
            sortDialog.findViewById<TextView>(R.id.alphabetical_sort_by_descending)
        alphabeticalSortByDescending.setOnClickListener {
            sortDialog.cancel()
            viewModel.favoriteCurrenciesList.sortByDescending{
                it.currencyCode
            }
            adapter.setCurrencyList(viewModel.favoriteCurrenciesList)
        }

        val valueSortByAscending =
            sortDialog.findViewById<TextView>(R.id.value_sort_by_ascending)
        valueSortByAscending.setOnClickListener {
            sortDialog.cancel()
            viewModel.favoriteCurrenciesList.sortBy {
                it.currencyValue
            }
            adapter.setCurrencyList(viewModel.favoriteCurrenciesList)
        }

        val valueSortByDescending =
            sortDialog.findViewById<TextView>(R.id.value_sort_by_descending)
        valueSortByDescending.setOnClickListener {
            sortDialog.cancel()
            viewModel.favoriteCurrenciesList.sortByDescending {
                it.currencyValue
            }
            adapter.setCurrencyList(viewModel.favoriteCurrenciesList)
        }

        sortDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }
}



