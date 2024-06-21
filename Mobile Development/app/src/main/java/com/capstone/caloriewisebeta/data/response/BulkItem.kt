package com.capstone.caloriewisebeta.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BulkItem(

    @field:SerializedName("bulking_item_id")
    val bulkingItemId: Int? = null,

    @field:SerializedName("cutting_item_id")
    val cuttingItemId: Int? = null,

    @field:SerializedName("food_name")
    val foodName: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("description")
    val description: String? = null
) :Parcelable
