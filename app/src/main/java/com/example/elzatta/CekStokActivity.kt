package com.example.elzatta

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CekStokActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_stok)

        val etBarcodeCek = findViewById<TextInputEditText>(R.id.etBarcodeCek)
        val btnCariStok = findViewById<MaterialButton>(R.id.btnCariStok)
        val layoutHasilStok = findViewById<LinearLayout>(R.id.layoutHasilStok)
        val tvNamaBarangCek = findViewById<TextView>(R.id.tvNamaBarangCek)
        val tvHargaBarang = findViewById<TextView>(R.id.tvHargaBarang)
        val tvJumlahStok = findViewById<TextView>(R.id.tvJumlahStok)

        btnCariStok.setOnClickListener {
            val barcode = etBarcodeCek.text.toString()

            if (barcode.isEmpty()) {
                etBarcodeCek.error = "Barcode tidak boleh kosong"
            } else {
                // Simulasi data ditemukan
                Toast.makeText(this, "Mencari data...", Toast.LENGTH_SHORT).show()
                layoutHasilStok.visibility = View.VISIBLE

                // Isi dengan data dummy
                tvNamaBarangCek.text = "Tunik Elzatta Pink ($barcode)"
                tvHargaBarang.text = "Harga: Rp 150.000"
                tvJumlahStok.text = "12 Pcs"
            }
        }
    }
}