package com.cyberself.vpn.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyberself.vpn.databinding.ItemCountryBinding
import com.cyberself.vpn.domain.model.Country

class BottomSheetAdapter(
    private val chooseCountry: (itemPosition: Int) -> Unit
) : ListAdapter<Country, BottomSheetAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemCountryBinding = ItemCountryBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(itemCountryBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ItemViewHolder(private val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Country, position: Int) = with(itemView) {
            binding.tvCountry.text = item.fullName
            binding.ivFlag.setImageResource(item.flag)
            if (item.isBestChoice)
                binding.tvBestChoice.visibility = View.VISIBLE
            else
                binding.tvBestChoice.visibility = View.GONE

            if (item.isChosen)
                binding.ivTick.visibility = View.VISIBLE
            else
                binding.ivTick.visibility = View.GONE

            setOnClickListener { chooseCountry(position) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean =
            oldItem.shortName == newItem.shortName

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean =
            oldItem == newItem
    }
}