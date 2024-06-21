package com.capstone.caloriewisebeta.view.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.databinding.FragmentProfileBinding
import com.capstone.caloriewisebeta.view.ViewModelFactory
import com.capstone.caloriewisebeta.view.login.LoginActivity
import com.capstone.caloriewisebeta.view.riwayat.RiwayatActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SESSION)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel = obtainViewModel(this@ProfileFragment)

        setupToolbar()
        // Other setup code if needed
    }

    private fun navigateToHistoryActivity() {
        val intent = Intent(requireContext(), RiwayatActivity::class.java)
        startActivity(intent)
    }

    private fun setupToolbar() {
        with(binding) {
            topAppBar.inflateMenu(R.menu.option_menu)
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_logout -> {
                        moveToLogin()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun moveToLogin() {
        profileViewModel.logout()
        showToast(getString(R.string.successfully_logged_out))
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(fragment: Fragment): ProfileViewModel {
        val factory = ViewModelFactory.getInstance(
            fragment.requireActivity().application,
            UserPreference.getInstance(fragment.requireContext().dataStore)
        )
        return ViewModelProvider(fragment, factory)[ProfileViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SESSION = "session"
    }
}
