package com.capstone.caloriewisebeta.view.main

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
import com.capstone.caloriewisebeta.data.response.SearchItem
import com.capstone.caloriewisebeta.data.result.Result
import com.capstone.caloriewisebeta.databinding.FragmentSearchBinding
import com.capstone.caloriewisebeta.view.ViewModelFactory
import androidx.appcompat.widget.SearchView

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchAdapter: SearchAdapter

    // Deklarasikan DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        searchViewModel = obtainViewModel(requireActivity() as AppCompatActivity)

        setupSearch()
        setupAccessibility()
    }

    private fun setupAccessibility() {
        binding.apply {
            rvGithub.contentDescription = getString(R.string.list_of_stories)
        }
    }

    private fun setupSearch() {
        searchAdapter = SearchAdapter()

        binding.rvGithub.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGithub.adapter = searchAdapter

        searchAdapter.setOnItemClickCallback(object : SearchAdapter.OnItemClickCallback {
            override fun onItemClicked(items: SearchItem?) {
                moveToDetailSearch(
                    items,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle()
                )
            }
        })

        searchViewModel.getAllSearch().observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val response = it.data
                        searchAdapter.fullList = response // Simpan seluruh data untuk kebutuhan filter
                        if (response.isEmpty()) {
                            binding.rvGithub.visibility = View.GONE
                        } else {
                            binding.rvGithub.visibility = View.VISIBLE
                            searchAdapter.submitList(response)
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(it.error)
                    }
                }
            }
        }

        setupSearchBar()
    }

    private fun setupSearchBar() {
        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchAdapter.filter(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchAdapter.filter(it) }
                return true
            }
        })
    }

    private fun moveToDetailSearch(item: SearchItem?, bundle: Bundle?) {
        val intent = Intent(requireContext(), DetailSearchActivity::class.java)
        intent.putExtra(DetailSearchActivity.EXTRA_RESULT, item)
        startActivity(intent, bundle)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): SearchViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(activity.dataStore)
        )
        return ViewModelProvider(activity, factory)[SearchViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SESSION = "session"
    }
}
