package com.bruce.androiddatabing.util

import android.arch.lifecycle.ViewModel
import android.databinding.BaseObservable
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry

open class ObservableViewModel: ViewModel(), Observable {

    private val callbacks: PropertyChangeRegistry by lazy {
        PropertyChangeRegistry()
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    fun notifyPropertyChanged(fieldId: Int = 0) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }
}