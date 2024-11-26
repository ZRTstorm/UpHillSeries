package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName

data class SimpleCrewInfoItem(
    val content: String,
    val crewId: Int,
    val crewName: String,
    val userName: String
)