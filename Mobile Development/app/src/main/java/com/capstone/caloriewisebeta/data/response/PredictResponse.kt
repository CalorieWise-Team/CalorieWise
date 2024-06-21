package com.capstone.caloriewisebeta.data.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("confidenceScore")
	val confidenceScore: Any? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("isAboveThreshold")
	val isAboveThreshold: Boolean? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DataItem(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("proteins")
	val proteins: Any? = null,

	@field:SerializedName("fat")
	val fat: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("calories")
	val calories: Int? = null,

	@field:SerializedName("food_id")
	val foodId: Int? = null,

	@field:SerializedName("carbohydrate")
	val carbohydrate: Any? = null
)
