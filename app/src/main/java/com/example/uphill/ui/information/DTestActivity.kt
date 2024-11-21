package com.example.uphill.ui.information

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R
import com.example.uphill.data.AppStatus

class DTestActivity:AppCompatActivity() {
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dtest)

        val originView = findViewById<ImageView>(R.id.originView)
        val diffView = findViewById<ImageView>(R.id.diffView)
        val nextBitmapButton = findViewById<Button>(R.id.nextBitmapButton)
        if(AppStatus.originBitmapList == null || AppStatus.diffBitmapList == null){
            return
        }
        originView.setImageBitmap(AppStatus.originBitmapList!![0])
        diffView.setImageBitmap(AppStatus.diffBitmapList!![0])
        nextBitmapButton.setOnClickListener {
            index++
            if(index >= AppStatus.diffBitmapList!!.size){
                index = 0
            }
            originView.setImageBitmap(AppStatus.originBitmapList!![index])
            diffView.setImageBitmap(AppStatus.diffBitmapList!![index])
            Log.d("DTestActivity", "index: $index/${AppStatus.diffBitmapList!!.size}")
            Log.d("DTestActivity", "origin: ${AppStatus.lastMovementData?.get(index)?.xpos}, ${AppStatus.lastMovementData?.get(index)?.ypos}")

        }
    }
}
