package com.capstone.caloriewisebeta.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.repository.SearchRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: SearchRepository,
    private val pref: UserPreference
) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun saveLoginState(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
            pref.login()
        }
    }
}