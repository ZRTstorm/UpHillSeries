package com.example.uphill.objdetection

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.opencv.core.Core
import android.media.MediaMetadataRetriever
import android.net.Uri
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.opencv.core.Size

private const val TAG = "ACTIVITY_DETECTION"

var targetFPS = 4

class ActivityDetection {
    private val portraitModeWidth = 200
    private val portraitModeHeight = 300
    private var threshold = 15.0

    var bitmap: Bitmap? = null

    var locationList: ArrayList<DoubleArray>? = null

    fun setThreshold(th: Double){
        threshold = th
    }

    private fun calcDiff(context: Context, videoUri: Uri): ArrayList<Mat>?{
        // Load the video
        val retriever = MediaMetadataRetriever()
        val frameArrayList = arrayListOf<Bitmap>()
        try {
            // 비디오 파일을 설정
            retriever.setDataSource(context, videoUri)

            // 썸네일 추출
            bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST)
            if(bitmap==null){
                return null
            }
            val image = Mat(bitmap!!.width, bitmap!!.height, CvType.CV_8UC1)
            Utils.bitmapToMat(bitmap, image)
            val resizedImage = Mat()
            val size = Size(portraitModeWidth.toDouble(), portraitModeHeight.toDouble())
            Imgproc.resize(image, resizedImage, size)
            bitmap =
                bitmap!!.config?.let {
                    Bitmap.createBitmap(portraitModeWidth,portraitModeHeight,
                        it
                    )
                }
            Utils.matToBitmap(resizedImage,bitmap)

            // 자르기
            val videoLength = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            Log.d(TAG, "Length: $videoLength")
            if (videoLength != null) {
                for (i: Int in 0..<videoLength.toInt() step (1000/ targetFPS)) {
                    val timeUs = i*1000.toLong()
                    val currBitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
                    if (currBitmap != null) {
                        frameArrayList.add(currBitmap)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 리소스 해제
            retriever.release()
        }
        if (frameArrayList.size>0) {
            Log.d(TAG, "Video splice successes. #"+frameArrayList.size)
        } else {
            Log.e(TAG, "Video splice fail")
            return null
        }

        // Make grayscale
        val grayFrameArrayList = arrayListOf<Mat>()
        frameArrayList.forEach {
            val image = Mat(it.width, it.height, CvType.CV_8UC1)
            val grayimage = Mat()

            Utils.bitmapToMat(it, image)
            val resizedImage = Mat()
            val size = Size(portraitModeWidth.toDouble(), portraitModeHeight.toDouble())

            Imgproc.resize(image, resizedImage, size)
            Imgproc.cvtColor(resizedImage, grayimage, Imgproc.COLOR_RGB2GRAY)

            grayFrameArrayList.add(grayimage)
        }
        if (grayFrameArrayList.size>0) {
            Log.d(TAG, "Video grayscale successes. #"+grayFrameArrayList.size)
        } else {
            Log.e(TAG, "Video grayscale fail")
            return null
        }
        // Calc diff
        val diffList = arrayListOf<Mat>()
        for (i in grayFrameArrayList.indices){
            if (i<2) continue
            val dst = Mat()
            Core.absdiff(grayFrameArrayList[i-2], grayFrameArrayList[i], dst)
            //Log.d(TAG,dst.rows().toString()+","+dst.cols().toString())
            diffList.add(dst)
        }
        if (diffList.size>0) {
            Log.d(TAG, "Video diff calc successes. #"+diffList.size)
        } else {
            Log.e(TAG, "Video diff calc fail")
            return null
        }

        return diffList
    }
    private fun calcOneDiffCenter(diff:Mat):DoubleArray{
        var sumX = 0.0
        var sumY = 0.0
        var weight = 0.0
        var count = 0
        for (y:Int in 0..<diff.height()){
            for (x:Int in 0..<diff.width()){
                val value = diff.get(y,x)[0]
                if (value>threshold){
                    sumX+=x.toDouble()
                    sumY+=y.toDouble()
                    count++
                }
                weight += value
            }
        }
        val avgX = sumX/count
        val avgY = sumY/count
        //val avgWeight = weight/diff.height()/diff.width()
        val ret = doubleArrayOf(avgY,avgX)

        //Log.d(TAG, "$avgX, $avgY, avg val: $avgWeight")
        return ret
    }
    private suspend fun calcDiffCenter(diffList: ArrayList<Mat>):ArrayList<DoubleArray>{
        val ret:ArrayList<DoubleArray> = arrayListOf()
        for (i:Int in 0..<diffList.size){
            val loc = calcOneDiffCenter(diffList[i])
            ret.add(loc)
        }
        return ret
    }
    suspend fun detect(context: Context, videoUri: Uri){
        val diffList = calcDiff(context, videoUri)
        if(diffList==null){
            Log.e(TAG, "Different calculating fail")
            return
        }
        locationList = calcDiffCenter(diffList)
    }

    fun printLocationLogs(){
        if(locationList==null){
            Log.e(TAG, "not yet calculated")
            return
        }
        var cnt = 0
        locationList!!.forEach {
            Log.d(TAG,"${cnt++}: (${it[1].toInt()},${it[0].toInt()})")
        }
    }
}
