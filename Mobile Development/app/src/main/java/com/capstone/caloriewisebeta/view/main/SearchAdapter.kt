package com.capstone.caloriewisebeta.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.caloriewisebeta.data.response.SearchItem
import com.capstone.caloriewisebeta.databinding.ItemRowSearchBinding

class SearchAdapter : ListAdapter<SearchItem, SearchAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null
    var fullList: List<SearchItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ListViewHolder(private var binding: ItemRowSearchBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchItem?) {
            binding.apply {
                tvSearchTitle.text = item?.name ?: "N/A"
                Glide.with(itemView.context)
                    .load(item?.imageUrl)
                    .into(imgItemSearch)

                itemView.setOnClickListener {
                    onItemClickCallback?.onItemClicked(item)
                }
            }
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullList  // Tampilkan seluruh list jika query kosong
        } else {
            fullList.filter { it.name?.contains(query, ignoreCase = true) == true }  // Filter list berdasarkan query
        }
        submitList(filteredList)  // Perbarui list yang ditampilkan
    }

    interface OnItemClickCallback {
        fun onItemClicked(items: SearchItem?)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchItem>() {
            override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
                return oldItem.foodId == newItem.foodId
            }

            override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
