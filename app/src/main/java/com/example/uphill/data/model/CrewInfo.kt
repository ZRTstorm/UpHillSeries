package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName

data class CrewInfo(
    val crewContent: String,
    val crewId: Int,
    val crewManList: List<CrewMan>,
    val crewName: String,
    val password: Any,
    val pilotId: Int,
    val pilotName: String
)