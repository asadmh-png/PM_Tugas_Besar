package com.example.elzatta // Sesuaikan dengan nama package kamu!

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

/**
 * Activity Dashboard: Pusat navigasi aplikasi Elzatta.
 * Menampilkan menu utama berdasarkan peran user (Cashier/Leader).
 */
class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Mengambil role user yang dikirim dari activity sebelumnya (Login/Buka Kasir)
        val role = intent.getStringExtra("USER_ROLE") ?: "Cashier"

        // Inisialisasi komponen UI menu
        val cardTransaksi = findViewById<MaterialCardView>(R.id.cardTransaksi)
        val cardRetur = findViewById<MaterialCardView>(R.id.cardRetur)
        val cardMutasi = findViewById<MaterialCardView>(R.id.cardMutasi)
        val cardTutupToko = findViewById<MaterialCardView>(R.id.cardTutupToko)
        val cardCekStok = findViewById<MaterialCardView>(R.id.cardCekStok)
        val cardUpdatePromo = findViewById<MaterialCardView>(R.id.cardUpdatePromo)
        val btnLihatLaporan = findViewById<MaterialButton>(R.id.btnLihatLaporan)

        // Logika Role-Based Access: Tombol laporan hanya terlihat oleh Leader
        if (role == "Leader") {
            btnLihatLaporan.visibility = View.VISIBLE
        }

        // Setup Event Listener untuk masing-masing menu
        
        // Navigasi ke menu Penjualan/Transaksi
        cardTransaksi.setOnClickListener { startActivity(Intent(this, TransaksiActivity::class.java)) }
        
        // Navigasi ke menu Pengembalian Barang (Retur)
        cardRetur.setOnClickListener { startActivity(Intent(this, ReturActivity::class.java)) }
        
        // Navigasi ke menu Mutasi Stok (Antar Cabang)
        cardMutasi.setOnClickListener { startActivity(Intent(this, MutasiActivity::class.java)) }
        
        // Navigasi ke menu Penutupan Shift/Toko
        cardTutupToko.setOnClickListener { startActivity(Intent(this, TutupTokoActivity::class.java)) }
        
        // Navigasi ke menu Pencarian Stok Produk
        cardCekStok.setOnClickListener { startActivity(Intent(this, CekStokActivity::class.java)) }
        
        // Navigasi ke menu Update Promo (Sinkronisasi Data)
        cardUpdatePromo.setOnClickListener { startActivity(Intent(this, UpdatePromoActivity::class.java)) }
        
        // Navigasi ke menu Laporan (Khusus Leader)
        btnLihatLaporan.setOnClickListener {
            startActivity(Intent(this, LaporanActivity::class.java))
        }
    }
}
