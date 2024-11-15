package com.example.uphill.http

import android.util.Log
import com.example.httptest2.HttpClient.Companion.SERVER_NAME
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 서버로 토큰을 전송하는 메서드 호출
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // 메시지 수신 처리
    }

    private fun sendRegistrationToServer(token: String) {
        // 서버로 토큰을 전송하는 로직 구현
        // 예: OkHttp를 사용하여 POST 요청으로 토큰 전송
        val client = OkHttpClient()
        val json = """
            {
                "idToken": "$token"
            }
        """
        Log.d(TAG, json)
        val requestBody: RequestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("http://$SERVER_NAME:8080/users/auth/login")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            Log.d(TAG, "Token sent to server successfully")
            // 서버 응답 처리
        }
    }
    companion object{
        const val TAG = "FirebaseMessagingService"

    }
}
