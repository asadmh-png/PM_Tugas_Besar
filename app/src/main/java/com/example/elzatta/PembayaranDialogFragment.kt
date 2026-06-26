package com.example.elzatta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PembayaranDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_pembayaran, container, false)

        val btnCash = view.findViewById<Button>(R.id.btnCash)
        val btnNonCash = view.findViewById<Button>(R.id.btnNonCash)

        btnCash.setOnClickListener {
            Toast.makeText(context, "Mode Cash: Silakan input uang pelanggan", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btnNonCash.setOnClickListener {
            Toast.makeText(context, "Mode QRIS/EDC: Menunggu scan...", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return view
    }
}