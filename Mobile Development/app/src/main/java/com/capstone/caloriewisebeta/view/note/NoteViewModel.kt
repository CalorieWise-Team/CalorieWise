package com.capstone.caloriewisebeta.view.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.repository.SearchRepository
import com.capstone.caloriewisebeta.data.response.NotesItem
import com.capstone.caloriewisebeta.data.result.Result

class NoteViewModel(
    private val repository: SearchRepository,
    private val pref: UserPreference
) : ViewModel() {

    fun getAllNote(): LiveData<Result<List<NotesItem>>> {
        return repository.getAllNote()
    }

}