package com.capstone.caloriewisebeta.view.signup

import androidx.lifecycle.ViewModel
import com.capstone.caloriewisebeta.data.repository.SearchRepository

class SignupViewModel(private val repository: SearchRepository): ViewModel() {

    fun register(email: String, password: String, name: String, gender: String) = repository.register(email, password, name, gender)

}