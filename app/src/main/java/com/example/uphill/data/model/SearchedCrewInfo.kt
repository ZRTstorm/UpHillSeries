package com.example.uphill.data.model


import com.google.gson.annotations.SerializedName

class SearchedCrewInfo : ArrayList<SearchedCrewInfoItem>(){
    fun toSimpleCrewInfo(): List<SimpleCrewInfoItem> {
        val simpleCrews = this.map {
            SimpleCrewInfoItem(it.content, it.crewId, it.crewName, it.userName)
        }
        return simpleCrews
    }
}