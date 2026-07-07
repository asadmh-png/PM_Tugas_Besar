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
import java.text.SimpleDateFormat
import java.util.*

class TutupTokoActivity : AppCompatActivity() {

    private var totalSistem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutup_toko)

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

        // Ambil data pendapatan shift aktif
        CoroutineScope(Dispatchers.Main).launch {
            val db = AppDatabase.getDatabase(this@TutupTokoActivity)
            val total = withContext(Dispatchers.IO) {
                db.transactionDao().getTotalPendapatanShift() ?: 0
            }
            totalSistem = total
            tvTotalSistem.text = "Rp $totalSistem"
        }

        btnHitungSelisih.setOnClickListener {
            val inputFisikStr = etUangFisik.text.toString()
            if (inputFisikStr.isEmpty()) {
                etUangFisik.error = "Masukkan jumlah uang fisik!"
                return@setOnClickListener
            }

            val uangFisik = inputFisikStr.toInt()
            val selisih = uangFisik - totalSistem

            layoutHasilSelisih.visibility = View.VISIBLE
            tilCatatan.visibility = View.VISIBLE
            btnTutupToko.isEnabled = true

            if (selisih == 0) {
                tvStatusSelisih.text = "Sesuai (Balance)"
                tvStatusSelisih.setTextColor(Color.parseColor("#388E3C"))
            } else if (selisih < 0) {
                tvStatusSelisih.text = "Minus Rp ${selisih * -1}"
                tvStatusSelisih.setTextColor(Color.parseColor("#D32F2F"))
            } else {
                tvStatusSelisih.text = "Lebih Rp $selisih"
                tvStatusSelisih.setTextColor(Color.parseColor("#F57C00"))
            }
        }

        btnTutupToko.setOnClickListener {
            val catatan = etCatatan.text.toString()
            val inputFisik = etUangFisik.text.toString().toIntOrNull() ?: 0
            val selisih = inputFisik - totalSistem

            // EKSEKUSI TUTUP SHIFT
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@TutupTokoActivity)

                // 1. Simpan Record ke ShiftSession
                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val shift = ShiftSession(
                    waktuTutup = currentTime,
                    totalSistem = totalSistem,
                    totalFisik = inputFisik,
                    selisih = selisih,
                    catatan = if (catatan.isEmpty()) "-" else catatan
                )
                db.shiftSessionDao().insertShift(shift)

                // 2. Kunci transaksi agar saldo sistem kembali ke 0
                db.transactionDao().closeAllCurrentTransactions()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TutupTokoActivity, "Shift Berhasil Ditutup!", Toast.LENGTH_SHORT).show()

                    // 3. Logout dan Redirect ke BukaKasirActivity
                    val intent = Intent(this@TutupTokoActivity, BukaKasirActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
