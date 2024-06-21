package com.capstone.caloriewisebeta.view.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.caloriewisebeta.data.response.NotesItem
import com.capstone.caloriewisebeta.databinding.ItemRowNoteBinding
import com.capstone.caloriewisebeta.helper.withDateFormat

class NoteAdapter : ListAdapter<NotesItem, NoteAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowNoteBinding.inflate(
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

    inner class ListViewHolder(private var binding: ItemRowNoteBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotesItem?) {
            binding.apply {
                tvNoteTitle.text = item?.title
                tvNoteContent.text = item?.content
                storyDate.text = item?.createdAt?.withDateFormat()
                Glide.with(itemView.context)
                    .load(item?.imageUrl)
                    .into(imgItemNote)

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
        fun onItemClicked(items: NotesItem?)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NotesItem>() {
            override fun areItemsTheSame(oldItem: NotesItem, newItem: NotesItem): Boolean {
                return oldItem.noteId == newItem.noteId
            }

            override fun areContentsTheSame(oldItem: NotesItem, newItem: NotesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}