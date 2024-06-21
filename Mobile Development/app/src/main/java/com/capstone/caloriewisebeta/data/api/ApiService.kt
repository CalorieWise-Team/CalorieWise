package com.capstone.caloriewisebeta.data.api

import com.capstone.caloriewisebeta.data.response.CutDetailResponse
import com.capstone.caloriewisebeta.data.response.FoodRecBulkResponse
import com.capstone.caloriewisebeta.data.response.LoginResponse
import com.capstone.caloriewisebeta.data.response.GeneralResponse
import com.capstone.caloriewisebeta.data.response.NoteResponse
import com.capstone.caloriewisebeta.data.response.PredictResponse
import com.capstone.caloriewisebeta.data.response.ProfileUpdateResponse
import com.capstone.caloriewisebeta.data.response.RegisterResponse
import com.capstone.caloriewisebeta.data.response.SearchItem
import com.capstone.caloriewisebeta.data.response.SearchResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("gender") gender: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("/views/food")
    suspend fun getSearch(): SearchResponse

    @GET("/views/notes")
    suspend fun getNote(): NoteResponse

    @Multipart
    @POST("/create/note")
    suspend fun uploadNewNote(
        @Part file: MultipartBody.Part,
        @Part("content") content: RequestBody,
        @Part("title") title: RequestBody
    ): GeneralResponse

    @Multipart
    @POST("/predict")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): PredictResponse

    @GET("/views/bulking")
    suspend fun getBulking() : FoodRecBulkResponse

    @GET("/views/cutting")
    suspend fun getCutting() : CutDetailResponse

    @FormUrlEncoded
    @PUT("/update/profile")
    suspend fun updateProfile(
        @Field("user_id") userId: String,
        @Field("name") name: String,
        @Field("gender") gender: String,
        @Field("email") email: String
    ): ProfileUpdateResponse

    abstract fun updateProfile(userId: String, name: String, gender: String): ProfileUpdateResponse
}