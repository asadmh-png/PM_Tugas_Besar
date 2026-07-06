package com.example.elzatta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PembayaranDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_pembayaran, container, false)

        // Menu Utama
        val layoutUtama = view.findViewById<LinearLayout>(R.id.layoutPilihanUtama)
        val layoutNonCash = view.findViewById<LinearLayout>(R.id.layoutMetodeNonCash)
        val btnCash = view.findViewById<Button>(R.id.btnCash)
        val btnNonCash = view.findViewById<Button>(R.id.btnNonCash)
        val btnBack = view.findViewById<Button>(R.id.btnBackToMetode)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)

        // Sections
        val sectionCash = view.findViewById<LinearLayout>(R.id.sectionCash)
        val sectionQRIS = view.findViewById<LinearLayout>(R.id.sectionQRIS)
        val sectionEDC = view.findViewById<LinearLayout>(R.id.sectionEDC)
        val sectionEWallet = view.findViewById<LinearLayout>(R.id.sectionEWallet)

        // Cash UI
        val tvTotalTagihanCash = view.findViewById<TextView>(R.id.tvTotalTagihanCash)
        val etUangCash = view.findViewById<EditText>(R.id.etUangCash)
        val tvKembalian = view.findViewById<TextView>(R.id.tvKembalian)
        val btnProsesCash = view.findViewById<Button>(R.id.btnProsesCash)

        // Action Buttons & Inputs
        val btnPilihQRIS = view.findViewById<Button>(R.id.btnPilihQRIS)
        val btnPilihEDC = view.findViewById<Button>(R.id.btnPilihEDC)
        val btnPilihEWallet = view.findViewById<Button>(R.id.btnPilihEWallet)

        val btnCekQRIS = view.findViewById<Button>(R.id.btnCekStatusQRIS)
        val btnProsesEDC = view.findViewById<Button>(R.id.btnProsesEDC)
        val btnKirimEWallet = view.findViewById<Button>(R.id.btnKirimTagihan)
        val etNoReferensi = view.findViewById<EditText>(R.id.etNoReferensi)
        val etNoHPEwallet = view.findViewById<EditText>(R.id.etNoHPEwallet)
        val pbEWallet = view.findViewById<ProgressBar>(R.id.pbEWallet)

        val totalBelanja = (activity as? TransaksiActivity)?.getTotalBelanja() ?: 0
        tvTotalTagihanCash.text = "Total: Rp $totalBelanja"

        // Logic Navigasi Internal Dialog
        fun showSection(section: View?, title: String) {
            layoutUtama.visibility = View.GONE
            layoutNonCash.visibility = View.GONE
            sectionCash.visibility = View.GONE
            sectionQRIS.visibility = View.GONE
            sectionEDC.visibility = View.GONE
            sectionEWallet.visibility = View.GONE
            
            section?.visibility = View.VISIBLE
            btnBack.visibility = View.VISIBLE
            tvTitle.text = title
        }

        btnCash.setOnClickListener {
            showSection(sectionCash, "Pembayaran Tunai")
        }

        btnProsesCash.setOnClickListener {
            val input = etUangCash.text.toString()
            if (input.isNotEmpty()) {
                val uangInput = input.toInt()
                if (uangInput >= totalBelanja) {
                    val kembalian = uangInput - totalBelanja
                    Toast.makeText(context, "Tunai Berhasil. Kembalian: Rp $kembalian", Toast.LENGTH_SHORT).show()
                    navigateToSuccess("CASH", uangInput, kembalian)
                } else {
                    etUangCash.error = "Uang kurang!"
                }
            } else {
                etUangCash.error = "Masukkan nominal"
            }
        }

        btnNonCash.setOnClickListener {
            showSection(layoutNonCash, "Pilih Metode Non-Tunai")
        }

        btnPilihQRIS.setOnClickListener { showSection(sectionQRIS, "Pembayaran QRIS") }
        btnPilihEDC.setOnClickListener { showSection(sectionEDC, "Pembayaran Mesin EDC") }
        btnPilihEWallet.setOnClickListener { showSection(sectionEWallet, "Pembayaran E-Wallet") }

        btnBack.setOnClickListener {
            showSection(layoutUtama, "Pilih Metode Pembayaran")
            btnBack.visibility = View.GONE
        }

        // Logic Pembayaran QRIS
        btnCekQRIS.setOnClickListener {
            Toast.makeText(context, "Pembayaran QRIS Berhasil", Toast.LENGTH_SHORT).show()
            navigateToSuccess("QRIS", totalBelanja, 0)
        }

        // Logic Pembayaran EDC
        btnProsesEDC.setOnClickListener {
            val ref = etNoReferensi.text.toString()
            if (ref.isNotEmpty()) {
                Toast.makeText(context, "EDC Berhasil (Ref: $ref)", Toast.LENGTH_SHORT).show()
                navigateToSuccess("EDC", totalBelanja, 0)
            } else {
                etNoReferensi.error = "Masukkan nomor referensi"
            }
        }

        // Logic Pembayaran E-Wallet
        btnKirimEWallet.setOnClickListener {
            val phone = etNoHPEwallet.text.toString()
            if (phone.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    btnKirimEWallet.isEnabled = false
                    pbEWallet.visibility = View.VISIBLE
                    
                    delay(3000) // Simulasi 3 detik sesuai request
                    
                    Toast.makeText(context, "Pembayaran E-Wallet Lunas", Toast.LENGTH_SHORT).show()
                    navigateToSuccess("E-WALLET", totalBelanja, 0)
                }
            } else {
                etNoHPEwallet.error = "Masukkan nomor HP"
            }
        }

        return view
    }

    private fun navigateToSuccess(metode: String, bayar: Int, kembali: Int) {
        val activity = activity as? TransaksiActivity
        val totalBelanja = activity?.getTotalBelanja() ?: 0
        val listBarangString = activity?.getDaftarBarangString() ?: ""
        
        val nomorNota = activity?.selesaikanTransaksi(metode) ?: ""

        val intent = Intent(requireContext(), TransaksiBerhasilActivity::class.java).apply {
            putExtra("METODE_BAYAR", metode)
            putExtra("TOTAL_BELANJA", totalBelanja)
            putExtra("UANG_BAYAR", bayar)
            putExtra("UANG_KEMBALI", kembali)
            putExtra("DAFTAR_BARANG", listBarangString)
            putExtra("NOMOR_NOTA", nomorNota)
        }
        startActivity(intent)
        dismiss()
    }
}
