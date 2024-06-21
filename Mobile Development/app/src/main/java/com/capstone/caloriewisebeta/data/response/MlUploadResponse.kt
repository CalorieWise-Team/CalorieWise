package com.capstone.caloriewisebeta.data.response

import com.google.gson.annotations.SerializedName

data class MlUploadResponse(

	@field:SerializedName("predicted_class_index")
	val predictedClassIndex: Int? = null,

	@field:SerializedName("food_name")
	val foodName: String? = null,

	@field:SerializedName("accuracy")
	val accuracy: Any? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("food_id")
	val foodId: Int? = null
)
