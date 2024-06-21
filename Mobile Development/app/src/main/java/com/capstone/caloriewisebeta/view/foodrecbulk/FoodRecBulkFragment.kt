package com.capstone.caloriewisebeta.view.foodrecbulk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.response.BulkItem
import com.capstone.caloriewisebeta.data.result.Result
import com.capstone.caloriewisebeta.databinding.FragmentFoodRecBulkingBinding
import com.capstone.caloriewisebeta.view.ViewModelFactory
import com.capstone.caloriewisebeta.view.foodreccut.FoodRecCutFragment


class FoodRecBulkFragment : Fragment(R.layout.fragment_food_rec_bulking) {

    private var _binding: FragmentFoodRecBulkingBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodRecBulkViewModel: FoodRecBulkViewModel

    // Declare the DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodRecBulkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        foodRecBulkViewModel = obtainViewModel(requireActivity() as AppCompatActivity)

        setupFood()
        setupAccessibility()
//        setupToolbar()
    }

    private fun setupAccessibility() {
        binding.apply {
            rvBulk.contentDescription = getString(R.string.list_of_stories)
        }
    }

//    private fun setupToolbar() {
//        with(binding) {
//            topAppBar.inflateMenu(R.menu.foodrec_menu)
//            topAppBar.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.menu_bulking -> {
//                        navigateToFragment(FoodRecBulkFragment())
//                        true
//                    }
//                    R.id.menu_cutting -> {
//                        navigateToFragment(FoodRecCutFragment())
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }
//    }
//
//    private fun navigateToFragment(fragment: Fragment) {
//        val transaction = parentFragmentManager.beginTransaction()
//        transaction.replace(R.id.frame_layout, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }


    private fun setupFood() {
        val foodRecBulkAdapter = FoodRecBulkAdapter()

        binding.rvBulk.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBulk.adapter = foodRecBulkAdapter

        foodRecBulkAdapter.setOnItemClickCallback(object : FoodRecBulkAdapter.OnItemClickCallback {
            override fun onItemClicked(items: BulkItem?) {
                moveToDetailBulk(
                    items,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle()
                )
            }
        })

        foodRecBulkViewModel.getBulk().observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val response = it.data
                        if (response.isEmpty()) {
                            binding.rvBulk.visibility = View.GONE
                            binding.foodrecNotAvailable.visibility = View.VISIBLE
                        } else {
                            binding.rvBulk.visibility = View.VISIBLE
                            binding.foodrecNotAvailable.visibility = View.GONE
                            foodRecBulkAdapter.submitList(response)
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

    private fun moveToDetailBulk(item: BulkItem?, bundle: Bundle?) {
        val intent = Intent(requireContext(), DetailBulkActivity::class.java)
        intent.putExtra(DetailBulkActivity.EXTRA_RESULT, item)
        startActivity(intent, bundle)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): FoodRecBulkViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(activity.dataStore)
        )
        return ViewModelProvider(activity, factory)[FoodRecBulkViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val SESSION = "session"
    }
}

