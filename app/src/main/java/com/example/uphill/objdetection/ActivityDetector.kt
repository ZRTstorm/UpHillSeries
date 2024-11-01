package com.example.uphill.objdetection

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log

object ActivityDetector {
    private const val TAG = "ACTIVITY_DETECTOR"
    private val activityDetection = ActivityDetection()

    var id = 0
    var isProcessing = false
    var thumbnail: Bitmap? = null

    suspend fun detect(context: Context, videoUri: Uri):ArrayList<DoubleArray>?{
        if(isProcessing){
            Log.d(TAG, "detector has been processing")
            return null
        }
        isProcessing = true
        activityDetection.detect(context,videoUri)
        thumbnail = activityDetection.bitmap
        isProcessing = false
        return activityDetection.locationList
    }
}