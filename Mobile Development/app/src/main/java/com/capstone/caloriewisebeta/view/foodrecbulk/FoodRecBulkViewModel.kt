package com.capstone.caloriewisebeta.view.foodrecbulk

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.caloriewisebeta.data.repository.SearchRepository
import com.capstone.caloriewisebeta.data.response.BulkItem
import com.capstone.caloriewisebeta.data.result.Result

class FoodRecBulkViewModel(
    private val repository: SearchRepository,
) : ViewModel() {

    fun getBulk(): LiveData<Result<List<BulkItem>>> {
        return repository.getBulk()
    }
}