package com.example.elzatta

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TutupTokoActivity : AppCompatActivity() {

    private var totalSistem = 0

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
        val tilCatatan = findViewById<TextInputLayout>(R.id.tilCatatan)
        val etCatatan = findViewById<TextInputEditText>(R.id.etCatatan)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarTutupToko)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Ambil data riil dari database
        CoroutineScope(Dispatchers.Main).launch {
            val db = AppDatabase.getDatabase(this@TutupTokoActivity)
            val total = withContext(Dispatchers.IO) {
                db.transactionDao().getTotalPendapatanShift() ?: 0
            }
            totalSistem = total
            tvTotalSistem.text = "Rp $totalSistem"
        }

        // Logika saat tombol Hitung ditekan
        btnHitungSelisih.setOnClickListener {
            val inputFisikStr = etUangFisik.text.toString()

            if (inputFisikStr.isEmpty()) {
                etUangFisik.error = "Masukkan jumlah uang fisik di laci!"
                return@setOnClickListener
            }

            val uangFisik = inputFisikStr.toInt()
            val selisih = uangFisik - totalSistem

            // Menampilkan layout hasil dan field catatan
            layoutHasilSelisih.visibility = View.VISIBLE
            tilCatatan.visibility = View.VISIBLE
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
            val catatan = etCatatan.text.toString()
            val inputFisik = etUangFisik.text.toString().toIntOrNull() ?: 0
            val selisih = inputFisik - totalSistem
            val statusSelisih = if (selisih == 0) "Sesuai" else if (selisih < 0) "Minus Rp ${selisih * -1}" else "Lebih Rp $selisih"

            val pesan = "Shift Ditutup. Selisih: $statusSelisih. Catatan: ${if (catatan.isEmpty()) "-" else catatan}"
            Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()

            // Kembali ke Dashboard dan bersihkan activity stack
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
