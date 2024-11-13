package com.example.uphill.objdetection

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ActivityDetector {
    private const val TAG = "ACTIVITY_DETECTOR"
    private val activityDetection = ActivityDetection()

    var id = 0
    var isProcessing = false
    var thumbnail: Bitmap? = null

    fun detect(videoPath: String, callback: (Boolean) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            if (isProcessing) {
                Log.d(TAG, "detector has been processing")
                return@launch
            }
            Log.d(TAG, "detector start")

            isProcessing = true

            try {
                activityDetection.detectFromFile(videoPath)
                thumbnail = activityDetection.bitmap
                isProcessing = false
                callback(true)
            } catch (e: Exception){
                Log.e(TAG, "Exception: $e")
                isProcessing = false
                callback(false)
            }

        }
    }
    fun detectImages(bitmapArray: ArrayList<Bitmap>, callback: (Boolean) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            if (isProcessing) {
                Log.d(TAG, "detector has been processing")
                return@launch
            }
            Log.d(TAG, "detector start")
            isProcessing = true
            try {
                activityDetection.detect(bitmapArray)
                thumbnail = activityDetection.bitmap
                isProcessing = false
                Log.d(TAG, "detector end")
                activityDetection.printLocationLogs()
                callback(true)
            } catch (e: Exception){
                Log.e(TAG, "Exception: $e")
                isProcessing = false
                callback(false)
            }
        }
    }
}