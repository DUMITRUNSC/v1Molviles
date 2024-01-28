package com.example.dumitrunascoproyecto.registrarse

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun bitmapToByteArray(bitmap: Bitmap, quality: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }
}