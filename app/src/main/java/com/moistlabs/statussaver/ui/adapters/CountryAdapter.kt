package com.moistlabs.statussaver.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moistlabs.statussaver.databinding.CountryListItemBinding
import com.moistlabs.statussaver.model.Country

class CountryAdapter(
    private val onClick: (country: Country) -> Unit,
) : ListAdapter<Country, CountryAdapter.CountryViewHolder>(CountryDiffCallback()) {

    inner class CountryViewHolder(
        val binding: CountryListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(country: Country) {
            binding.run {
                tvCountryCode.text = country.isoCode
                tvCountryName.text = country.countryName
                root.setOnClickListener {
                    onClick(country)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = CountryListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun filter(query: String) {
        val filteredList = Country.countries.filter {
            it.countryName.startsWith(query, ignoreCase = true) or it.isoCode.contains(query)
        }
        submitList(filteredList)
    }

    class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.countryName == newItem.countryName
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }
    }
}