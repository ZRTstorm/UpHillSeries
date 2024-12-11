package com.example.uphill.ui.dashboard.competition

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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

        battleRoomIdTextView.text = "방 아이디: ${data.battleRoomId}"
        participantCodeTextView.text = "초대코드: ${data.participantCode}"

        participantCodeTextView.setOnClickListener{
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = android.content.ClipData.newPlainText("Label", data.participantCode)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "초대코드가 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.closeButton).setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        // Handle dismiss event if needed
        activity?.finish()
    }
}
