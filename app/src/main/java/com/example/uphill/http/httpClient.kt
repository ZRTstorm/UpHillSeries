package com.example.httptest2

import android.util.Log
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.UserId
import com.example.uphill.http.SocketClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.ArrayList


class HttpClient(private val userId:Int) {
    private val client = OkHttpClient()
    private val server_name = "http://$SERVER_NAME:8080/"

    fun getClimbingData():ClimbingData?{

        var climbingData: ClimbingData? = null
        val request = Request.Builder()
            .url(server_name+"climbing/users/"+userId+"/data")
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                val jsonResponse = response.body?.string()
                val listType = object:TypeToken<List<ClimbingDataItem>>() {}.type
                val temp:List<ClimbingDataItem> = Gson().fromJson(jsonResponse, listType)
                climbingData = ClimbingData(ArrayList(temp))
            }
        } catch (e:Exception){
            e.printStackTrace()
        }
        return climbingData
    }

    fun login(idToken:String){
        val json = """
            {
                "idToken": "$idToken"
            }
        """
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())

        //Log.d(TAG, json)
        val request = Request.Builder()
            .url(server_name+"users/auth/login")
            .addHeader("Accept", "*/*")
            .post(requestBody)
            .build()

        //Log.d(TAG, "Try login")
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(TAG, "Login failed: ${e.message}")
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if(response.isSuccessful){
                    val responseData = response.body?.string()
                    Log.d(TAG, responseData.toString())
                    val gson = Gson()
                    val userId = gson.fromJson(responseData, UserId::class.java)
                    UserInfo.userId = userId.userId
                    SocketClient.connect()
                } else{
                    Log.e(TAG, "Request failed. ${response.code}")
                }
            }

        })
    }
    companion object{
        const val TAG = "HTTP_CLIENT"
        const val SERVER_NAME = "copytixe.iptime.org"
    }
}