package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName
import java.util.Locale

data class BattleRoomClimbingDataItem(
    val climbingDataId: Int,
    val climbingTime: Int,
    val success: Boolean,
    val userName: String,
    val userId: Int
){
    fun getClimbingTimeString():String{
        var ret = ""
        var sec = climbingTime/1000
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