package com.andreisemyonov.exchangerates.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.andreisemyonov.exchangerates.R
import com.andreisemyonov.exchangerates.adapter.CurrencyAdapter
import com.andreisemyonov.exchangerates.databinding.AllCurrenciesFragmentBinding
import com.andreisemyonov.exchangerates.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AllCurrenciesFragment: Fragment() {

    private lateinit var binding: AllCurrenciesFragmentBinding

    private val viewModel by viewModels<CurrencyViewModel>()

    private lateinit var adapter: CurrencyAdapter

    private lateinit var sortDialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AllCurrenciesFragmentBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        adapter = CurrencyAdapter(requireActivity(), viewModel)

        binding.recyclerView.adapter = adapter

        sortDialog = Dialog(requireActivity())

        if(viewModel.allCurrenciesList.isNotEmpty())
            adapter.setCurrencyList(viewModel.allCurrenciesList)

        binding.allCurrenciesFragmentSelectedCurrency.selectItemByIndex(viewModel.allCurrenciesFragmentBaseCurrencyItemIndex)

        setOnClickListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collect { event ->
                when(event) {

                    is CurrencyViewModel.CurrencyEvent.LoadingAllCurrencies -> {
                        binding.progressBar.isVisible = true
                    }
                    is CurrencyViewModel.CurrencyEvent.CurrenciesListLoaded -> {
                        binding.progressBar.isVisible = false
                        adapter.setCurrencyList(event.allCurrenciesList)
                    }
                    is CurrencyViewModel.CurrencyEvent.Failure -> {
                        Toast.makeText(requireActivity(), event.errorText, Toast.LENGTH_LONG).show()
                        binding.progressBar.isVisible = false
                    }
                    is CurrencyViewModel.CurrencyEvent.FavoriteCurrenciesListChanged -> {
                        adapter.notifyDataSetChanged()
                    }
                    is CurrencyViewModel.CurrencyEvent.FavoriteCurrenciesListIsEmpty -> {
                        adapter.notifyDataSetChanged()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setOnClickListeners(){

        binding.getExchangeRates.setOnClickListener {
            viewModel.loadCurrencyList(viewModel.allCurrenciesFragmentSelectedBaseCurrency)
        }

        binding.allCurrenciesFragmentSelectedCurrency.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            viewModel.loadCurrencyList(newText)
            viewModel.allCurrenciesFragmentSelectedBaseCurrency = newText
            viewModel.allCurrenciesFragmentBaseCurrencyItemIndex = newIndex
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
            viewModel.allCurrenciesList.sortBy {
                it.currencyCode
            }
            adapter.setCurrencyList(viewModel.allCurrenciesList)
        }

        val alphabeticalSortByDescending =
            sortDialog.findViewById<TextView>(R.id.alphabetical_sort_by_descending)
        alphabeticalSortByDescending.setOnClickListener {
            sortDialog.cancel()
            viewModel.allCurrenciesList.sortByDescending{
                it.currencyCode
            }
            adapter.setCurrencyList(viewModel.allCurrenciesList)
        }

        val valueSortByAscending =
            sortDialog.findViewById<TextView>(R.id.value_sort_by_ascending)
        valueSortByAscending.setOnClickListener {
            sortDialog.cancel()
            viewModel.allCurrenciesList.sortBy {
                it.currencyValue
            }
            adapter.setCurrencyList(viewModel.allCurrenciesList)
        }

        val valueSortByDescending =
            sortDialog.findViewById<TextView>(R.id.value_sort_by_descending)
        valueSortByDescending.setOnClickListener {
            sortDialog.cancel()
            viewModel.allCurrenciesList.sortByDescending {
                it.currencyValue
            }
            adapter.setCurrencyList(viewModel.allCurrenciesList)
        }
        sortDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }
}



