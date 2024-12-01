package com.example.uphill.ui.search.crew.member

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import com.example.uphill.R
import com.example.uphill.databinding.FragmentCrewMemberBinding
import com.example.httptest2.HttpClient
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.SearchedCrewInfoItem
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
            // UI에 데이터 반영
            binding.textView2.text = crew.crewName
            binding.textView3.text = crew.userName
            binding.textView7.text = crew.content
        } else {
            Toast.makeText(requireContext(), "Failed to load crew data.", Toast.LENGTH_SHORT).show()
        }

        if(UserInfo.crewInfo != null){
            binding.button17.visibility = INVISIBLE
        }
        // 버튼 클릭 리스너 설정
        binding.button17.setOnClickListener {
            if (crew != null) {
                showPasswordDialog(crew.crewId)
            }
        }

        return root
    }

    private fun showPasswordDialog(crewId: Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_password_input, null)
        val passwordInput = dialogView.findViewById<EditText>(R.id.passwordInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Enter Password")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val password = passwordInput.text.toString()
                if (password.isNotEmpty()) {
                    scope.launch {
                        HttpClient().registerCrew(crewId, password)
                    }
                } else {
                    Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

