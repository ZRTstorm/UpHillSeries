package com.example.httptest2


import com.example.uphill.data.model.MovementDataItem
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        val kstTime = dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault())
        return kstTime.toLocalDateTime()
    }
    fun getClimbingTimeString():String{
        var ret = ""
        var sec = climbingTime
        if(sec>3600){
            val hour = sec/3600
            sec/=3600
            ret+="${hour}시간 "
        }
        if (sec>60){
            val min = sec/60
            sec/=60
            ret+=String.format(Locale.KOREA, "%02d분 ", min)
        }
        ret+= String.format(Locale.KOREA, "%02d초", sec)
        return ret
    }
}