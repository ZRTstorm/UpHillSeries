package com.example.uphill.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object Convert {
    @OptIn(ExperimentalEncodingApi::class)
    fun bitmapToBase64(bitmap: Bitmap):String{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encode(byteArray)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun base64ToBitmap(base64:String):Bitmap?{
        try {
            val byteArray = Base64.decode(base64)
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e:Exception){
            Log.e("CONVERT", "cannot convert base64 to bitmap", e)
            return null
        }
    }
}