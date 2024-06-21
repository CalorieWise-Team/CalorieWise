package com.capstone.caloriewisebeta.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class CutDetailResponse(

	@field:SerializedName("data")
	val data: Cut? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
@Parcelize
data class Cut(

	@field:SerializedName("food_name")
	val foodName: String? = null,

	@field:SerializedName("cutting_item_id")
	val cuttingItemId: Int? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("cutting_item_details")
	val cuttingItemDetails: List<CuttingItemDetailsItem?>? = null,

	@field:SerializedName("total_all_calories")
	val totalAllCalories: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("total_all_proteins")
	val totalAllProteins: @RawValue Any? = null,

	@field:SerializedName("total_all_carbohydrate")
	val totalAllCarbohydrate: @RawValue Any? = null,

	@field:SerializedName("total_all_fat")
	val totalAllFat: @RawValue Any? = null
) :Parcelable

@Parcelize
data class CuttingItemDetailsItem(

	@field:SerializedName("food_name")
	val foodName: String? = null,

	@field:SerializedName("total_fat")
	val totalFat: @RawValue Any? = null,

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("total_calories")
	val totalCalories: Int? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("cutting_item_detail_id")
	val cuttingItemDetailId: Int? = null,

	@field:SerializedName("total_carbohydrate")
	val totalCarbohydrate: Int? = null,

	@field:SerializedName("calories")
	val calories: Int? = null,

	@field:SerializedName("food_id")
	val foodId: Int? = null,

	@field:SerializedName("carbohydrate")
	val carbohydrate: Int? = null,

	@field:SerializedName("proteins")
	val proteins: @RawValue Any? = null,

	@field:SerializedName("total_proteins")
	val totalProteins: @RawValue Any? = null,

	@field:SerializedName("fat")
	val fat: @RawValue Any? = null
) :Parcelable
