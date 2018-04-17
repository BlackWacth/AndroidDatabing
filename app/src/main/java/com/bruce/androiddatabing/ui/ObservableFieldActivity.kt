package com.bruce.androiddatabing.ui

import android.databinding.DataBindingUtil
import android.databinding.ObservableInt
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bruce.androiddatabing.R
import com.bruce.androiddatabing.data.ObservableFieldProfile
import com.bruce.androiddatabing.databinding.ActivityObservableFieldBinding

class ObservableFieldActivity : AppCompatActivity() {

    private val observableFieldProfile = ObservableFieldProfile("Hua", "Bruce", ObservableInt(0))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityObservableFieldBinding>(this, R.layout.activity_observable_field)
        binding.user = observableFieldProfile
    }

    fun onLike(view: View) {
        observableFieldProfile.likes.set(observableFieldProfile.likes.get() + 1)
    }
}
