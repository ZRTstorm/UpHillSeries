package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName

data class BattleRoomClimbingDataItem(
    val climbingDataId: Int,
    val climbingTime: Int,
    val success: Boolean,
    val userName: String
)