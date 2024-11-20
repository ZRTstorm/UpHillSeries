package com.example.uphill.data

import com.example.uphill.data.model.AnimationMovementData

object AppStatus {
    var isStart = false
    var isEnd = false

    var animationData: AnimationMovementData? = null
    var animationData2: AnimationMovementData? = null

    var isOpenCVInitialized = false

    fun initClimbingStatus(){
        isStart = false
        isEnd = false
    }
    fun initAnimationData(){
        animationData = null
        animationData2 = null
    }
}