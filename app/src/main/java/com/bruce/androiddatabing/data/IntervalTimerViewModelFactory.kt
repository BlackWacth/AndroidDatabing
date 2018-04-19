package com.bruce.androiddatabing.data

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.bruce.androiddatabing.util.DefaultTimer

object IntervalTimerViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IntervalTimerViewModel::class.java)) {
            return IntervalTimerViewModel(DefaultTimer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}