package com.example.uphill.data.model


import android.graphics.Bitmap
import com.example.uphill.data.Convert
import com.google.gson.annotations.SerializedName

data class RouteImageData(
    val endX: Int,
    val endY: Int,
    val imageData: String,
    val startX: Int,
    val startY: Int
){
    fun toBitmap(): Bitmap? {
        return Convert.base64ToBitmap(imageData)
    }
}