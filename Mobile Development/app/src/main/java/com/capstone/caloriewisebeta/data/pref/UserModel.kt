package com.capstone.caloriewisebeta.data.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val userId: String,
    val email: String,
    val token: String,
) :Parcelable