package com.example.httptest2

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat.startForegroundService
import com.example.uphill.data.Convert
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.BattleRoomClimbingData
import com.example.uphill.data.model.BattleRoomData
import com.example.uphill.data.model.BattleRoomDataList
import com.example.uphill.data.model.BattleRoomDetailInfo
import com.example.uphill.data.model.BattleRoomRegistryReceivedData
import com.example.uphill.data.model.BattleRoomRegistrySendData
import com.example.uphill.data.model.ClimbingRoute
import com.example.uphill.data.model.CrewInfo
import com.example.uphill.data.model.CrewMan
import com.example.uphill.data.model.EntryPosition
import com.example.uphill.data.model.MovementData
import com.example.uphill.data.model.RouteImageData
import com.example.uphill.data.model.SimpleCrewInfo
import com.example.uphill.data.model.UserId
import com.example.uphill.http.WebSocketService
import com.example.uphill.ui.record.QueueStatus
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

                    UserInfo.crewInfo = getCrewInfo()
                } else{
                    Log.e(TAG, "Request failed. ${response.code}")
                }
            }
        })
    }
    fun getProfileImageUrl(userId: Int):String{
        val url = "$server_name/users/profile/$userId"
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "*/*")
            .get()
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                if(response.body == null) return ""

                return response.body!!.string()
            }
        } catch (e:Exception){
            Log.e(TAG, "Error Occurred", e)
            return ""
        }
    }

    // sensor-controller
    // route-Controller
    fun uploadRouteImage(image: Bitmap, routeId:Int){
        val url = "$server_name/routes/$routeId/upload"
        val json = Convert.bitmapToBase64(image)

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
    fun setRouteLocation(routeId: Int, startX:Int, startY:Int, endX:Int, endY:Int){
        val url = "$server_name/routes/$routeId/sets"
        val json = """
            "startX": $startX,
            "startY": $startY,
            "endX": $endX,
            "endY": $endY
        """.trimIndent()
        fun op(response:Response){
            Log.d(TAG, "Set route location success")
        }
        patch(url, json, ::op)
    }
    fun getRouteImageData(routeId:Int):RouteImageData?{
        val url = "$server_name/routes/$routeId/routeImage"
        fun op(response:Response):RouteImageData? {
            Log.d(TAG, "Get route image success")
            if (response.body == null) return null
            val routeImageData = Gson().fromJson(response.body!!.string(), RouteImageData::class.java)
            return routeImageData
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
    fun getEntryPosition():EntryPosition?{
        val url = "$server_name/entryQueue/${UserInfo.userId}/position"
        fun op(response:Response):EntryPosition?{
            Log.d(TAG, "Get entry position success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, EntryPosition::class.java)

        }
        return get(url, ::op)
    }
    fun deleteEntry(){
        val url = "$server_name/entryQueue/${UserInfo.userId}"
        delete(url, "Delete entry success")
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
        if(UserInfo.battleRoomId != null){
            postBattleRoomClimbingData(UserInfo.battleRoomId!!, UserInfo.lastClimbingId!!)
            QueueStatus.reset()
        }
        UserInfo.lastClimbingId = null
    }
    fun getMovementData(climbingDataId: Int):MovementData?{
        val url = server_name+"/bodyMovement/$climbingDataId"
        Log.d(TAG, "Get movement data. dataId: $climbingDataId")
        fun op(response:Response):MovementData?{
            Log.d(TAG, "Get movement data success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, MovementData::class.java)
        }
        return get(url, ::op)
    }
    // crew-controller
    fun createCrew(crewName: String, content: String, password: String){
        val url = "$server_name/crew/${UserInfo.userId}"
        val json = """
            {
                "crewName": "$crewName",
                "content": "$content",
                "password": "$password"
            }
        """
        post(url, json, "Create crew success")
    }
    fun registerCrew(crewId: Int, password: String){
        val url = "$server_name/crew/crewMan/${UserInfo.userId}/register"
        val json = """
            {
                "crewId": $crewId,
                "password": "$password"
            }
        """
        Log.d(TAG, json)
        post(url, json, "Register crew success")
    }
    fun getCrewImage(crewId: Int):Bitmap?{
        val url="$server_name/crew/$crewId/image"
        fun op(response:Response):Bitmap?{
            Log.d(TAG, "Get crew image success")
            if (response.body == null) return null
            return Convert.base64ToBitmap(response.body!!.string())
        }
        return get(url, ::op)
    }
    fun putCrewImage(crewId: Int, image: Bitmap){
        val url = "$server_name/crew/$crewId/image"
        val json = Convert.bitmapToBase64(image)
        fun op(response:Response){
            Log.d(TAG, "Put crew image success")
        }
        patch(url, json, ::op)
    }
    fun getCrewInfo(): CrewInfo?{
        val url = "$server_name/crew/${UserInfo.userId}/crewInfo"
        fun op(response:Response):CrewInfo?{
            Log.d(TAG, "Get crew info success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, CrewInfo::class.java)
        }
        return get(url, ::op)
    }
    fun searchCrews(crewName: String):SimpleCrewInfo?{
        val url = "$server_name/crew/$crewName"
        fun op(response:Response):SimpleCrewInfo? {
            Log.d(TAG, "Search crews success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, SimpleCrewInfo::class.java)
        }
        return get(url, ::op)
    }
    fun getCrewManList(crewId: Int):List<CrewMan>?{
        val url = "$server_name/crew/$crewId/crewMan"
        fun op(response:Response):List<CrewMan>? {
            Log.d(TAG, "Get crew man list success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            val listType = object:TypeToken<List<CrewMan>>() {}.type
            return Gson().fromJson(jsonResponse, listType)
        }
        return get(url, ::op)
    }
    fun getAllCrew(): SimpleCrewInfo? {
        val url = "$server_name/crew/all"
        fun op(response: Response): SimpleCrewInfo? {
            Log.d(TAG, "Get all crew success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, object : TypeToken<SimpleCrewInfo>() {}.type)
        }
        return get(url, ::op)
    }
    fun deleteCrew(crewId: Int){
        val url = "$server_name/crew/$crewId/${UserInfo.userId}"
        delete(url, "Delete crew success")
    }
    fun unsubscribeCrew(){
        val url = "$server_name/crew/crewMan/${UserInfo.userId}"
        delete(url, "Unsubscribe crew success")
    }

    // battle-room-controller
    fun postBattleRoomClimbingData(battleRoomId: Int, climbingDataId: Int){
        val url = "$server_name/battleRoom/${UserInfo.userId}/$battleRoomId/$climbingDataId"
        post(url, "","Post battle room success")
    }
    fun participantBattleRoom(participantCode: String){
        val room = getBattleRoomFromCode(participantCode)
        if(room==null){
            Log.d(TAG, "room is null")
            return
        }
        participantBattleRoom(room.battleRoomId)
    }
    fun participantBattleRoom(battleRoomId: Int){
        val url = "$server_name/battleRoom/${UserInfo.userId}/$battleRoomId/participant"
        fun op(response: Response){
            Log.d(TAG, "participant success")
        }
        post(url, ::op)
    }
    fun registryBattleRoom(
        battleRoomRegistrySendData: BattleRoomRegistrySendData,
        callback: (BattleRoomRegistryReceivedData?) -> Unit
    ) {
        val url = "$server_name/battleRoom/${UserInfo.userId}/registry"
        val json = Gson().toJson(battleRoomRegistrySendData)

        post(url, json) { response ->
            if (response.body == null) {
                Log.e(TAG, "No response body")
                callback(null)
            } else {
                val jsonResponse = response.body?.string()
                val data = Gson().fromJson(jsonResponse, BattleRoomRegistryReceivedData::class.java)
                callback(data)
            }
        }
    }
    fun finishBattle(battleRoomId: Int){
        val url = "$server_name/battleRoom/${UserInfo.userId}/$battleRoomId/end"
        fun op(response: Response){
            Log.d(TAG, "finish success")
        }
        patch(url, ::op)
    }
    fun getBattleRoomDetailInfo(battleRoomId: Int):BattleRoomDetailInfo?{
        val url = "$server_name/battleRoom/$battleRoomId/info"
        fun op(response: Response):BattleRoomDetailInfo?{
            Log.d(TAG, "get battle room detail info success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, BattleRoomDetailInfo::class.java)
        }
        return get(url, ::op)
    }
    fun getBattleRoomClimbingData(battleRoomId: Int): BattleRoomClimbingData?{
        val url = "$server_name/battleRoom/$battleRoomId/climbingData"
        fun op(response: Response):BattleRoomClimbingData?{
            Log.d(TAG, "get battle room climbing data success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, BattleRoomClimbingData::class.java)
        }
        return get(url, ::op)
    }
    fun getUserBattleRoom():BattleRoomDataList?{
        val url = "$server_name/battleRoom/users/${UserInfo.userId}/all"
        fun op(response: Response):BattleRoomDataList?{
            Log.d(TAG, "get all success")
            if (response.body == null) return BattleRoomDataList()
            val jsonResponse = response.body?.string()
            val listType = object:TypeToken<BattleRoomDataList>() {}.type
            return Gson().fromJson(jsonResponse, listType)
        }
        return get(url, ::op)
    }
    fun getBattleRoomFromCode(participantCode: String): BattleRoomData?{
        val url = "$server_name/battleRoom/participant/$participantCode"
        fun op(response: Response):BattleRoomData?{
            Log.d(TAG, "get room from code success")
            if (response.body == null) return null
            val jsonResponse = response.body?.string()
            return Gson().fromJson(jsonResponse, BattleRoomData::class.java)
        }
        return get(url, ::op)
    }
    fun getBattleRoomFromCrewId(crewId: Int):BattleRoomDataList?{
        val url = "$server_name/battleRoom/crews/$crewId/all"
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
        delete(url, "Delete battle room success")
    }
    fun quitBattleRoom(battleRoomId: Int){
        val url = "$server_name/battleRoom/$battleRoomId/${UserInfo.userId}/quit"
        fun op(response: Response){
            Log.d(TAG, "quit room from crewId success")
        }
        post(url, ::op)
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
    private fun <R> delete(url: String, operation:(Response) -> R): R?{
        val json = """ """
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader("accept", "*/*")
            .delete(requestBody)
            .build()

        try {

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                return operation(response)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error Occurred", e)
            return null
        }
    }
    private fun delete(url: String, successLogText: String){
        fun op(response:Response){
            Log.d(TAG, successLogText)
        }
        delete(url, ::op)
    }


}