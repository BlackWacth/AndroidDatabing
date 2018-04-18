package com.bruce.androiddatabing.ui

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bruce.androiddatabing.R
import com.bruce.androiddatabing.data.ProfileObservableViewModel
import com.bruce.androiddatabing.databinding.ActivityViewModelBinding

class ViewModelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityViewModelBinding>(this, R.layout.activity_view_model)

        val viewModel = ViewModelProviders.of(this).get(ProfileObservableViewModel::class.java)

        binding.viewModel = viewModel
    }
}
