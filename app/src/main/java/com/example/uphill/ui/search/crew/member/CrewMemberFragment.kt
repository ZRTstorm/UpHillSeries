package com.example.uphill.ui.search.crew.member

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.httptest2.HttpClient
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.data.UserInfo
import com.example.uphill.databinding.FragmentCrewMemberBinding
import com.example.uphill.ui.search.CrewSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CrewMemberFragment : Fragment() {

    private var _binding: FragmentCrewMemberBinding? = null
    private val binding get() = _binding!!
    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrewMemberBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 전달받은 데이터 읽기
        val crew = CrewSingleton.selectedCrew

        if (crew != null) {
                binding.textView2.text = crew.crewName
                binding.textView3.text = crew.userName
                binding.textView8.text = crew.content
                refreshCrewMembers(crew.crewId) // 초기 데이터 로드
        } else {
            Toast.makeText(requireContext(), "Failed to load crew data.", Toast.LENGTH_SHORT).show()
        }
        binding.crewMember.layoutManager = LinearLayoutManager(requireContext())


        if(UserInfo.crewInfo != null){
            if(UserInfo.userId == UserInfo.crewInfo!!.pilotId){
                binding.button17.text = "삭제"
            }
            else{
                binding.button17.text = "탈퇴"
            }
        }
        // 버튼 클릭 리스너 설정
        binding.button17.setOnClickListener {
            if(UserInfo.crewInfo != null) {
                if(UserInfo.userId == UserInfo.crewInfo!!.pilotId) {
                    scope.launch {
                        HttpClient().deleteCrew(UserInfo.crewInfo!!.crewId)
                        Toast.makeText(
                            requireContext(),
                            "크루를 삭제했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        UserInfo.crewInfo = null
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                else {
                    scope.launch {
                        HttpClient().unsubscribeCrew()
                        UserInfo.crewInfo = null
                        Toast.makeText(
                            requireContext(),
                            "크루를 탈퇴했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            else{
                if (crew != null) {
                    showPasswordDialog(crew.crewId)
                }
            }
        }

        return root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshCrewMembers(crewId: Int) {
        scope.launch {
            val updatedCrewMembers = HttpClient().getCrewManList(crewId)
            updatedCrewMembers?.let {
                requireActivity().runOnUiThread {
                    val adapter = CrewMemberAdapter(it)
                    binding.crewMember.adapter = adapter
                    binding.crewMember.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun showPasswordDialog(crewId: Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_password_input, null)
        val passwordInput = dialogView.findViewById<EditText>(R.id.passwordInput)

        AlertDialog.Builder(requireContext())
            .setTitle("크루초대번호")
            .setView(dialogView)
            .setPositiveButton("제출") { _, _ ->
                val password = passwordInput.text.toString()
                if (password.isNotEmpty()) {
                    scope.launch {
                        try {
                            HttpClient().registerCrew(crewId, password)
                            UserInfo.crewInfo = HttpClient().getCrewInfo()
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "크루 등록 완료!", Toast.LENGTH_SHORT).show()
                                refreshCrewMembers(crewId) // 새로고침 호출
                            }
                        }
                        catch (_:Exception){
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "등록 실패. 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "비밀번호를 입력해주세요!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }


    fun newInstant() : CrewMemberFragment
    {
        val args = Bundle()
        val frag = CrewMemberFragment()
        frag.arguments = args
        return frag
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

