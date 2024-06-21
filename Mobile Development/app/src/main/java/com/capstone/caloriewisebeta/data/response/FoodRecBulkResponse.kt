package com.capstone.caloriewisebeta.data.response

import com.google.gson.annotations.SerializedName

data class FoodRecBulkResponse(

    @field:SerializedName("data")
	val data: List<BulkItem?>? = null,

    @field:SerializedName("error")
	val error: Boolean? = null,

    @field:SerializedName("status")
	val status: Int? = null
)