package com.capstone.caloriewisebeta.data.response

import com.google.gson.annotations.SerializedName

data class SearchResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data_makanan")
	val dataMakanan: List<SearchItem?>? = null
)
