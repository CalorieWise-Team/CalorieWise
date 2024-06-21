package com.capstone.caloriewisebeta.view.riwayat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.databinding.ActivityRiwayatBinding

class RiwayatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            title = getString(R.string.riwayat_activity_title)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}
