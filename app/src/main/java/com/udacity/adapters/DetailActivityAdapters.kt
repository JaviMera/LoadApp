package com.udacity.adapters

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.udacity.R

@BindingAdapter("statusColor")
fun bindStatusTextColor(textView: TextView, statusText: String){
    when(statusText){
        textView.context.getString(R.string.downloaded_file_successful) -> textView.setTextColor(ContextCompat.getColor(textView.context, R.color.colorPrimaryDark))
        textView.context.getString(R.string.downloaded_file_fail) -> textView.setTextColor(ContextCompat.getColor(textView.context, R.color.colorStatusFail))
    }
}