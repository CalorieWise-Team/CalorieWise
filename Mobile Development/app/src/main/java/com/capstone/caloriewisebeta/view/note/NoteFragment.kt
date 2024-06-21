package com.capstone.caloriewisebeta.view.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.response.NotesItem
import com.capstone.caloriewisebeta.data.result.Result
import com.capstone.caloriewisebeta.databinding.FragmentNoteBinding
import com.capstone.caloriewisebeta.view.ViewModelFactory

class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel

    // Declare the DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        noteViewModel = obtainViewModel(requireActivity() as AppCompatActivity)

        setupNotes()
        setupAction()
        setupAccessibility()
    }

    private fun setupAction() {
        binding.fabButton.setOnClickListener { moveToAddNewNote() }
    }

    private fun setupAccessibility() {
        binding.apply {
            rvNote.contentDescription = getString(R.string.list_of_stories)
            fabButton.contentDescription = getString(R.string.fb_add_note)
        }
    }

    private fun setupNotes() {
        val noteAdapter = NoteAdapter()

        binding.rvNote.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNote.adapter = noteAdapter

        noteAdapter.setOnItemClickCallback(object : NoteAdapter.OnItemClickCallback {
            override fun onItemClicked(items: NotesItem?) {
                moveToDetailNote(
                    items,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle()
                )
            }
        })

        noteViewModel.getAllNote().observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val response = it.data
                        if (response.isEmpty()) {
                            binding.rvNote.visibility = View.GONE
                            binding.storyNotAvailable.visibility = View.VISIBLE
                        } else {
                            binding.rvNote.visibility = View.VISIBLE
                            binding.storyNotAvailable.visibility = View.GONE
                            noteAdapter.submitList(response)
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(it.error)
                    }
                }
            }
        }
    }

    private fun moveToDetailNote(item: NotesItem?, bundle: Bundle?) {
        val intent = Intent(requireContext(), DetailNoteActivity::class.java)
        intent.putExtra(DetailNoteActivity.EXTRA_RESULT, item)
        startActivity(intent, bundle)
    }

    private fun moveToAddNewNote() {
        startActivity(Intent(requireContext(), AddNoteActivity::class.java))
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): NoteViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(activity.dataStore)
        )
        return ViewModelProvider(activity, factory)[NoteViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SESSION = "session"
    }
}
