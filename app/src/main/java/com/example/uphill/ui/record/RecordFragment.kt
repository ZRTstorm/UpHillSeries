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
import com.example.uphill.objdetection.ActivityDetector
import com.example.uphill.objdetection.ClimbingRoute
import com.example.uphill.objdetection.UserAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private lateinit var imageView: ImageView

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

        binding.imageView2.setImageResource(R.drawable.green_circle)
        imageView = binding.imageView
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

    private suspend fun calcTestVideo():Bitmap?{
        val context = this.context ?: return null
        val movingView = binding.imageView2

        val sourceUriString =
            "android.resource://" + context.packageName + "/" + R.raw.climbing_demo_30s2
        val videoUri = Uri.parse(sourceUriString)


        val data = ActivityDetector.detect(context, videoUri) ?: return null
        var climbingRoute: ClimbingRoute? = null
        if(ActivityDetector.thumbnail != null){
            climbingRoute = ClimbingRoute(ActivityDetector.thumbnail, Point(23,192), Point(127,60))
        }
        val uaTest = UserAnimator(movingView, data, climbingRoute!!)
        uaTest.startTime=3.7
        uaTest.endTime=18.0
        uaTest.calc()
        Handler(Looper.getMainLooper()).post{
            Log.d(TAG,"animation start")
            uaTest.start()
        }
        return ActivityDetector.thumbnail
    }
    private fun updateView(bitmap: Bitmap){

        if (::imageView.isInitialized){
            imageView.setImageBitmap(bitmap)
        }else{
            Log.e(TAG, "imageView is not initialized")
        }
    }

    companion object{
        private const val TAG = "RECORD_FRAGMENT"
    }
}