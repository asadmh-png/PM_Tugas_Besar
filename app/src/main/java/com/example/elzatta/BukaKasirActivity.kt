package com.example.elzatta

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BukaKasirActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menyambungkan file Kotlin ini dengan file XML yang barusan kita buat
        setContentView(R.layout.activity_buka_kasir)

        // 1. Kenalkan komponen UI dari XML ke variabel Kotlin
        val actvMesinKasir = findViewById<AutoCompleteTextView>(R.id.actvMesinKasir)
        val etNamaKasir = findViewById<TextInputEditText>(R.id.etNamaKasir)
        val etModalAwal = findViewById<TextInputEditText>(R.id.etModalAwal)
        val rgShift = findViewById<RadioGroup>(R.id.rgShift)
        val btnBukaKasir = findViewById<MaterialButton>(R.id.btnBukaKasir)

        // 2. Buat daftar pilihan untuk Dropdown Nomor Mesin Kasir
        val daftarMesin = listOf("Mesin 01", "Mesin 02", "Mesin 03", "Mesin 04")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, daftarMesin)
        actvMesinKasir.setAdapter(adapter)

        // 3. Logika & Form Validation saat tombol diklik
        btnBukaKasir.setOnClickListener {
            val mesinTerpilih = actvMesinKasir.text.toString()
            val namaKasir = etNamaKasir.text.toString()
            val modalAwal = etModalAwal.text.toString()
            val shiftTerpilihId = rgShift.checkedRadioButtonId

            // --- Mulai Validasi ---
            if (mesinTerpilih.isEmpty()) {
                actvMesinKasir.error = "Pilih mesin kasir terlebih dahulu!"
                return@setOnClickListener // Hentikan proses jika error
            }
            if (namaKasir.isEmpty()) {
                etNamaKasir.error = "Nama kasir tidak boleh kosong!"
                return@setOnClickListener
            }
            if (modalAwal.isEmpty()) {
                etModalAwal.error = "Nominal modal awal harus diisi!"
                return@setOnClickListener
            }
            if (shiftTerpilihId == -1) { // -1 artinya tidak ada RadioButton yang dipilih
                Toast.makeText(this, "Silakan pilih shift kerja!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // --- Selesai Validasi ---

            // Jika semua form valid, ambil teks dari shift yang dipilih
            val rbShiftTerpilih = findViewById<RadioButton>(shiftTerpilihId)
            val namaShift = rbShiftTerpilih.text.toString()

            // ---- PROSES SIMPAN KE DATABASE ROOM ----
            val session = KasirSession(
                nomorMesin = mesinTerpilih,
                namaKasir = namaKasir,
                modalAwal = modalAwal.toDoubleOrNull() ?: 0.0,
                shift = namaShift
            )

            // Jalankan di background thread agar tidak membuat UI lag
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@BukaKasirActivity)
                db.kasirDao().insertSession(session)
                
                // Setelah simpan, balik ke UI thread untuk pindah halaman
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BukaKasirActivity, "Sesi Kasir Berhasil Dibuka!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@BukaKasirActivity, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}