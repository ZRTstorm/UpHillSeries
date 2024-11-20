package com.example.uphill.ui.home

import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R
import com.example.uphill.data.AppStatus
import com.example.uphill.data.UserInfo
import com.example.uphill.objdetection.ClimbingRoute
import com.example.uphill.objdetection.UserAnimator

class CompareActivity : AppCompatActivity() {
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
                Log.d(TAG, "animationData is not null")
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
            }, 5000) // 5초 지연
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
    private fun showSingleAnimation(){
        val movingView = findViewById<ImageView>(R.id.movingView)
        movingView.setImageBitmap(UserInfo.photo)

        val testClimbingRoute = ClimbingRoute(null, Point(0, 0), Point(30, 30))

        val userAnimator = UserAnimator(movingView, AppStatus.animationData!!.movementData.convertToDoubleArrayList(), testClimbingRoute)


        Log.d(TAG, "calc start")
        userAnimator.calc()
        //userAnimator.animator?.start()
        userAnimator.startToEndTest(50.0, 50.0)

    }
    private fun showCompareAnimation(){

    }



    override fun onDestroy() {
        super.onDestroy()
        AppStatus.initAnimationData()
    }

    companion object{
        private const val TAG = "CompareActivity"

    }
}