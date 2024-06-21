package com.capstone.caloriewisebeta.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class SearchItem(

    @field:SerializedName("proteins")
    val proteins: Float?= null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("fat")
    val fat: Float? = null,

    @field:SerializedName("calories")
    val calories: Float? = null,

    @field:SerializedName("food_id")
    val foodId: Int? = null,

    @field:SerializedName("carbohydrate")
    val carbohydrate: Float? = null
) : Parcelable