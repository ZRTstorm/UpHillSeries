package com.example.uphill.data.model


import com.example.httptest2.ClimbingData
import com.example.httptest2.ClimbingDataItem
import com.google.gson.annotations.SerializedName

class BattleRoomClimbingData:ArrayList<BattleRoomClimbingDataItem>(){
    fun sort():BattleRoomClimbingData{
        val data = BattleRoomClimbingData()
        data.addAll(this.sortedBy { it.climbingTime })
        return data
    }
}