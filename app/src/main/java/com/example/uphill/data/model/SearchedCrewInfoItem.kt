package com.example.uphill.data.model


import android.os.Parcel
import android.os.Parcelable

data class SearchedCrewInfoItem(
    val content: String,
    val crewId: Int,
    val crewName: String,
    val userName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(content)
        parcel.writeInt(crewId)
        parcel.writeString(crewName)
        parcel.writeString(userName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchedCrewInfoItem> {
        override fun createFromParcel(parcel: Parcel): SearchedCrewInfoItem {
            return SearchedCrewInfoItem(parcel)
        }

        override fun newArray(size: Int): Array<SearchedCrewInfoItem?> {
            return arrayOfNulls(size)
        }
    }
}