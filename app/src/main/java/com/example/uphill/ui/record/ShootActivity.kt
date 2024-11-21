package com.example.uphill.ui.record

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.httptest2.HttpClient
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.data.AppStatus
import com.example.uphill.http.SocketClient
import com.example.uphill.objdetection.ActivityDetector
import com.example.uphill.objdetection.targetFPS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class ShootActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var btnRecord: Button
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var cameraExcutor = Executors.newSingleThreadExecutor()
    private var bitmapArray = arrayListOf<Bitmap>()
    private var lastCaptureTime: Long = 0

    private lateinit var handler: Handler
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoot)

        viewFinder = findViewById(R.id.viewFinder)
        btnRecord = findViewById(R.id.btnRecord)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        handler = Handler(mainLooper)

        btnRecord.setOnClickListener {
            Log.d(TAG, "captureVideo button clicked")
            captureVideo()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = viewFinder.surfaceProvider
            }

            videoCapture = VideoCapture.withOutput(
                Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                    .build()
            )
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(cameraExcutor) { image ->

                val currentTime = System.currentTimeMillis()
                if (recording != null && currentTime - lastCaptureTime >= 1000 / targetFPS) {
                    Log.d(TAG, "Video is being recorded")
                    addBitmapToList(image.toBitmap())
                    lastCaptureTime = currentTime
                }
                image.close()
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, videoCapture, imageAnalysis)
            } catch (exc: Exception) {
                Toast.makeText(this, "카메라 시작에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }
    private fun imageProxyToBitmap(image: ImageProxy):Bitmap?{
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        if(bytes.isNotEmpty()){
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        return null
    }
    private fun addBitmapToList(bitmap:Bitmap){
            bitmapArray.add(bitmap)
            Log.d(TAG, "bitmap size: ${bitmapArray.size}")
    }

    private fun captureVideo() {
        val videoCapture = videoCapture ?: return

        btnRecord.isEnabled = false

        val contentResolver = applicationContext.contentResolver
        val videoUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, ContentValues())


        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            btnRecord.text = "Record"

            detectObject()
            AppStatus.initClimbingStatus()

            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
            finish()


        } else {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".mp4")
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Movies/Uphill")
            }

            val mediaStoreOutputOptions = MediaStoreOutputOptions
                .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues)
                .build()
            recording = videoCapture.output
                .prepareRecording(this, mediaStoreOutputOptions)
                .apply {
                    withAudioEnabled()
                }
                .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            btnRecord.text = "Recording"
                            btnRecord.isEnabled = true
                            SocketClient.setEndEventHandleFunction{ onEndSignalReceived() }
                        }

                        is VideoRecordEvent.Finalize -> {
                            if (!recordEvent.hasError()) {

                                recordEvent.outputResults.outputUri.path?.let { detectObject() }?:run{
                                    Toast.makeText(this, "Cannot find the path", Toast.LENGTH_SHORT).show()
                                    Log.e(TAG, "Cannot find the path")
                                }

                                recording = null
                                btnRecord.text = "Record"
                                btnRecord.isEnabled = true

                                AppStatus.initClimbingStatus()

                                val intent = Intent(this, ResultActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                recording?.close()
                                recording = null
                                Toast.makeText(
                                    this,
                                    "Video capture failed: ${recordEvent.error}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnRecord.text = "Record"
                                btnRecord.isEnabled = true
                            }
                            btnRecord.apply {
                                text = "Record"
                                isEnabled = true
                            }
                        }
                    }
                }
        }
    }



    private fun stopRecording(){
        Log.d(TAG, "stopRecording")
        if (recording != null) {
            recording?.stop()
        }
    }
    private fun onEndSignalReceived(){
        scope.launch {
            delay(5000)
            stopRecording()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketClient.setEndEventHandleFunction {}
    }

    private fun detectObject(){
        Log.d(TAG, "bitmap size: ${bitmapArray.size}")
        ActivityDetector.detectImages(bitmapArray){success ->
            runOnUiThread{
                if(success){
                    Toast.makeText(this, "object detection success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "object detection fail", Toast.LENGTH_SHORT).show()
                }
                bitmapArray.clear()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        private const val TAG = "ShootActivity"
    }
}