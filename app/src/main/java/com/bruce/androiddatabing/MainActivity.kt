package com.bruce.androiddatabing

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bruce.androiddatabing.databinding.ActivityMainBinding
import com.bruce.androiddatabing.ui.ObservableFieldActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.observableFieldsActivityButton.setOnClickListener {
            startActivity(Intent(this, ObservableFieldActivity::class.java))
        }

        binding.viewmodelActivityButton.setOnClickListener {

        }
    }
}
