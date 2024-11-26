package com.example.uphill.data.model


data class BattleRoomData(
    val adminName: String,
    val battleRoomId: Int,
    val content: String,
    val progress: Boolean,
    val routeId: Int,
    val title: String
)