package com.example.uphill.ui.record

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.httptest2.HttpClient
import com.example.uphill.data.AppStatus
import com.example.uphill.data.UserInfo
import com.example.uphill.databinding.FragmentRecordBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)

    private lateinit var qrScannerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // ActivityResultLauncher 초기화
        qrScannerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val intentResult: IntentResult? =
                    IntentIntegrator.parseActivityResult(
                        result.resultCode,
                        result.data
                    )
                if (intentResult != null && intentResult.contents != null) {
                    // QR 코드의 JSON 데이터 파싱
                    val scannedContent = intentResult.contents
                    try {
                        val jsonObject = JSONObject(scannedContent)
                        if (jsonObject.has("routeId")) {
                            val routeId = jsonObject.getInt("routeId")
                            UserInfo.capturedRouteId = routeId


                             // queueActivity로 이동
                             val intent = Intent(requireContext(), QueueActivity::class.java)
                             startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "routeId가 포함되지 않은 QR 코드입니다.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("QR_SCAN_ERROR", "JSON 파싱 에러: $e")
                        Toast.makeText(requireContext(), "잘못된 QR 코드 형식입니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "QR 코드 인식 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val RecordViewModel =
            ViewModelProvider(this).get(RecordViewModel::class.java)

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.d("RecordFragment", "onCreateView called")

        // QR 코드 스캔 시작 버튼
        if (QueueStatus.isRegistered) {
            if(QueueStatus.routeImage != null){
                binding.imageView7.setImageBitmap(QueueStatus.routeImage!!.toBitmap())
                binding.imageView7.visibility = View.VISIBLE
            } else{
                updateImage()
            }

            updateNowPosition()
            binding.countText.visibility = View.VISIBLE
            binding.countNoText.visibility = View.VISIBLE
            binding.countNoText.text = QueueStatus.nowPosition.toString()
            binding.countNoText.setOnClickListener {
                updateNowPosition()
            }
            binding.routeNumText.visibility = View.VISIBLE
            binding.routeNumText.text = QueueStatus.routeId.toString()

            binding.button8.text = "취소"
            binding.button8.setOnClickListener {
                scope.launch {
                    val httpClient = HttpClient()
                    httpClient.deleteEntry()
                    val handler = Handler(Looper.getMainLooper())
                    handler.post{
                        Toast.makeText(requireContext(), "경로 등록이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                init()
            }
        } else{
            Log.d("RecordFragment", "not registered")
            init()
        }


        return root
    }
    private fun init(){
        QueueStatus.isRegistered = false
        QueueStatus.routeId = null
        QueueStatus.nowPosition = null
        QueueStatus.routeImage = null
        binding.imageView7.visibility = View.GONE
        binding.countNoText.visibility = View.GONE
        binding.countText.visibility = View.GONE
        binding.routeNumText.visibility = View.GONE
        binding.button8.text = "경로 등록"
        binding.button8.setOnClickListener {


            val integrator = IntentIntegrator(requireActivity())
            integrator.setBeepEnabled(false) // 비프음 비활성화
            integrator.setOrientationLocked(true) // 방향 고정 활성화
            integrator.captureActivity = CustomCaptureActivity::class.java // 세로 고정된 CustomCaptureActivity 사용
            qrScannerLauncher.launch(integrator.createScanIntent())
        }
    }
    private fun updateNowPosition(){
        scope.launch {
            val httpClient = HttpClient()
            QueueStatus.nowPosition = httpClient.getEntryPosition()?.count
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                binding.countNoText.text = QueueStatus.nowPosition.toString()
            }
        }
    }
    private fun updateImage(){
        scope.launch {
            val httpClient = HttpClient()
            QueueStatus.routeImage = httpClient.getRouteImageData(QueueStatus.routeId!!)
            val handler = Handler(Looper.getMainLooper())
            if (QueueStatus.routeImage!=null){
                handler.post {
                    binding.imageView7.setImageBitmap(QueueStatus.routeImage!!.toBitmap())
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
