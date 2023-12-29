package com.doru.reptomin.playground.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

class ExifUtil {

    companion object {
        @Throws(Exception::class)
        fun getExifData(context: Context, photoUri: Uri): ExifInterface? {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(photoUri)

            inputStream?.use {
                return ExifInterface(it)
            }

            return null
        }
    }
}
