package com.example.uphill.data

import android.graphics.Bitmap
import com.example.uphill.data.model.AnimationMovementData
import com.example.uphill.data.model.MovementData
import org.opencv.core.Mat

object AppStatus {
    var isStart = false
    var isEnd = false

    var animationData: AnimationMovementData? = null
    var animationData2: AnimationMovementData? = null

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
    }
}