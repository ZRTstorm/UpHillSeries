package com.example.uphill.data

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseUser

object UserInfo {
    var user: FirebaseUser? = null
    var userId: Int? = null
    var photo: Bitmap? = null
    var capturedRouteId: Int? = null
    var lastClimbingId: Int? = 1
    var isCompetitionEntered: Boolean = false
}