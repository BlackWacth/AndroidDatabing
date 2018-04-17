package com.bruce.androiddatabing.util

import android.databinding.BindingConversion
import android.view.View

object ConverterUtil {
    @JvmStatic fun isZero(number: Int) = number == 0
}

object BindingConverters{

    @BindingConversion
    @JvmStatic fun booleanToVisibility(isNotVisible: Boolean) = if (isNotVisible) View.GONE else View.VISIBLE
}