package com.capstone.caloriewisebeta.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.repository.SearchRepository
import com.capstone.caloriewisebeta.di.Injection
import com.capstone.caloriewisebeta.view.foodrecbulk.FoodRecBulkViewModel
import com.capstone.caloriewisebeta.view.foodreccut.FoodRecCutViewModel
import com.capstone.caloriewisebeta.view.login.LoginViewModel
import com.capstone.caloriewisebeta.view.main.MainViewModel
import com.capstone.caloriewisebeta.view.main.SearchViewModel
import com.capstone.caloriewisebeta.view.note.AddNoteViewModel
import com.capstone.caloriewisebeta.view.note.NoteViewModel
import com.capstone.caloriewisebeta.view.profile.ProfileViewModel
import com.capstone.caloriewisebeta.view.signup.SignupViewModel

class ViewModelFactory private constructor(
    private val repository: SearchRepository,
    private val pref: UserPreference
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
        } else if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository, pref) as T
        } else if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(repository, pref) as T
        } else if (modelClass.isAssignableFrom(AddNoteViewModel::class.java)) {
            return AddNoteViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(pref, repository) as T
        } else if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(repository, pref) as T
        } else if (modelClass.isAssignableFrom(FoodRecBulkViewModel::class.java)) {
            return FoodRecBulkViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(FoodRecCutViewModel::class.java)) {
            return FoodRecCutViewModel(repository) as T
        }


        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context, pref: UserPreference): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context), pref)
            }.also { instance = it }
    }
}