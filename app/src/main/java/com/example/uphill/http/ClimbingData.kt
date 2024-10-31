package com.example.httptest2

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
            val dateFromFormatted = LocalDate.parse(it.createdTime, DateTimeFormatter.ISO_DATE_TIME.withZone(
                ZoneId.of("UTC")))
            if(date == dateFromFormatted){
                temp.add(it)
            }
        }
        val ret = ClimbingData(temp)

        return ret
    }

}