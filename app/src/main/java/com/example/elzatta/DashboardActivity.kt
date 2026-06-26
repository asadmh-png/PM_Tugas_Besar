package com.example.elzatta // Sesuaikan dengan nama package kamu!

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Kenalkan tombol-tombol
        val btnMenuTransaksi = findViewById<MaterialButton>(R.id.btnMenuTransaksi)
        val btnMenuRetur = findViewById<MaterialButton>(R.id.btnMenuRetur)
        val btnMenuMutasi = findViewById<MaterialButton>(R.id.btnMenuMutasi)
        val btnMenuTutupToko = findViewById<MaterialButton>(R.id.btnMenuTutupToko)
        val btnMenuCekStok = findViewById<MaterialButton>(R.id.btnMenuCekStok)
        val btnMenuUpdatePromo = findViewById<MaterialButton>(R.id.btnMenuUpdatePromo)

        // Pasang Intent (Navigasi) ke masing-masing halaman
        btnMenuTransaksi.setOnClickListener {
            val intent = Intent(this, TransaksiActivity::class.java)
            startActivity(intent)
        }

        btnMenuRetur.setOnClickListener {
            val intent = Intent(this, ReturActivity::class.java)
            startActivity(intent)
        }

        btnMenuMutasi.setOnClickListener {
            val intent = Intent(this, MutasiActivity::class.java)
            startActivity(intent)
        }

        btnMenuTutupToko.setOnClickListener {
            val intent = Intent(this, TutupTokoActivity::class.java)
            startActivity(intent)
        }

        // Fungsi kliknya (taruh di bawah Intent yang sudah ada)
        btnMenuCekStok.setOnClickListener {
            val intent = Intent(this, CekStokActivity::class.java)
            startActivity(intent)
        }

        btnMenuUpdatePromo.setOnClickListener {
            val intent = Intent(this, UpdatePromoActivity::class.java)
            startActivity(intent)
        }
    }
}