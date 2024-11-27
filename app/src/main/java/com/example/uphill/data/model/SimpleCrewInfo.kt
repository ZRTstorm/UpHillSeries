package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName

data class SimpleCrewInfo(
    @SerializedName("crews")
    val crews: List<SimpleCrewInfoItem>
)
