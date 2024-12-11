package com.example.uphill.ui.record

import android.graphics.Bitmap
import com.example.uphill.data.model.RouteImageData

object QueueStatus {
    var isRegistered: Boolean = false
    var nowPosition: Int? = null
    var routeId: Int? = null
    var routeImage: RouteImageData? = null

    fun reset() {
        isRegistered = false
        nowPosition = null
        routeId = null
        routeImage = null
    }
}