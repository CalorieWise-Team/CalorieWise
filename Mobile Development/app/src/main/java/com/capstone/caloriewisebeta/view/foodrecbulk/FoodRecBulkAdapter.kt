package com.capstone.caloriewisebeta.view.foodrecbulk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.caloriewisebeta.data.response.BulkItem
import com.capstone.caloriewisebeta.databinding.ItemRowFoodRecBinding

class FoodRecBulkAdapter : ListAdapter<BulkItem, FoodRecBulkAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowFoodRecBinding.inflate(
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

    inner class ListViewHolder(private var binding: ItemRowFoodRecBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BulkItem) {
            binding.apply {
                tvFoodTitle.text = item?.foodName
                tvFoodContent.text = item?.description
                Glide.with(itemView.context)
                    .load(item?.imageUrl)
                    .into(imgItemFood)

                itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(item)
                }
            }
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(items: BulkItem?)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BulkItem>() {
            override fun areItemsTheSame(oldItem: BulkItem, newItem: BulkItem): Boolean {
                return oldItem.bulkingItemId == newItem.bulkingItemId
            }

            override fun areContentsTheSame(oldItem: BulkItem, newItem: BulkItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}