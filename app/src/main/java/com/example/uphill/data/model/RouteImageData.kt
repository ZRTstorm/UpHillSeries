package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName

data class RouteImageData(
    val endX: Int,
    val endY: Int,
    val imageData: String,
    val startX: Int,
    val startY: Int
)