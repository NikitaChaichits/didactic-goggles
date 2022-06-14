package com.example.vpn.ui.connection.alert.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vpn.databinding.FixedIssuesItemBinding

class IssuesResultAdapter : ListAdapter<String, IssuesResultAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemFixedIssuesBinding = FixedIssuesItemBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(itemFixedIssuesBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(private val binding: FixedIssuesItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) = with(itemView) {
            binding.tvFixedIssues.text = item
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}