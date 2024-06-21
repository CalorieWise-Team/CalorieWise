package com.capstone.caloriewisebeta.view.foodreccut

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.caloriewisebeta.data.response.Cut
import com.capstone.caloriewisebeta.databinding.ItemRowFoodRecCutBinding

class FoodRecCutAdapter : ListAdapter<Cut, FoodRecCutAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowFoodRecCutBinding.inflate(
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

    inner class ListViewHolder(private var binding: ItemRowFoodRecCutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cut) {
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
        fun onItemClicked(items: Cut?)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Cut>() {
            override fun areItemsTheSame(oldItem: Cut, newItem: Cut): Boolean {
                return oldItem.cuttingItemId == newItem.cuttingItemId
            }

            override fun areContentsTheSame(oldItem: Cut, newItem: Cut): Boolean {
                return oldItem == newItem
            }
        }
    }
}