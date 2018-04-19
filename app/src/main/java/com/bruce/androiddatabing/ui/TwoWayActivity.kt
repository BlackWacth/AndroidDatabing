package com.bruce.androiddatabing.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableInt
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bruce.androiddatabing.BR
import com.bruce.androiddatabing.R
import com.bruce.androiddatabing.data.IntervalTimerViewModel
import com.bruce.androiddatabing.data.IntervalTimerViewModelFactory
import com.bruce.androiddatabing.databinding.ActivityTwoWayBinding

const val SHARED_PREFS_KEY = "timer"

class TwoWayActivity : AppCompatActivity() {

    private val intervalTimerViewModel: IntervalTimerViewModel
        by lazy {
            ViewModelProviders.of(this, IntervalTimerViewModelFactory).get(IntervalTimerViewModel::class.java)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_way)
        val binding = DataBindingUtil.setContentView<ActivityTwoWayBinding>(this, R.layout.activity_two_way)
        val viewModel = intervalTimerViewModel
        binding.timerViewModel = viewModel

        observeAndSaveTimePerSet(viewModel.timePerWorkSet, R.string.prefs_timePerWorkSet)
        observeAndSaveTimePerSet(viewModel.timePerRestSet, R.string.prefs_timePerRestSet)

        observableAndSaveNumberOfSets(viewModel)

        if (savedInstanceState == null) {
            restorePreferences(viewModel)
            observableAndSaveNumberOfSets(viewModel)
        }
    }

    private fun restorePreferences(viewModel: IntervalTimerViewModel) {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE) ?: return

        var wasAnythingRestored = false

        val timePerWorkSetKey = getString(R.string.prefs_timePerWorkSet)
        if (sharedPreferences.contains(timePerWorkSetKey)) {
            viewModel.timePerWorkSet.set(sharedPreferences.getInt(timePerWorkSetKey, 100))
            wasAnythingRestored = true
        }

        val timePerRestSetKey = getString(R.string.prefs_timePerRestSet)
        if (sharedPreferences.contains(timePerRestSetKey)) {
            viewModel.timePerWorkSet.set(sharedPreferences.getInt(timePerRestSetKey, 50))
            wasAnythingRestored = true
        }

        val numberOfSetsKey = getString(R.string.prefs_numberOfSets)
        if (sharedPreferences.contains(numberOfSetsKey)) {
            viewModel.numberOfSets = arrayOf(0, sharedPreferences.getInt(numberOfSetsKey, 5))
            wasAnythingRestored = true
        }

        if (wasAnythingRestored) Log.i(this.javaClass.simpleName, "Preferences restored")

        viewModel.stopButtonClicked()
    }

    private fun observableAndSaveNumberOfSets(viewModel: IntervalTimerViewModel) {
        viewModel.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (propertyId == BR.numberOfSets) {
                    val sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE) ?: return
                    sharedPreferences.edit().apply {
                        putInt(getString(R.string.prefs_numberOfSets), viewModel.numberOfSets[1])
                        apply()
                    }
                }
            }
        })
    }

    private fun observeAndSaveTimePerSet(timePerWorkSet: ObservableInt, prefsKey: Int) {
        timePerWorkSet.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                sharedPreferences.edit().apply{
                    putInt(getString(prefsKey), (sender as ObservableInt).get())
                    apply()
                }
            }
        })
    }



}
