package com.example.elzatta

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MutasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mutasi)

        // Binding UI
        val rgJenisMutasi = findViewById<RadioGroup>(R.id.rgJenisMutasi)
        val layoutFormMutasi = findViewById<LinearLayout>(R.id.layoutFormMutasi)
        val tilTokoTujuan = findViewById<TextInputLayout>(R.id.tilTokoTujuan)
        val tilNomorResi = findViewById<TextInputLayout>(R.id.tilNomorResi)
        
        val actvTokoTujuan = findViewById<AutoCompleteTextView>(R.id.actvTokoTujuan)
        val etBarcodeMutasi = findViewById<TextInputEditText>(R.id.etBarcodeMutasi)
        val etNomorResi = findViewById<TextInputEditText>(R.id.etNomorResi)
        val etQtyMutasi = findViewById<TextInputEditText>(R.id.etQtyMutasi)
        val btnProsesMutasi = findViewById<MaterialButton>(R.id.btnProsesMutasi)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarMutasi)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Setup Dropdown Toko
        val daftarToko = listOf("Elzatta BIP", "Elzatta TSM", "Elzatta Ciwalk", "Gudang Pusat")
        val adapterToko = ArrayAdapter(this, android.R.layout.simple_list_item_1, daftarToko)
        actvTokoTujuan.setAdapter(adapterToko)

        // Logic Validasi Button Enabled/Disabled
        val inputWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isTypeSelected = rgJenisMutasi.checkedRadioButtonId != -1
                val isBarcodeFilled = etBarcodeMutasi.text?.isNotEmpty() == true
                val isQtyFilled = etQtyMutasi.text?.isNotEmpty() == true
                
                btnProsesMutasi.isEnabled = isTypeSelected && isBarcodeFilled && isQtyFilled
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etBarcodeMutasi.addTextChangedListener(inputWatcher)
        etQtyMutasi.addTextChangedListener(inputWatcher)

        rgJenisMutasi.setOnCheckedChangeListener { _, checkedId ->
            layoutFormMutasi.visibility = View.VISIBLE
            
            if (checkedId == R.id.rbKirim) {
                tilTokoTujuan.visibility = View.VISIBLE
                tilNomorResi.visibility = View.GONE
                btnProsesMutasi.text = "PROSES KIRIM (TO)"
            } else {
                tilTokoTujuan.visibility = View.GONE
                tilNomorResi.visibility = View.VISIBLE
                btnProsesMutasi.text = "PROSES TERIMA (TI)"
            }
            // Trigger validasi ulang saat radio berubah
            btnProsesMutasi.isEnabled = etBarcodeMutasi.text?.isNotEmpty() == true && etQtyMutasi.text?.isNotEmpty() == true
        }

        btnProsesMutasi.setOnClickListener {
            val barcode = etBarcodeMutasi.text.toString().trim()
            val qtyString = etQtyMutasi.text.toString().trim()
            val qty = qtyString.toIntOrNull() ?: 0
            val isTO = rgJenisMutasi.checkedRadioButtonId == R.id.rbKirim

            if (qty <= 0) {
                etQtyMutasi.error = "Jumlah harus lebih dari 0"
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@MutasiActivity)
                val product = db.productDao().getProductByBarcode(barcode)

                withContext(Dispatchers.Main) {
                    if (product == null) {
                        Toast.makeText(this@MutasiActivity, "Barang tidak ditemukan!", Toast.LENGTH_SHORT).show()
                        return@withContext
                    }

                    if (isTO) {
                        // Logika Transfer Out
                        if (product.stok < qty) {
                            Toast.makeText(this@MutasiActivity, "Stok tidak cukup! (Sisa: ${product.stok})", Toast.LENGTH_LONG).show()
                        } else {
                            db.productDao().updateStok(barcode, qty)
                            Toast.makeText(this@MutasiActivity, "TO Berhasil: ${product.nama} berkurang $qty", Toast.LENGTH_LONG).show()
                            resetForm(etBarcodeMutasi, etQtyMutasi, actvTokoTujuan, etNomorResi)
                        }
                    } else {
                        // Logika Transfer In
                        db.productDao().tambahStok(barcode, qty)
                        Toast.makeText(this@MutasiActivity, "TI Berhasil: ${product.nama} bertambah $qty", Toast.LENGTH_LONG).show()
                        resetForm(etBarcodeMutasi, etQtyMutasi, actvTokoTujuan, etNomorResi)
                    }
                }
            }
        }
    }

    private fun resetForm(barcode: TextInputEditText, qty: TextInputEditText, toko: AutoCompleteTextView, resi: TextInputEditText) {
        barcode.text?.clear()
        qty.text?.clear()
        toko.text?.clear()
        resi.text?.clear()
        barcode.requestFocus()
    }
}
