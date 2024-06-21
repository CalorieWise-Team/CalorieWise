package com.capstone.caloriewisebeta.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.repository.SearchRepository
import com.capstone.caloriewisebeta.data.response.ProfileUpdateResponse
import com.capstone.caloriewisebeta.data.result.Result
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreference: UserPreference,
    private val Repository: SearchRepository
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            userPreference.logout()
        }
    }

    fun updateProfile(name: String, gender: String, email: String): LiveData<Result<ProfileUpdateResponse>> {
        return Repository.updateProfile(name, gender, email)
    }
}
