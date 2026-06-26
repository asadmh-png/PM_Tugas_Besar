package com.example.elzatta // Sesuaikan package!

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class UpdatePromoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_promo)

        val btnSyncPromo = findViewById<MaterialButton>(R.id.btnSyncPromo)

        btnSyncPromo.setOnClickListener {
            // Simulasi proses sinkronisasi
            Toast.makeText(this, "Mengunduh data promo dari server pusat...", Toast.LENGTH_LONG).show()

            // Nanti di sini ditaruh fungsi Retrofit/API untuk narik data master
        }
    }
}