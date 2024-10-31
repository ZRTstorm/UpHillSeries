package com.example.uphill.objdetection

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import kotlin.math.round

private const val TAG = "USER_ANIMATOR"

class UserAnimator(val view:ImageView, val location:ArrayList<DoubleArray>, val climbingRoute: ClimbingRoute) {
    private val basicMagnifier = 4.7
    private var xMagnifier = 4.0
    private var yMagnifier = 4.0
    var duration = (500/ targetFPS).toLong()

    var startTime = 0.0
    var endTime = -1.0

    var profileBitmap:Bitmap? = null
    var animator:Animator? = null

    private val firstViewX:Float = (climbingRoute.start.x*basicMagnifier).toFloat()
    private val firstViewY:Float = (climbingRoute.start.y*basicMagnifier).toFloat()

    private fun loadDefaultProfile(){
        //val image = BitmapFactory.decodeResource(r, R.raw.default_profile)
    }

    private fun startFrame():Int{
        val expectedFrame = startTime * targetFPS
        return round(expectedFrame).toInt()
    }
    private fun endFrame():Int{
        if(endTime<0.0){
            return location.size-1
        }
        var expectedFrame = endTime * targetFPS
        if(expectedFrame>location.size-1){
            return location.size-1
        }
        return round(expectedFrame).toInt()
    }

    private fun calcMag(){
        val expectedXDiff = climbingRoute.end.x - climbingRoute.start.x
        val expectedYDiff = climbingRoute.end.y - climbingRoute.start.y

        val realXDiff = location[endFrame()][1] - location[startFrame()][1]
        val realYDiff = location[endFrame()][0] - location[startFrame()][0]

        Log.d(TAG, "From: (${location[startFrame()][1]},${location[startFrame()][0]})")
        Log.d(TAG, "To  : (${location[endFrame()][1]},${location[endFrame()][0]})")

        xMagnifier=basicMagnifier * expectedXDiff/realXDiff
        yMagnifier=basicMagnifier * expectedYDiff/realYDiff
    }
    suspend fun calc(){
        val movingPath:ArrayList<DoubleArray> = arrayListOf()

        calcMag()
        for (i:Int in startFrame()..endFrame()){
            var y = location[i+1][0] - location[i][0]
            var x = location[i+1][1] - location[i][1]
            y*=yMagnifier
            x*=xMagnifier
            movingPath.add(doubleArrayOf(y,x))
        }
        var nowX = view.x
        var nowY = view.y
        val path = android.graphics.Path().apply {
            moveTo(nowX,nowY)
            movingPath.forEach{
                nowX+=it[1].toFloat()
                nowY+=it[0].toFloat()
                lineTo(nowX,nowY)
            }
        }
        animator = ObjectAnimator.ofFloat(view, View.X, View.Y, path)
        animator!!.duration = duration * movingPath.size

        view.visibility = View.VISIBLE

//        val xAnimatorList:ArrayList<ObjectAnimator> = arrayListOf()
//        val yAnimatorList:ArrayList<ObjectAnimator> = arrayListOf()
//        for(i:Int in startFrame()..endFrame()) {
//            val xAnimator = ObjectAnimator.ofFloat(view, "translationX", location[i][1].toFloat())
//            val yAnimator = ObjectAnimator.ofFloat(view, "translationY", location[i][0].toFloat())
//            xAnimator.duration = duration
//            yAnimator.duration = duration
//            xAnimator.interpolator = LinearInterpolator()
//            yAnimator.interpolator = LinearInterpolator()
//            xAnimatorList.add(xAnimator)
//            yAnimatorList.add(yAnimator)
//        }
//        val animatorSet = AnimatorSet()
//        animatorSet.play(xAnimatorList[0]).with(yAnimatorList[0])
//        for (i:Int in 1..<xAnimatorList.size){
//            animatorSet.play(xAnimatorList[i]).after(yAnimatorList[i-1])
//            animatorSet.play(xAnimatorList[i]).with(yAnimatorList[i])
//        }
//        animatorSet.playSequentially(xAnimatorList as List<Animator>?)
//
//        animatorSet.start()
    }
    fun start(){
        if (animator!=null) {
            animator!!.start()
        }
    }
    fun reset(){
        view.visibility = View.INVISIBLE
        val path = android.graphics.Path().apply {
            moveTo(firstViewX, firstViewY)
        }
        val animator = ObjectAnimator.ofFloat(view, View.X, View.Y, path)
        animator.start()

    }
    suspend fun startToEndTest(){
        val duration:Long = 5000
        var y = location[endFrame()][0] - location[startFrame()][0]
        var x = location[endFrame()][1] - location[startFrame()][1]
        y*=yMagnifier
        x*=xMagnifier
        Log.d(TAG, "move to $x, $y")
        val path = android.graphics.Path().apply {
            moveTo(view.x, view.y)
            lineTo(view.x + x.toFloat(), view.y + y.toFloat())
        }
        val animator = ObjectAnimator.ofFloat(view, View.X, View.Y, path)
        animator.duration = duration

        animator.start()
    }
}