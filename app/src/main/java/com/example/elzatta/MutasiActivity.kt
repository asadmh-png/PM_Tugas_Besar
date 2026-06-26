package com.example.elzatta

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MutasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mutasi)

        // Kenalkan UI
        val rgJenisMutasi = findViewById<RadioGroup>(R.id.rgJenisMutasi)
        val layoutKirim = findViewById<LinearLayout>(R.id.layoutKirim)
        val layoutTerima = findViewById<LinearLayout>(R.id.layoutTerima)
        val actvTokoTujuan = findViewById<AutoCompleteTextView>(R.id.actvTokoTujuan)
        val etBarcodeMutasi = findViewById<TextInputEditText>(R.id.etBarcodeMutasi)
        val etNomorResi = findViewById<TextInputEditText>(R.id.etNomorResi)
        val btnProsesMutasi = findViewById<MaterialButton>(R.id.btnProsesMutasi)

        // Setup Dropdown Toko Tujuan
        val daftarToko = listOf("Elzatta BIP", "Elzatta Trans Studio", "Elzatta Ciwalk", "Gudang Pusat")
        val adapterToko = ArrayAdapter(this, android.R.layout.simple_list_item_1, daftarToko)
        actvTokoTujuan.setAdapter(adapterToko)

        // Logika Menyembunyikan/Menampilkan Form berdasarkan Pilihan
        rgJenisMutasi.setOnCheckedChangeListener { _, checkedId ->
            btnProsesMutasi.isEnabled = true // Hidupkan tombol

            if (checkedId == R.id.rbKirim) {
                // Jika pilih Kirim: Tampilkan form kirim, sembunyikan form terima
                layoutKirim.visibility = View.VISIBLE
                layoutTerima.visibility = View.GONE
                btnProsesMutasi.text = "KIRIM BARANG (TO)"
            } else if (checkedId == R.id.rbTerima) {
                // Jika pilih Terima: Tampilkan form terima, sembunyikan form kirim
                layoutTerima.visibility = View.VISIBLE
                layoutKirim.visibility = View.GONE
                btnProsesMutasi.text = "TERIMA BARANG (TI)"
            }
        }

        // Logika saat tombol Proses ditekan
        btnProsesMutasi.setOnClickListener {
            val idTerpilih = rgJenisMutasi.checkedRadioButtonId

            if (idTerpilih == R.id.rbKirim) {
                val toko = actvTokoTujuan.text.toString()
                val barcode = etBarcodeMutasi.text.toString()

                if (toko.isEmpty() || barcode.isEmpty()) {
                    Toast.makeText(this, "Toko tujuan dan barcode harus diisi!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Memproses pengiriman ke $toko...", Toast.LENGTH_LONG).show()
                    // Nanti sistem akan mengurangi jumlah stok toko pengirim
                }
            } else if (idTerpilih == R.id.rbTerima) {
                val resi = etNomorResi.text.toString()

                if (resi.isEmpty()) {
                    etNomorResi.error = "Nomor resi wajib diisi!"
                } else {
                    Toast.makeText(this, "Mengecek fisik barang untuk resi $resi...", Toast.LENGTH_LONG).show()
                    // Nanti sistem akan menambah jumlah stok toko penerima
                }
            }
        }
    }
}