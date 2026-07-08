package com.example.elzatta

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransaksiBerhasilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi_berhasil)

        val tvWaktu = findViewById<TextView>(R.id.tvWaktuBerhasil)
        val btnCetak = findViewById<MaterialButton>(R.id.btnCetakStruk)
        val btnSelesai = findViewById<MaterialButton>(R.id.btnSelesai)

        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        tvWaktu.text = sdf.format(Date())

        btnCetak.setOnClickListener {
            val strukText = generateSimulasiStruk()
            android.app.AlertDialog.Builder(this)
                .setTitle("Struk Belanja")
                .setMessage(strukText)
                .setPositiveButton("OK", null)
                .show()
        }

        btnSelesai.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun generateSimulasiStruk(): String {
        val metode = intent.getStringExtra("METODE_BAYAR") ?: "CASH"
        val total = intent.getIntExtra("TOTAL_BELANJA", 0)
        val bayar = intent.getIntExtra("UANG_BAYAR", 0)
        val kembali = intent.getIntExtra("UANG_KEMBALI", 0)
        val daftarBarang = intent.getStringExtra("DAFTAR_BARANG") ?: ""
        val nomorNota = intent.getStringExtra("NOMOR_NOTA") ?: "ELZ-${System.currentTimeMillis() % 100000}"

        return """
            ELZATTA HIJAB - STORE
            =========================
            Waktu: ${SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date())}
            No. Nota: $nomorNota
            Metode: $metode
            -------------------------
            $daftarBarang
            -------------------------
            TOTAL    : Rp $total
            BAYAR    : Rp $bayar
            KEMBALI  : Rp $kembali
            =========================
            Terima Kasih
        """.trimIndent()
    }
}
