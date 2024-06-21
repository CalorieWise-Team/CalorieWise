package com.capstone.caloriewisebeta.view.foodreccut

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
import com.capstone.caloriewisebeta.data.response.Cut
import com.capstone.caloriewisebeta.data.result.Result
import com.capstone.caloriewisebeta.databinding.FragmentFoodRecCuttingBinding
import com.capstone.caloriewisebeta.view.ViewModelFactory
import com.capstone.caloriewisebeta.view.foodrecbulk.FoodRecBulkFragment

class FoodRecCutFragment : Fragment(R.layout.fragment_food_rec_cutting) {

    private var _binding: FragmentFoodRecCuttingBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodRecCutViewModel: FoodRecCutViewModel

    // Declare the DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodRecCuttingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        foodRecCutViewModel = obtainViewModel(requireActivity() as AppCompatActivity)

        setupFood()
        setupAccessibility()
        setupToolbar()
    }

    private fun setupAccessibility() {
        binding.apply {
            rvCut.contentDescription = getString(R.string.list_of_stories)
        }
    }

    private fun setupToolbar() {
        with(binding) {
            topAppBar.inflateMenu(R.menu.foodrec_menu)
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_bulking -> {
                        navigateToFragment(FoodRecBulkFragment())
                        true
                    }
                    R.id.menu_cutting -> {
                        navigateToFragment(FoodRecCutFragment())
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setupFood() {
        val foodRecCutAdapter = FoodRecCutAdapter()

        binding.rvCut.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCut.adapter = foodRecCutAdapter

        foodRecCutAdapter.setOnItemClickCallback(object : FoodRecCutAdapter.OnItemClickCallback {
            override fun onItemClicked(item: Cut?) {
                moveToDetailCut(
                    item,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle()
                )
            }
        })

        foodRecCutViewModel.getCut().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val response = result.data
                    if (response.isEmpty()) {
                        binding.rvCut.visibility = View.GONE
                        binding.foodrecNotAvailable.visibility = View.VISIBLE
                    } else {
                        binding.rvCut.visibility = View.VISIBLE
                        binding.foodrecNotAvailable.visibility = View.GONE
//                        foodRecCutAdapter.submitList(response.cuttingItemDetails.filterNotNull())
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    showToast(result.error)
                }
            }
        }
    }

    private fun moveToDetailCut(item: Cut?, bundle: Bundle?) {
        val intent = Intent(requireContext(), DetailCutActivity::class.java)
        intent.putExtra(DetailCutActivity.EXTRA_RESULT, item)
        startActivity(intent, bundle)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): FoodRecCutViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(activity.dataStore)
        )
        return ViewModelProvider(activity, factory)[FoodRecCutViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SESSION = "session"
    }
}
