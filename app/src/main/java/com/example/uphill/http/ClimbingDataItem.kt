package com.example.httptest2


import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ClimbingDataItem(
    val climbingTime: Int,
    val createdTime: String,
    val id: Int,
    val routeId: Int,
    val success: Boolean,
    val userId: Int
){
    override fun toString(): String {
        val ret = "id: $id, " +
                "userId: $userId, " +
                "routeId: $routeId, " +
                "success: $success, " +
                "climbingTime: $climbingTime, " +
                "createdTime: $createdTime"
        return ret
    }
    fun getCreatedTime(): LocalDateTime? {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(createdTime, formatter)
        return dateTime
    }
}