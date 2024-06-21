package com.capstone.caloriewisebeta.data.response

import com.google.gson.annotations.SerializedName

data class NoteResponse(

	@field:SerializedName("notes")
	val notes: List<NotesItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

