package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName

data class BattleRoomDetailInfo(
    val adminId: Int,
    val adminName: String,
    val battleRoomId: Int,
    val content: String,
    val crewId: Int,
    val participantCode: String,
    val participantList: List<Participant>,
    val progress: Boolean,
    val routeId: Int,
    val startTime: String,
    val title: String
)