package com.capstone.caloriewisebeta.view.foodreccut

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.caloriewisebeta.data.repository.SearchRepository
import com.capstone.caloriewisebeta.data.response.CuttingItemDetailsItem
import com.capstone.caloriewisebeta.data.result.Result

class FoodRecCutViewModel(
    private val repository: SearchRepository,
) : ViewModel() {

    fun getCut(): LiveData<Result<List<CuttingItemDetailsItem>>>{
        return repository.getCut()
    }

}