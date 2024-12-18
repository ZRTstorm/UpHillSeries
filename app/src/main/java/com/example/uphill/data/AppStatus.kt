package com.example.uphill.data

import android.graphics.Bitmap
import com.example.uphill.data.model.AnimationMovementData
import com.example.uphill.data.model.MovementData

object AppStatus {
    var isStart = false
    var isEnd = false

    var animationData: AnimationMovementData? = null
    var animationData2: AnimationMovementData? = null
    var animationRouteId: Int? = null
    var animationProfile: Bitmap? = null
    var animationProfile2: Bitmap? = null
    var animationUserName: String? = null
    var animationUserName2: String? = null

    var originBitmapList: ArrayList<Bitmap>? = null
    var diffBitmapList: ArrayList<Bitmap>? = null
    var lastMovementData: MovementData? = null

    var isOpenCVInitialized = false

    fun initClimbingStatus(){
        isStart = false
        isEnd = false
    }
    fun initAnimationData(){
        animationData = null
        animationData2 = null
        animationRouteId = null
        animationProfile = null
        animationProfile2 = null
        animationUserName = null
        animationUserName2 = null

    }
}