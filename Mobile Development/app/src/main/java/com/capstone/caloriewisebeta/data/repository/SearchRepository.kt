package com.capstone.caloriewisebeta.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.caloriewisebeta.data.api.ApiConfig
import com.capstone.caloriewisebeta.data.api.ApiService
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.response.BulkItem
import com.capstone.caloriewisebeta.data.response.CutDetailResponse
import com.capstone.caloriewisebeta.data.response.CuttingItemDetailsItem
import com.capstone.caloriewisebeta.data.response.FoodRecBulkResponse
import com.capstone.caloriewisebeta.data.response.GeneralResponse
import com.capstone.caloriewisebeta.data.response.LoginResponse
import com.capstone.caloriewisebeta.data.response.LoginResult
import com.capstone.caloriewisebeta.data.response.NoteResponse
import com.capstone.caloriewisebeta.data.response.NotesItem
import com.capstone.caloriewisebeta.data.response.ProfileUpdateResponse
import com.capstone.caloriewisebeta.data.response.RegisterResponse
import com.capstone.caloriewisebeta.data.response.SearchItem
import com.capstone.caloriewisebeta.data.response.SearchResponse
import com.capstone.caloriewisebeta.data.result.Result
import com.capstone.caloriewisebeta.helper.reduceFileImage
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class SearchRepository private constructor(
    private var apiService: ApiService,
    private val userPreference: UserPreference
) {

    private var token: String? = null

    private suspend fun getToken(): String? = token ?: runBlocking {
        userPreference.getToken().first()
    }.also { token = it }

    fun register(email: String, password: String, name: String, gender: String): LiveData<com.capstone.caloriewisebeta.data.result.Result<RegisterResponse>> = liveData {
        emit(com.capstone.caloriewisebeta.data.result.Result.Loading)
        try {
            val response = apiService.register(email, password, name, gender)

            emit(com.capstone.caloriewisebeta.data.result.Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)

            emit(com.capstone.caloriewisebeta.data.result.Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            Log.d(TAG, "register: ${e.message}")

            emit(com.capstone.caloriewisebeta.data.result.Result.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<com.capstone.caloriewisebeta.data.result.Result<LoginResult>> = liveData {
        emit(com.capstone.caloriewisebeta.data.result.Result.Loading)
        try {
            val response = apiService.login(email, password)
            val loginResult = response.loginResult

            if (loginResult != null) {
                emit(com.capstone.caloriewisebeta.data.result.Result.Success(loginResult))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)

            emit(com.capstone.caloriewisebeta.data.result.Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            Log.d(TAG, "login: ${e.message}")

            emit(com.capstone.caloriewisebeta.data.result.Result.Error(e.message.toString()))
        }
    }

    fun updateProfile(name: String, gender: String, email: String): LiveData<Result<ProfileUpdateResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = getToken()
            val response = apiService.updateProfile(name, gender, email)

            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ProfileUpdateResponse::class.java)

            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAllSearch(): LiveData<com.capstone.caloriewisebeta.data.result.Result<List<SearchItem>>> = liveData {
        emit(com.capstone.caloriewisebeta.data.result.Result.Loading)
        try {
            val token = getToken()
            apiService = ApiConfig.getApiService(token.toString())

            val response = apiService.getSearch()

            // Check if the response contains a single search object or an array of search
            val searchItem = response.dataMakanan ?: emptyList()

            if (searchItem.isNotEmpty() && searchItem[0] is SearchItem) {
                emit(Result.Success(searchItem as List<SearchItem>))
            } else {
                // If response.search is null or not a list of SearchItem, it means the response is a single Search object
                emit(Result.Success(listOf(response as SearchItem)))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, SearchResponse::class.java)

            emit(Result.Error(errorResponse.message ?: "Unknown error"))
        } catch (e: Exception) {
            Log.d(TAG, "getAllSearch: ${e.message}")

            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun getAllNote(): LiveData<Result<List<NotesItem>>> = liveData {
        emit(Result.Loading)
        try {
            val token = getToken()
            apiService = ApiConfig.getApiService(token.toString())

            val response = apiService.getNote()

            // Check if the response contains a single note object or an array of notes
            val notesItem = response.notes ?: emptyList()

            if (notesItem.isNotEmpty() && notesItem[0] is NotesItem) {
                emit(Result.Success(notesItem as List<NotesItem>))
            } else {
                // If response.notes is null or not a list of NotesItem, it means the response is a single note object
                emit(Result.Success(listOf(response as NotesItem)))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, NoteResponse::class.java)

            emit(Result.Error(errorResponse.message ?: "Unknown error"))
        } catch (e: Exception) {
            Log.d(TAG, "getAllNote: ${e.message}")

            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }


    fun uploadNewNote(file: File?, content: String, title: String): LiveData<Result<GeneralResponse>> = liveData {
        emit(Result.Loading)
        try {
            val imageFile = reduceFileImage(file!!)
            Log.d("Image File", "showImage: ${imageFile.path}")

            val contentBody = content.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val titleBody = title.toRequestBody("text/plain".toMediaType())

            val multipartBody = MultipartBody.Part.createFormData(
                "myImage",
                imageFile.name,
                requestImageFile
            )

            val response = apiService.uploadNewNote(multipartBody, contentBody, titleBody)

            emit((Result.Success(response)))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, GeneralResponse::class.java)

            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            Log.d(TAG, "uploadNewStory: ${e.message}")

            emit(Result.Error(e.message.toString()))
        }
    }

    fun getBulk(): LiveData<Result<List<BulkItem>>> = liveData {
        emit(Result.Loading)
        try {
            val token = getToken()
            apiService = ApiConfig.getApiService(token.toString())

            val response = apiService.getBulking()

            // Check if the response contains a single note object or an array of notes
            val dataItem = response.data ?: emptyList()

            if (dataItem.isNotEmpty() && dataItem[0] is BulkItem) {
                emit(Result.Success(dataItem as List<BulkItem>))
            } else {
                emit(Result.Success(listOf(response as BulkItem)))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FoodRecBulkResponse::class.java)

//            emit(Result.Error(errorResponse.message ?: "Unknown error"))
        } catch (e: Exception) {
            Log.d(TAG, "getBulk: ${e.message}")

            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun getCut(): LiveData<Result<List<CuttingItemDetailsItem>>> = liveData {
        emit(Result.Loading)
        try {
            val token = getToken()
            apiService = ApiConfig.getApiService(token.toString())

            val response = apiService.getCutting()

            // Check if the response contains the data object
            val cut = response.data

            // Check if the cuttingItemDetails list is not null and not empty
            if (cut != null && cut.cuttingItemDetails != null && cut.cuttingItemDetails.isNotEmpty()) {
                emit(Result.Success(cut.cuttingItemDetails.filterNotNull()))
            } else {
                emit(Result.Success(emptyList()))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, CutDetailResponse::class.java)

//            emit(Result.Error(errorResponse.message ?: "Unknown error"))
        } catch (e: Exception) {
            Log.d(TAG, "getCut: ${e.message}")

            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }



    companion object {
        private const val TAG = "SearchRepository"
        private var instance: SearchRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): SearchRepository {
            return instance ?: synchronized(this) {
                instance ?: SearchRepository(apiService, userPreference).also { instance = it }
            }
        }
    }
}