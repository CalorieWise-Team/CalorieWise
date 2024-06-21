package com.capstone.caloriewisebeta.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.repository.SearchRepository
import com.capstone.caloriewisebeta.data.response.NotesItem
import com.capstone.caloriewisebeta.data.response.SearchItem
import com.capstone.caloriewisebeta.data.result.Result

class SearchViewModel(
    private val repository: SearchRepository,
    private val pref: UserPreference
) : ViewModel() {

    fun getAllSearch(): LiveData<Result<List<SearchItem>>> {
        return repository.getAllSearch()
    }

}