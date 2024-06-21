// Injection.kt
package com.capstone.caloriewisebeta.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.caloriewisebeta.data.api.ApiConfig
import com.capstone.caloriewisebeta.data.api.ApiService
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.repository.SearchRepository

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

object Injection {
    fun provideRepository(context: Context): SearchRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return SearchRepository.getInstance(apiService, pref)
    }
}
