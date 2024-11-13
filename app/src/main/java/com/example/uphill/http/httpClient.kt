package com.example.httptest2

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
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
    companion object{
        const val TAG = "HTTP_CLIENT"
        const val SERVER_NAME = "copytixe.iptime.org"
    }
}