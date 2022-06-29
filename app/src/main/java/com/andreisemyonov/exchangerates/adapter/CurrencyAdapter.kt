package com.andreisemyonov.exchangerates.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.andreisemyonov.exchangerates.R
import com.andreisemyonov.exchangerates.database.FavoriteCurrency
import com.andreisemyonov.exchangerates.databinding.CurrencyItemBinding
import com.andreisemyonov.exchangerates.domain.Currency
import com.andreisemyonov.exchangerates.viewmodel.CurrencyViewModel
import kotlinx.coroutines.launch
import java.math.RoundingMode

class CurrencyAdapter (
    private val context: Context,
    private val viewModel: CurrencyViewModel,
    private var isFavoritesFragment: Boolean = false
    ): RecyclerView.Adapter<CurrencyAdapter.CurrencyItemViewHolder>() {

    var currencyList: ArrayList<Currency> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.currency_item, parent, false)
        return CurrencyItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyItemViewHolder, position: Int) {

        with(holder) {
            binding.tvCurrencyCode.text = currencyList[position].currencyCode
            binding.tvCurrencyValue.text =
                currencyList[position].currencyValue.toBigDecimal().setScale(
                    2, RoundingMode.CEILING
                ).toDouble().toString()

            if(!isFavoritesFragment){

                viewModel.viewModelScope.launch{
                    val fav = viewModel.databaseRepository.isFavorite(currencyList[position].currencyCode)
                    val i = fav.toInt()
                    if (i == 1) {
                        favoritesFlag = true
                        binding.addToFavorites.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                    }
                    else {
                        favoritesFlag = false
                        binding.addToFavorites.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_border))
                    }
                }

                binding.addToFavorites.setOnClickListener {

                    val currency = FavoriteCurrency(
                        currencyList[position].currencyCode
                    )

                    if (!favoritesFlag) {
                        favoritesFlag = true
                        viewModel.addCurrencyToFavorites(currency)
                        binding.addToFavorites.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                    }else{
                        favoritesFlag = false
                        viewModel.deleteCurrencyFromFavorites(currency)
                        binding.addToFavorites.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_border))
                    }
                }
            }
            else{
                binding.addToFavorites.isVisible = false
                binding.deleteFromFavorites.isVisible = true

                binding.deleteFromFavorites.setOnClickListener {

                    val currency = FavoriteCurrency(
                        currencyList[position].currencyCode
                    )
                    viewModel.deleteCurrencyFromFavorites(currency)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return currencyList.size
    }

    fun setCurrencyList(list: MutableList<Currency>) {
        currencyList.clear()
        currencyList.addAll(list)
        this.notifyDataSetChanged()
    }

    inner class CurrencyItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding = CurrencyItemBinding.bind(itemView)
        var favoritesFlag = false
    }
}