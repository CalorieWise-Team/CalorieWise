package com.capstone.caloriewisebeta.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.databinding.ActivityMainBinding
import com.capstone.caloriewisebeta.view.caloriecal.CalorieCalFragment
import com.capstone.caloriewisebeta.view.foodrecbulk.FoodRecBulkFragment
import com.capstone.caloriewisebeta.view.note.NoteFragment
import com.capstone.caloriewisebeta.view.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(SearchFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId) {

                R.id.home -> replaceFragment(SearchFragment())
                R.id.caloriecal -> replaceFragment(CalorieCalFragment())
                R.id.note -> replaceFragment(NoteFragment())
                R.id.foodrec -> replaceFragment(FoodRecBulkFragment())
                R.id.profile -> replaceFragment(ProfileFragment())

                else ->{

                }
            }

            true
        }
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}



