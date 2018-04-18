package com.bruce.androiddatabing.ui

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bruce.androiddatabing.R
import com.bruce.androiddatabing.databinding.ActivityTwoWayBinding

class TwoWayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_way)
        val binding = DataBindingUtil.setContentView<ActivityTwoWayBinding>(this, R.layout.activity_two_way)

    }
}
