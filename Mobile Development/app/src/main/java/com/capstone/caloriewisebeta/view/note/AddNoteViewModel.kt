package com.capstone.caloriewisebeta.view.note

import androidx.lifecycle.ViewModel
import com.capstone.caloriewisebeta.data.repository.SearchRepository
import java.io.File

class AddNoteViewModel(private val repository: SearchRepository): ViewModel() {

    fun uploadNewNote(file: File?, content: String, title:String) = repository.uploadNewNote(file, content, title)

}