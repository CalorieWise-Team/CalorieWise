package com.capstone.caloriewisebeta.data.response

import com.google.gson.annotations.SerializedName

data class MealResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class data(

	@field:SerializedName("meal_date")
	val mealDate: String? = null,

	@field:SerializedName("meal_title")
	val mealTitle: String? = null,

	@field:SerializedName("meal_items")
	val mealItems: List<MealItemsItem?>? = null,

	@field:SerializedName("meal_time")
	val mealTime: String? = null,

	@field:SerializedName("meal_id")
	val mealId: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)

data class MealItemsItem(

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("meal_item_id")
	val mealItemId: Int? = null,

	@field:SerializedName("food_id")
	val foodId: Int? = null
)
