package com.example.httptest2

import com.example.uphill.data.model.MovementData
import java.time.LocalDate

class ClimbingData(val items: ArrayList<ClimbingDataItem>){
    fun print(){
        println(this.toString())
    }

    override fun toString(): String {
        return items.toString()
    }

    fun getDateData(date:LocalDate): ClimbingData {
        val temp:ArrayList<ClimbingDataItem> = arrayListOf()
        items.forEach{
            val dateFromFormatted = it.getCreatedTime()?.toLocalDate()
            if (dateFromFormatted!=null) {
                if (date == dateFromFormatted) {
                    temp.add(it)
                }
            }
        }
        val ret = ClimbingData(temp)

        return ret
    }

}