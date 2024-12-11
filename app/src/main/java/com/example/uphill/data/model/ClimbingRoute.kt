package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName

data class ClimbingRoute(
    val climbingCenterId: Int,
    val difficulty: String,
    val endX: Int,
    val endY: Int,
    val routeId: Int,
    val startX: Int,
    val startY: Int
)