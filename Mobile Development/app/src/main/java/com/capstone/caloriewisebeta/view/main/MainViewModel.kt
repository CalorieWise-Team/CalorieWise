package com.capstone.caloriewisebeta.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.repository.SearchRepository
import com.capstone.caloriewisebeta.data.response.SearchItem
import com.capstone.caloriewisebeta.data.result.Result
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SearchRepository,
    private val pref: UserPreference
) : ViewModel() {

    fun getAllSearch(): LiveData<Result <List<SearchItem>>> {
        return repository.getAllSearch()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}