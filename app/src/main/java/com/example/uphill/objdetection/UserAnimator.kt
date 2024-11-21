package com.example.uphill.objdetection

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Interpolator
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import kotlin.math.round

private const val TAG = "USER_ANIMATOR"

// Y position is first, and X position is second
class UserAnimator(val view:ImageView, val parentView:ImageView, val location:ArrayList<DoubleArray>, val climbingRoute: ClimbingRoute) {
    private val basicMagnifier =  1.5F
    private var xMagnifier = 1.0
    private var yMagnifier = 1.0
    var duration = (500/ targetFPS).toLong()

    private val topY = parentView.y + parentView.height/2 - parentView.width * 0.75F - view.height/2
    private val botY = parentView.y + parentView.height/2 + parentView.width * 0.75F - view.height/2
    private val leftX = 0.0F - view.width/2.0F
    private val rightX = (parentView.width - view.width/2).toFloat()

    var xOffset = 0.0F
    var yOffset = 0.0F

    var startTime = 0.0
    var endTime = -1.0

    var profileBitmap:Bitmap? = null
    var animator:Animator? = null

    private val firstViewX:Float = parentView.x + (climbingRoute.start.x*basicMagnifier).toFloat()
    private val firstViewY:Float = parentView.y + parentView.height - (climbingRoute.start.y*basicMagnifier).toFloat()

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

        xMagnifier=expectedXDiff/realXDiff
        yMagnifier=expectedYDiff/realYDiff
        Log.d(TAG, "xMagnifier: $xMagnifier")
        Log.d(TAG, "yMagnifier: $yMagnifier")
    }
    fun calc(){
        val movingPath:ArrayList<DoubleArray> = arrayListOf()

        calcMag()

        location.forEach{
            val x = (it[1] - location[startFrame()][1]) * xMagnifier
            val y = (it[0] - location[startFrame()][0]) * yMagnifier
            movingPath.add(doubleArrayOf(x, y))
        }

        val startX = getRelativeLocationX(climbingRoute.start.x)
        val startY = getRelativeLocationY(climbingRoute.start.y)
        val endX = getRelativeLocationX(climbingRoute.end.x)
        val endY = getRelativeLocationY(climbingRoute.end.y)

        val path = android.graphics.Path().apply {
            moveTo(xOffset + startX, yOffset + startY)
            movingPath.forEach{
                lineTo(xOffset + getRelativeLocationX(it), yOffset + getRelativeLocationY(it))
            }

            lineTo(endX, endY)
        }
        animator = ObjectAnimator.ofFloat(view, View.X, View.Y, path)
        animator!!.duration = duration * location.size
        animator!!.interpolator = LinearInterpolator()

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
        Log.d(TAG, "reset to $firstViewX, $firstViewY")
    }
    fun moveTest(){
        val d = 4000L
        val path = android.graphics.Path().apply {
            moveTo(leftX, topY)
            lineTo(rightX, topY)
            lineTo(rightX, botY)
            lineTo(leftX, botY)
            lineTo(leftX, topY)
        }
        val animator = ObjectAnimator.ofFloat(view, View.X, View.Y, path)
        animator.duration = d
        animator.start()
    }
    private fun getRelativeLocationX(loc:Int):Float{
        val relativeX = leftX + loc * (rightX - leftX) / ActivityDetection.portraitSize.width

        return relativeX
    }
    private fun getRelativeLocationY(loc:Int):Float{
        val relativeY = botY - (loc * (botY - topY) / ActivityDetection.portraitSize.height)

        return relativeY
    }
    private fun getRelativeLocationX(loc:DoubleArray):Float{
        val x = loc[1]
        val relativeX = leftX + x * (rightX-leftX)/ActivityDetection.portraitSize.width

        return relativeX.toFloat()
    }
    private fun getRelativeLocationY(loc:DoubleArray):Float{
        val y = loc[0]
        val relativeY = botY - (y * (botY-topY)/ActivityDetection.portraitSize.height)

        return relativeY.toFloat()
    }

    fun startToEndTest(){
        val duration:Long = 5000
//        var y = location[endFrame()][0] - location[startFrame()][0]
//        var x = location[endFrame()][1] - location[startFrame()][1]
//        y*=yMagnifier
//        x*=xMagnifier

        val primeX = view.x
        val primeY = view.y

        val expectedXDiff = climbingRoute.end.x - climbingRoute.start.x
        val expectedYDiff = climbingRoute.end.y - climbingRoute.start.y
        var y = expectedYDiff * basicMagnifier
        var x = expectedXDiff * basicMagnifier

        parentView.post{
                Log.d(TAG, "view: ${view.x}, ${view.y}")
                Log.d(TAG, "parent: ${parentView.x}, ${parentView.y} / ${parentView.width}, ${parentView.height}")
                Log.d(TAG, "start: $primeX, $primeY")
                Log.d(TAG, "move to $x, $y")
        }

        Log.d(TAG, "view: ${view.x}, ${view.y}")
        Log.d(TAG, "parent: ${parentView.x}, ${parentView.y} / ${parentView.width}, ${parentView.height}")
        Log.d(TAG, "start: $primeX, $primeY")
        Log.d(TAG, "move to $x, $y")
        val path = android.graphics.Path().apply {
            moveTo(primeX, primeY)
            lineTo(primeX+ x.toFloat(), primeY - y.toFloat())
        }
        val animator = ObjectAnimator.ofFloat(view, View.X, View.Y, path)
        animator.duration = duration

        animator.start()
    }
}