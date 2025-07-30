package com.moistlabs.statussaver.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Media(
    val fileName: String,
    val uri: Uri,
    val isVideo: Boolean,
    val type: ContentType = ContentType.REGULAR
) : Parcelable
