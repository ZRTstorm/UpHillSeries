package com.example.uphill.ui.dashboard.competition

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.uphill.R
import com.example.uphill.data.model.BattleRoomRegistryReceivedData

class BattleRoomResultDialog(private val data: BattleRoomRegistryReceivedData) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_dialog, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val battleRoomIdTextView = view.findViewById<TextView>(R.id.battleRoomIdTextView)
        val participantCodeTextView = view.findViewById<TextView>(R.id.participantCodeTextView)

        battleRoomIdTextView.text = "Battle Room ID: ${data.battleRoomId}"
        participantCodeTextView.text = "Participant Code: ${data.participantCode}"

        view.findViewById<View>(R.id.closeButton).setOnClickListener {
            parentFragmentManager.setFragmentResult("inviteCodeDialog", Bundle())
            dismiss()
        }
    }
}
