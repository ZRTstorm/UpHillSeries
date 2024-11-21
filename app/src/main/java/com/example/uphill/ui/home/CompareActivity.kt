package com.example.uphill.ui.home

import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uphill.R
import com.example.uphill.data.AppStatus
import com.example.uphill.data.Convert
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.RouteImageData
import com.example.uphill.objdetection.ClimbingRoute
import com.example.uphill.objdetection.UserAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CompareActivity : AppCompatActivity() {
    private var httpJob: Job = Job()
    private val httpScope = CoroutineScope(Dispatchers.IO + httpJob)
    private var routeImageData: RouteImageData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare)

        if(AppStatus.animationData==null){
            Log.d(TAG, "animationData is null")
            showAlertAndGoBack()
        } else{
            if(AppStatus.animationData2==null){
                Log.d(TAG, "animationData2 is null")
                showSingleAnimation()
            }
            else{
                Log.d(TAG, "animationData is both not null")
                showCompareAnimation()
            }
        }
    }

    private fun showAlertAndGoBack() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage("데이터가 없습니다.")
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
            Handler(Looper.getMainLooper()).postDelayed({
                finish() // 현재 액티비티를 종료하여 이전 화면으로 돌아감
            }, 1000) // 5초 지연
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
    private fun showSingleAnimation(){
        val movingView = findViewById<ImageView>(R.id.movingView)
        val parentView = findViewById<ImageView>(R.id.imageView4)
        //movingView.setImageBitmap(UserInfo.photo)
        Glide.with(this)
            .load(UserInfo.photo)
            .transform(com.bumptech.glide.load.resource.bitmap.CircleCrop())
            .into(movingView)
        httpScope.launch {
            setBackgroundImage()
            val climbingRoute = ClimbingRoute(null, Point(routeImageData!!.startX, routeImageData!!.startY), Point(routeImageData!!.endX, routeImageData!!.endY))
            Log.d(TAG, "calc start")
            parentView.post{
                val userAnimator = UserAnimator(movingView, parentView, AppStatus.animationData!!.movementData.convertToDoubleArrayList(), climbingRoute)

                Log.d(TAG, "parent view: ${parentView.width}, ${parentView.height}, location: ${parentView.x}, ${parentView.y}")
                Log.d(TAG, "moving view: ${movingView.width}, ${movingView.height}, location: ${movingView.x}, ${movingView.y}")

                userAnimator.calc()
                val handler = Handler(Looper.getMainLooper())
                handler.post{
                    userAnimator.start()
                }
            }
        }


    }
    private fun showCompareAnimation(){
        val movingView = findViewById<ImageView>(R.id.movingView)
        val movingView2 = findViewById<ImageView>(R.id.movingView2)
        val parentView = findViewById<ImageView>(R.id.imageView4)

        movingView.setImageBitmap(UserInfo.photo)
        movingView2.setImageBitmap(UserInfo.photo)

        httpScope.launch {
            setBackgroundImage()
            val climbingRoute = ClimbingRoute(null, Point(routeImageData!!.startX, routeImageData!!.startY), Point(routeImageData!!.endX, routeImageData!!.endY))
            Log.d(TAG, "calc start")
            parentView.post{
                val userAnimator = UserAnimator(movingView, parentView, AppStatus.animationData!!.movementData.convertToDoubleArrayList(), climbingRoute)
                val userAnimator2 = UserAnimator(movingView2, parentView, AppStatus.animationData2!!.movementData.convertToDoubleArrayList(), climbingRoute)
                userAnimator2.xOffset = 0.0f
                userAnimator2.yOffset = dpToPx(applicationContext, 50f)

                userAnimator.calc()
                userAnimator2.calc()

                val animatorSet = android.animation.AnimatorSet()
                animatorSet.playTogether(userAnimator.animator, userAnimator2.animator)

                val handler = Handler(Looper.getMainLooper())
                handler.post{
                    animatorSet.start()
                }
            }


        }

    }
    private suspend fun setBackgroundImage(){
        val backgroundView = findViewById<ImageView>(R.id.imageView4)
        val httpClient = com.example.httptest2.HttpClient()
        if(AppStatus.animationRouteId==null){
            Log.d(TAG, "animationRouteId is null")
            return
        }
        routeImageData = httpClient.getRouteImageData(AppStatus.animationRouteId!!)
        if(routeImageData==null){
            Log.d(TAG, "imageBase64 is null")
            return
        }
        val image = Convert.base64ToBitmap(routeImageData!!.imageData)
        val handler = Handler(Looper.getMainLooper())
        handler.post{
        backgroundView.setImageBitmap(image)
    }

    }
    private fun dpToPx(context: android.content.Context, dp: Float): Float {
        val density = context.resources.displayMetrics.density
        return dp * density
    }


    override fun onDestroy() {
        super.onDestroy()
        AppStatus.initAnimationData()
    }

    companion object{
        private const val TAG = "CompareActivity"

    }
}