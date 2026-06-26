package com.example.elzatta

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ReturActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retur)

        // Kenalkan UI
        val etNomorNota = findViewById<TextInputEditText>(R.id.etNomorNota)
        val btnCariNota = findViewById<MaterialButton>(R.id.btnCariNota)
        val layoutHasilRetur = findViewById<LinearLayout>(R.id.layoutHasilRetur)
        val etAlasanRetur = findViewById<TextInputEditText>(R.id.etAlasanRetur)
        val btnProsesRetur = findViewById<MaterialButton>(R.id.btnProsesRetur)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarRetur)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Logika saat tombol Cari ditekan
        btnCariNota.setOnClickListener {
            val nomorNota = etNomorNota.text.toString()

            if (nomorNota.isEmpty()) {
                etNomorNota.error = "Nomor nota wajib diisi!"
            } else if (nomorNota == "12345") {
                // Simulasi jika nota DITEMUKAN di database (Anggap saja passwordnya 12345)
                Toast.makeText(this, "Nota valid! Memuat data...", Toast.LENGTH_SHORT).show()
                layoutHasilRetur.visibility = View.VISIBLE // Tampilkan form lanjutan
            } else {
                // Simulasi jika nota TIDAK DITEMUKAN
                Toast.makeText(this, "Error: Nota Tidak Ditemukan!", Toast.LENGTH_LONG).show()
                layoutHasilRetur.visibility = View.GONE // Sembunyikan form
            }
        }

        // Logika saat tombol Proses Retur ditekan
        btnProsesRetur.setOnClickListener {
            val alasan = etAlasanRetur.text.toString()

            if (alasan.isEmpty()) {
                etAlasanRetur.error = "Mohon isi alasan retur"
            } else {
                Toast.makeText(this, "Retur diproses. Silakan keluarkan uang / ganti barang.", Toast.LENGTH_LONG).show()
                // Nanti di sini data dikirim ke Backend untuk update stok inventori
                finish() // Menutup halaman retur dan kembali ke halaman sebelumnya
            }
        }
    }
}