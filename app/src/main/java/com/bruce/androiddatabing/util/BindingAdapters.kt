package com.bruce.androiddatabing.util

import android.content.Context
import android.content.res.ColorStateList
import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ImageViewCompat
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bruce.androiddatabing.R
import com.bruce.androiddatabing.data.Popularity


object BindingAdapters {

    @BindingAdapter(value = ["app:popularityIcon"])
    @JvmStatic fun popularityIcon(view: ImageView, popularity: Popularity) {
        val color = getAssociatedColor(view.context, popularity)
        ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(color))
        view.setImageDrawable(getDrawablePopularity(view.context, popularity))
    }

    @BindingAdapter(value = ["app:progressScaled", "android:max"], requireAll = true)
    @JvmStatic fun setProgress(progressBar: ProgressBar, likes: Int, max: Int) {
        progressBar.progress = (likes * max / 5).coerceAtMost(max)
    }

    @BindingAdapter(value = ["app:hideIfZero"])
    @JvmStatic fun hideIfZero(view: View, number: Int) {
        view.visibility = if (number == 0) View.GONE else View.VISIBLE
    }

    @BindingAdapter(value = ["app:progressTint"])
    @JvmStatic fun tintPopularity(view: ProgressBar, popularity: Popularity) {
        val color = getAssociatedColor(view.context, popularity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.progressTintList = ColorStateList.valueOf(color)
        }
    }

    private fun getDrawablePopularity(context: Context, popularity: Popularity): Drawable? = when(popularity) {
        Popularity.NORMAL -> ContextCompat.getDrawable(context, R.drawable.ic_person_black_96dp)
        Popularity.POPULAR -> ContextCompat.getDrawable(context, R.drawable.ic_whatshot_black_96dp)
        Popularity.STAR -> ContextCompat.getDrawable(context, R.drawable.ic_whatshot_black_96dp)
    }

    private fun getAssociatedColor(context: Context, popularity: Popularity): Int = when(popularity) {
        Popularity.NORMAL -> context.theme.obtainStyledAttributes(intArrayOf(android.R.attr.colorForeground)).getColor(0, 0x000000)
        Popularity.POPULAR -> ContextCompat.getColor(context, R.color.popular)
        Popularity.STAR -> ContextCompat.getColor(context, R.color.star)
    }
}
