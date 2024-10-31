package com.example.uphill.ui.record

import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.uphill.R
import com.example.uphill.databinding.FragmentRecordBinding
import com.example.uphill.objdetection.ActivityDetection
import com.example.uphill.objdetection.ClimbingRoute
import com.example.uphill.objdetection.UserAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


const val TAG = "RECORD_FRAGMENT"
class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val informationViewModel =
            ViewModelProvider(this).get(RecordViewModel::class.java)

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRecord
        informationViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = calcTestVideo()
            if (bitmap!=null){
                withContext(Dispatchers.Main){
                    updateView(bitmap)
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    suspend fun calcTestVideo():Bitmap?{
        val context = this.context ?: return null
        val movingView = binding.imageView2



        val sourceUriString =
            "android.resource://" + context.packageName + "/" + R.raw.climbing_demo_30s2
        val videoUri = Uri.parse(sourceUriString)

        val activityDetection = ActivityDetection(context, videoUri)

        activityDetection.detect()
        var climbingRoute: ClimbingRoute? = null
        if(activityDetection.bitmap!=null){
            climbingRoute = ClimbingRoute(activityDetection.bitmap, Point(23,192), Point(127,60))
        }
        if(activityDetection.locationList!=null) {
            Log.d(TAG,"animation start")
            val uaTest = UserAnimator(movingView, activityDetection.locationList!!, climbingRoute!!)
            uaTest.startTime=3.7
            uaTest.endTime=18.0
            uaTest.calc()
            Handler(Looper.getMainLooper()).post{
                uaTest.start()
            }
        }
        return activityDetection.bitmap
    }
    fun calcVideo(videoUri: Uri){
        val activityDetection = this.context?.let { ActivityDetection(it, videoUri) }
        if (activityDetection==null){
            return
        }

    }
    fun updateView(bitmap: Bitmap){
        binding.imageView.setImageBitmap(bitmap)
    }
}