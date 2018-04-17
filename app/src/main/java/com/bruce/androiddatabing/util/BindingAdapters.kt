package com.bruce.androiddatabing.util

import android.databinding.BindingAdapter
import android.widget.ProgressBar


object BindingAdapters {

    @BindingAdapter(value = ["app:progressScaled", "android:max"], requireAll = true)
    @JvmStatic fun setProgress(progressBar: ProgressBar, likes: Int, max: Int) {
        progressBar.progress = (likes * max / 5).coerceAtMost(max)
    }
}
