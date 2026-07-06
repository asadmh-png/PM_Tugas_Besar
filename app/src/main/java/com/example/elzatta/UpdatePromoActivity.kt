package com.example.elzatta

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdatePromoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_promo)

        val btnSyncPromo = findViewById<MaterialButton>(R.id.btnSyncPromo)
        val pbLoadingPromo = findViewById<ProgressBar>(R.id.pbLoadingPromo)
        val tvLastSync = findViewById<TextView>(R.id.tvLastSync)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarUpdatePromo)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnSyncPromo.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                // 1. Persiapan UI
                btnSyncPromo.isEnabled = false
                pbLoadingPromo.visibility = View.VISIBLE
                Toast.makeText(this@UpdatePromoActivity, "Menghubungkan ke server pusat...", Toast.LENGTH_SHORT).show()

                // 2. Simulasi delay unduh data (3 detik)
                delay(3000)

                // 3. Eksekusi update ke database
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(this@UpdatePromoActivity)
                    db.productDao().updatePromoSimulation()
                }

                // 4. Update Waktu Sinkronisasi
                val currentTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                tvLastSync.text = "Terakhir diupdate: $currentTime"

                // 5. Selesai
                pbLoadingPromo.visibility = View.GONE
                btnSyncPromo.isEnabled = true
                Toast.makeText(this@UpdatePromoActivity, "Sinkronisasi Promo Berhasil!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
