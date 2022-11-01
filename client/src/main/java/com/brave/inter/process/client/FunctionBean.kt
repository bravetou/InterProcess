package com.brave.inter.process.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FunctionBean(
    val id: Int = -1,
    val imgRes: Int = -1,
    var name: String = ""
) : Parcelable