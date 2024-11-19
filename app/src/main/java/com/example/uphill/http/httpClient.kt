package com.example.httptest2

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat.startForegroundService
import com.example.uphill.data.Convert
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.BattleRoomData
import com.example.uphill.data.model.BattleRoomDataList
import com.example.uphill.data.model.BattleRoomRegistryReceivedData
import com.example.uphill.data.model.BattleRoomRegistrySendData
import com.example.uphill.data.model.ClimbingRoute
import com.example.uphill.data.model.MovementData
import com.example.uphill.data.model.UserId
import com.example.uphill.http.SocketClient
import com.example.uphill.http.WebSocketService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.ArrayList


class HttpClient {
    private val client = OkHttpClient()
    private val server_name = "http://$SERVER_NAME:8080"

    // users-controller
    fun login(idToken:String, context: Context){
        val url = "$server_name/users/auth/login"
        val json = """
            {
                "idToken": "$idToken"
            }
        """
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "*/*")
            .post(requestBody)
            .build()

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
                    val serviceIntent = Intent(context, WebSocketService::class.java)
                    startForegroundService(context, serviceIntent)
                    Log.d(TAG, "Login success. ID: ${userId.userId}")
                } else{
                    Log.e(TAG, "Request failed. ${response.code}")
                }
            }
        })
    }

    // sensor-controller
    // route-Controller
    fun uploadRouteImage(image: Bitmap, routeId:Int){
        val url = "$server_name/routes/$routeId/upload"
        val json = """
            "${Convert.bitmapToBase64(image)}"
        """

        post(url,json, "Upload route image success")
    }
    fun registerRouteGroup(firstRouteId:Int, secondRouteId:Int){
        val url = "$server_name/routes/routeGroup/register"
        val json = """
            {
                "route1": $firstRouteId,
                "route2": $secondRouteId
            }
        """
        post(url, json, "Register route group success")
    }
    fun registerRoute(route: ClimbingRoute){
        val url = "$server_name/routes/route/register"
        val json = Gson().toJson(route)
        post(url, json, "Register route success")
    }
    fun registerCenter(){}
    fun getRouteImage(routeId:Int):Bitmap?{
        val url = "$server_name/routes/$routeId/routeImage"
        fun op(response:Response):Bitmap? {
            Log.d(TAG, "Get route image success")
            if (response.body == null) return null
            return Convert.base64ToBitmap(response.body!!.string())
        }
        return get(url, ::op)
    }
    fun getCenterImage(centerId:Int):Bitmap?{
        val url = "$server_name/routes/$centerId/centerImage"
        fun op(response:Response):Bitmap? {
            Log.d(TAG, "Get center image success")
            if (response.body == null) return null
            return Convert.base64ToBitmap(response.body!!.string())
        }
        return get(url, ::op)
    }
    fun getAllRoute():List<ClimbingRoute>?{
        val url = "$server_name/routes/route"

        fun op(response:Response):List<ClimbingRoute>?{
            Log.d(TAG, "Get all route success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            val listType = object:TypeToken<List<ClimbingRoute>>() {}.type
            return Gson().fromJson(jsonResponse, listType)
        }

        return get(url, ::op)
    }
    fun getRouteGroup(routeId: Int):List<Int>?{
        val url = "$server_name/routes/routeGroup/$routeId"

        fun op(response:Response):List<Int>{
            Log.d(TAG, "Get route group success")
            if (response.body == null) return emptyList()
            val jsonResponse = response.body?.string()
            val listType = object:TypeToken<List<Int>>() {}.type
            return Gson().fromJson(jsonResponse, listType)
        }

        return get(url, ::op)
    }
    //fun getRouteInCenter(centerId: Int):List<Int>{}
    //fun getAllCenter():List<Int>{}
    fun deleteRoute(routeId: Int){
        val url = "$server_name/routes/route/$routeId"

        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                Log.d(TAG, "Delete route success")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // entry-queue-controller
    fun rejectEntry(){
        val url = "$server_name/entryQueue/${UserInfo.userId}/reject"

        val json = """ """
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader("accept","*/*")
            .post(requestBody)
            .build()
        Log.d(TAG, request.body.toString())
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                Log.d(TAG, "Reject success")
            }
        }catch (e: Exception){
            Log.e(TAG, "Reject failed", e)
        }
    }
    fun registerEntry(routeId: Int){
        val url = "$server_name/entryQueue/register"
        val json = """
            {
                "userId": ${UserInfo.userId},
                "routeId": $routeId
            }
            """
        post(url, json,"Register entry success")
    }
    // climbing-data-controller
    fun getClimbingData():ClimbingData?{
        val url = server_name+"/climbing/users/${UserInfo.userId}/data"
        fun op(response:Response):ClimbingData?{
            Log.d(TAG, "Get climbing data success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            val listType = object:TypeToken<List<ClimbingDataItem>>() {}.type
            val temp:List<ClimbingDataItem> = Gson().fromJson(jsonResponse, listType)
            return ClimbingData(ArrayList(temp))
        }
        return get(url, ::op)
    }
    // body-movement-controller
    fun notifyFail(){
        val url = server_name+UserInfo.userId.toString()+"/failure"

        val json = """ """
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())


        val request = Request.Builder()
            .url(url)
            .patch(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                Log.d(TAG, "Notify fail success")
            }
            } catch (e: Exception) {
            Log.e(TAG, "Error Occurred", e)
        }
    }
    fun postMovementData(movementData: MovementData){
        val url = server_name+"/bodyMovement/"+UserInfo.userId+"/${UserInfo.lastClimbingId}"
        val json = Gson().toJson(movementData)
        post(url, json, "Send movement data success")
        UserInfo.lastClimbingId = null
    }
    fun getMovementData(climbingDataId: Int):MovementData?{
        val url = server_name+"/bodyMovement/$climbingDataId"
        fun op(response:Response):MovementData?{
            Log.d(TAG, "Get movement data success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, MovementData::class.java)
        }
        return get(url, ::op)
    }

    // battle-room-controller
    fun participantBattleRoom(battleRoomId: Int){
        val url = "$server_name/battleRoom/${UserInfo.userId}/$battleRoomId/participant"
        fun op(response: Response){
            Log.d(TAG, "participant success")
        }
        post(url, ::op)
    }
    fun registryBattleRoom(battleRoomRegistrySendData: BattleRoomRegistrySendData):BattleRoomRegistryReceivedData?{
        val url = "$server_name/battleRoom/${UserInfo.userId}/registry"
        val json = Gson().toJson(battleRoomRegistrySendData)
        fun op(response: Response):BattleRoomRegistryReceivedData?{
            Log.d(TAG, "registry success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, BattleRoomRegistryReceivedData::class.java)
        }
        return post(url, json, ::op)
    }
    fun finishBattle(battleRoomId: Int){
        val url = "$server_name/battleRoom/${UserInfo.userId}/$battleRoomId/end"
        fun op(response: Response){
            Log.d(TAG, "finish success")
        }
        patch(url, ::op)
    }
    fun getAllBattleRoom():BattleRoomDataList?{
        val url = "$server_name/battleRoom/${UserInfo.userId}/all"
        fun op(response: Response):BattleRoomDataList?{
            Log.d(TAG, "get all success")
            if (response.body == null) return BattleRoomDataList()
            val jsonResponse = response.body?.string()
            val listType = object:TypeToken<BattleRoomDataList>() {}.type
            return Gson().fromJson(jsonResponse, listType)
        }
        return get(url, ::op)
    }
    fun getRoomFromCode(participantCode: String): BattleRoomData?{
        val url = "$server_name/battleRoom/$participantCode"
        fun op(response: Response):BattleRoomData?{
            Log.d(TAG, "get room from code success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, BattleRoomData::class.java)
        }
        return get(url, ::op)
    }
    fun getRoomFromCrewId(crewId: Int):BattleRoomDataList?{
        val url = "$server_name/battleRoom/$crewId/all"
        fun op(response: Response):BattleRoomDataList?{
            Log.d(TAG, "get room from crewId success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            val listType = object:TypeToken<BattleRoomDataList>() {}.type
            return Gson().fromJson(jsonResponse, listType)
        }

        return get(url, ::op)
    }
    fun deleteBattleRoom(battleRoomId: Int){
        val url = "$server_name/battleRoom/${UserInfo.userId}/$battleRoomId"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                Log.d(TAG, "Delete room success")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    companion object{
        const val TAG = "HTTP_CLIENT"
        const val SERVER_NAME = "copytixe.iptime.org"
    }

    private fun <R> get(url: String, operation:(Response) -> R): R?{
        val request = Request.Builder()
            .url(url)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                return operation(response)
            }
        } catch (e:Exception){
            Log.e(TAG, "Error Occurred", e)
            return null
        }
    }
    private fun <R> post(url: String, operation: (Response) -> R): R?{
        val json = """ """
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("accept", "*/*")
            .post(requestBody)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                return operation(response)
            }
        } catch (e:Exception){
            Log.e(TAG, "Error Occurred", e)
            return null
        }
    }
    private fun <R> post(url: String, json:String, operation:(Response) -> R): R?{
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                return operation(response)
            }
        } catch (e:Exception){
            Log.e(TAG, "Error Occurred", e)
            return null
        }
    }
    private fun post(url: String, json:String, operation:(Response)->Unit){
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                operation(response)
            }
        } catch (e:Exception){
            Log.e(TAG, "Error Occurred", e)
            return
        }
    }
    private fun post(url: String, json:String, successLogText:String){
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                Log.d(TAG, successLogText)
            }
        } catch (e:Exception){
            Log.e(TAG, "Error Occurred", e)
            return
        }
    }
    private fun <R> patch(url: String, json:String, operation:(Response) -> R): R?{
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .patch(requestBody)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                return operation(response)
            }
        } catch (e:Exception){
            Log.e(TAG, "Error Occurred", e)
            return null
        }
    }
    private fun <R> patch(url: String, operation:(Response) -> R): R?{
        val json = """ """
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("accept", "*/*")
            .patch(requestBody)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                return operation(response)
            }
        } catch (e:Exception){
            Log.e(TAG, "Error Occurred", e)
            return null
        }
    }


}