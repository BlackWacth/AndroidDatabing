package com.bruce.androiddatabing.util

import android.content.Context
import android.content.res.ColorStateList
import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ImageViewCompat
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bruce.androiddatabing.R
import com.bruce.androiddatabing.data.Popularity


object BindingAdapters {

    @BindingAdapter(value = ["app:popularityIcon"])
    @JvmStatic
    fun popularityIcon(view: ImageView, popularity: Popularity) {
        val color = getAssociatedColor(view.context, popularity)
        ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(color))
        view.setImageDrawable(getDrawablePopularity(view.context, popularity))
    }

    @BindingAdapter(value = ["app:progressScaled", "android:max"], requireAll = true)
    @JvmStatic
    fun setProgress(progressBar: ProgressBar, likes: Int, max: Int) {
        progressBar.progress = (likes * max / 5).coerceAtMost(max)
    }

    @BindingAdapter(value = ["app:hideIfZero"])
    @JvmStatic
    fun hideIfZero(view: View, number: Int) {
        view.visibility = if (number == 0) View.GONE else View.VISIBLE
    }

    @BindingAdapter(value = ["app:progressTint"])
    @JvmStatic
    fun tintPopularity(view: ProgressBar, popularity: Popularity) {
        val color = getAssociatedColor(view.context, popularity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.progressTintList = ColorStateList.valueOf(color)
        }
    }

    private fun getDrawablePopularity(context: Context, popularity: Popularity): Drawable? = when (popularity) {
        Popularity.NORMAL -> ContextCompat.getDrawable(context, R.drawable.ic_person_black_96dp)
        Popularity.POPULAR -> ContextCompat.getDrawable(context, R.drawable.ic_whatshot_black_96dp)
        Popularity.STAR -> ContextCompat.getDrawable(context, R.drawable.ic_whatshot_black_96dp)
    }

    private fun getAssociatedColor(context: Context, popularity: Popularity): Int = when (popularity) {
        Popularity.NORMAL -> context.theme.obtainStyledAttributes(intArrayOf(android.R.attr.colorForeground)).getColor(0, 0x000000)
        Popularity.POPULAR -> ContextCompat.getColor(context, R.color.popular)
        Popularity.STAR -> ContextCompat.getColor(context, R.color.star)
    }

    @BindingAdapter("clearTextOnFocus")
    @JvmStatic
    fun EditText.clearTextOnFocus(enabled: Boolean) = if (enabled) {
        clearOnFocusAndDispatch(this, null)
    } else {
        this.onFocusChangeListener = null
    }

    @BindingAdapter("clearOnFocusAndDispatch")
    @JvmStatic fun clearOnFocusAndDispatch(editText: EditText, listener: View.OnFocusChangeListener?) {
        editText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            val textView = v as TextView
            if (hasFocus) {
                editText.setTag(R.id.previous_value, textView.text)
                textView.text = ""
            } else {
                if (textView.text.isEmpty()) {
                    val tag = textView.getTag(R.id.previous_value) as CharSequence
                    textView.text = tag
                }
                listener?.onFocusChange(v, hasFocus)
            }
        }
    }

    @BindingAdapter("hideKeyboardOnInputDone")
    @JvmStatic fun hideKeyboardOnInputDone(view: EditText, enabled: Boolean) {
        if (!enabled) return

        val listener = TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.clearFocus()
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            false
        }
        view.setOnEditorActionListener(listener)
    }

    @BindingAdapter(value = ["android:max", "android:progress"], requireAll = true)
    @JvmStatic fun updateProgress(progressBar: ProgressBar, max: Int, progress: Int) {
        progressBar.max = max
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(progress, false)
        } else {
            progressBar.progress = progress
        }
    }

    @BindingAdapter("loseFocusWhen")
    @JvmStatic fun loseFocusWhen(view: EditText, condition: Boolean) {
        if (condition) view.clearFocus()
    }
}
