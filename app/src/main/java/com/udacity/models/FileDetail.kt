package com.udacity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileDetail(
    val fileName: String,
    val fileStatus: String
) : Parcelable