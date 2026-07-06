package com.example.elzatta

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BukaKasirActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buka_kasir)

        val actvMesinKasir = findViewById<AutoCompleteTextView>(R.id.actvMesinKasir)
        val etNamaKasir = findViewById<TextInputEditText>(R.id.etNamaKasir)
        val etModalAwal = findViewById<TextInputEditText>(R.id.etModalAwal)
        val rgShift = findViewById<RadioGroup>(R.id.rgShift)
        val btnBukaKasir = findViewById<MaterialButton>(R.id.btnBukaKasir)

        val daftarMesin = listOf("Mesin 01", "Mesin 02", "Mesin 03", "Mesin 04")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, daftarMesin)
        actvMesinKasir.setAdapter(adapter)

        // Simulasi penambahan user jika belum ada (hanya untuk testing)
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@BukaKasirActivity)
            db.userDao().insertUser(User("K001", "Ahmad Kasir", "Kasir"))
            db.userDao().insertUser(User("K002", "Siti Kasir", "Kasir"))
        }

        btnBukaKasir.setOnClickListener {
            val mesinTerpilih = actvMesinKasir.text.toString()
            val inputIdUser = etNamaKasir.text.toString()
            val modalAwalStr = etModalAwal.text.toString()
            val shiftTerpilihId = rgShift.checkedRadioButtonId

            // Validasi Input
            if (mesinTerpilih.isEmpty()) {
                actvMesinKasir.error = getString(R.string.error_mesin_kosong)
                return@setOnClickListener
            }
            if (inputIdUser.isEmpty()) {
                etNamaKasir.error = getString(R.string.error_id_kosong)
                return@setOnClickListener
            }
            if (modalAwalStr.isEmpty()) {
                etModalAwal.error = getString(R.string.error_modal_kosong)
                return@setOnClickListener
            }
            if (shiftTerpilihId == -1) {
                Toast.makeText(this, getString(R.string.error_shift_kosong), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val modalAwal = modalAwalStr.toDoubleOrNull() ?: 0.0

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@BukaKasirActivity)
                val user = db.userDao().getUserById(inputIdUser)

                withContext(Dispatchers.Main) {
                    if (user != null) {
                        val rbShiftTerpilih = findViewById<RadioButton>(shiftTerpilihId)
                        val namaShift = rbShiftTerpilih.text.toString()
                        saveSessionAndNavigate(user.namaLengkap, mesinTerpilih, modalAwal, namaShift)
                    } else {
                        etNamaKasir.error = getString(R.string.error_id_tidak_terdaftar)
                    }
                }
            }
        }
    }

    private fun saveSessionAndNavigate(namaKasir: String, mesin: String, modal: Double, shift: String) {
        val session = KasirSession(
            nomorMesin = mesin,
            namaKasir = namaKasir,
            modalAwal = modal,
            shift = shift
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@BukaKasirActivity)
            db.kasirDao().insertSession(session)
            
            withContext(Dispatchers.Main) {
                Toast.makeText(this@BukaKasirActivity, getString(R.string.success_buka_kasir, namaKasir), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@BukaKasirActivity, DashboardActivity::class.java))
                finish()
            }
        }
    }
}
