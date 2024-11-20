package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName

class MovementData : ArrayList<MovementDataItem>(){
    fun convertToDoubleArrayList(): ArrayList<DoubleArray> {
        val data = this
        val arrayList = ArrayList<DoubleArray>()
        data.sortedBy { it.sequence }.forEach { item ->
            val doubleArray = doubleArrayOf(item.ypos.toDouble(), item.xpos.toDouble())
            arrayList.add(doubleArray)
        }
        return arrayList
    }
}