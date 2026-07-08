package com.example.elzatta // Sesuaikan dengan nama package kamu!

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val role = intent.getStringExtra("USER_ROLE") ?: "Cashier"

        val cardTransaksi = findViewById<MaterialCardView>(R.id.cardTransaksi)
        val cardRetur = findViewById<MaterialCardView>(R.id.cardRetur)
        val cardMutasi = findViewById<MaterialCardView>(R.id.cardMutasi)
        val cardTutupToko = findViewById<MaterialCardView>(R.id.cardTutupToko)
        val cardCekStok = findViewById<MaterialCardView>(R.id.cardCekStok)
        val cardUpdatePromo = findViewById<MaterialCardView>(R.id.cardUpdatePromo)
        val btnLihatLaporan = findViewById<MaterialButton>(R.id.btnLihatLaporan)

        // Tampilkan tombol laporan hanya jika role adalah Leader
        if (role == "Leader") {
            btnLihatLaporan.visibility = View.VISIBLE
        }

        // Listener
        cardTransaksi.setOnClickListener { startActivity(Intent(this, TransaksiActivity::class.java)) }
        cardRetur.setOnClickListener { startActivity(Intent(this, ReturActivity::class.java)) }
        cardMutasi.setOnClickListener { startActivity(Intent(this, MutasiActivity::class.java)) }
        cardTutupToko.setOnClickListener { startActivity(Intent(this, TutupTokoActivity::class.java)) }
        cardCekStok.setOnClickListener { startActivity(Intent(this, CekStokActivity::class.java)) }
        cardUpdatePromo.setOnClickListener { startActivity(Intent(this, UpdatePromoActivity::class.java)) }
        
        btnLihatLaporan.setOnClickListener {
            startActivity(Intent(this, LaporanActivity::class.java))
        }
    }
}
