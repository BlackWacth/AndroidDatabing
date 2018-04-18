package com.bruce.androiddatabing.data

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.bruce.androiddatabing.util.ObservableViewModel

class ProfileObservableViewModel : ObservableViewModel() {
    val name = ObservableField("Hua")
    val lastName = ObservableField("ObservableViewModel")
    val likes = ObservableInt(0)

    fun onLike() {
        likes.increment()
        notifyPropertyChanged()
    }

    fun getPopularity(): Popularity = likes.get().let {
        when {
            it > 9 -> Popularity.STAR
            it > 4 -> Popularity.POPULAR
            else -> Popularity.NORMAL
        }
    }
}

class ProfileObservableFieldsViewModel: ViewModel() {
    val name = ObservableField("Hua")
    val lastName = ObservableField("ViewModel")
    val likes = ObservableInt(0)

    val popularity = ObservableField<Popularity>(Popularity.NORMAL)

    fun onLike() {
        likes.increment()
        popularity.set(likes.get().let {
            when {
                it > 9 -> Popularity.STAR
                it > 4 -> Popularity.POPULAR
                else -> Popularity.NORMAL
            }
        })
    }
}

private fun ObservableInt.increment() = set(get() + 1)

enum class Popularity {
    NORMAL,
    POPULAR,
    STAR
}
