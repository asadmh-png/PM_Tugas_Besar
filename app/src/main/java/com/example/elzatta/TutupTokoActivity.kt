package com.example.elzatta

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class TutupTokoActivity : AppCompatActivity() {

    // Dummy data: Ceritanya sistem mencatat hari ini ada uang Rp 5.000.000
    private val totalSistem = 5000000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutup_toko)

        // Kenalkan UI
        val tvTotalSistem = findViewById<TextView>(R.id.tvTotalSistem)
        val etUangFisik = findViewById<TextInputEditText>(R.id.etUangFisik)
        val btnHitungSelisih = findViewById<MaterialButton>(R.id.btnHitungSelisih)
        val layoutHasilSelisih = findViewById<LinearLayout>(R.id.layoutHasilSelisih)
        val tvStatusSelisih = findViewById<TextView>(R.id.tvStatusSelisih)
        val btnTutupToko = findViewById<MaterialButton>(R.id.btnTutupToko)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarTutupToko)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Tampilkan total sistem ke layar
        tvTotalSistem.text = "Rp $totalSistem"

        // Logika saat tombol Hitung ditekan
        btnHitungSelisih.setOnClickListener {
            val inputFisikStr = etUangFisik.text.toString()

            if (inputFisikStr.isEmpty()) {
                etUangFisik.error = "Masukkan jumlah uang fisik di laci!"
                return@setOnClickListener
            }

            val uangFisik = inputFisikStr.toInt()
            val selisih = uangFisik - totalSistem

            // Menampilkan layout hasil
            layoutHasilSelisih.visibility = View.VISIBLE
            btnTutupToko.isEnabled = true // Hidupkan tombol Tutup Toko

            // Menentukan status balance / minus / lebih
            if (selisih == 0) {
                tvStatusSelisih.text = "Sesuai (Balance)"
                tvStatusSelisih.setTextColor(Color.parseColor("#388E3C")) // Hijau
            } else if (selisih < 0) {
                tvStatusSelisih.text = "Minus Rp ${selisih * -1}"
                tvStatusSelisih.setTextColor(Color.parseColor("#D32F2F")) // Merah
            } else {
                tvStatusSelisih.text = "Lebih Rp $selisih"
                tvStatusSelisih.setTextColor(Color.parseColor("#F57C00")) // Orange
            }
        }

        // Logika saat tombol Tutup Toko ditekan
        btnTutupToko.setOnClickListener {
            Toast.makeText(this, "Mencetak Z-Report & Menutup Shift...", Toast.LENGTH_LONG).show()
            // Di sini nanti data selisih akan dikirim ke Backend
        }
    }
}