package com.example.uphill.ui.record.server

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class QueueServer(private val userId:Int) {
    private val client = OkHttpClient()
    private val serverUrl = "http://copytixe.iptime.org:8080/"

    fun sendRouteID(userId: Int, climbingData: Map<String, Any>): Boolean {
        // JSON 데이터 생성
        val json = JSONObject()
        json.put("userId", userId)
        for ((key, value) in climbingData) {
            json.put(key, value)
        }

        // JSON RequestBody 생성
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = json.toString().toRequestBody(mediaType)

        // POST 요청 생성
        val request = Request.Builder()
            .url(serverUrl + "climbing/data")
            .post(requestBody)
            .build()

        return try {
            // 요청 실행
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }
                // 요청 성공 여부 반환
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}