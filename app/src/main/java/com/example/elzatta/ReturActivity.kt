package com.example.elzatta

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReturActivity : AppCompatActivity() {

    private var transactionAktif: SaleTransaction? = null
    private var itemAktif: List<TransactionItem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retur)

        val etNomorNota = findViewById<TextInputEditText>(R.id.etNomorNota)
        val btnCariNota = findViewById<MaterialButton>(R.id.btnCariNota)
        val layoutHasilRetur = findViewById<LinearLayout>(R.id.layoutHasilRetur)
        val tvDetailNota = findViewById<TextView>(R.id.tvDetailNota)
        val containerItems = findViewById<LinearLayout>(R.id.containerItemsRetur)
        val etAlasanRetur = findViewById<TextInputEditText>(R.id.etAlasanRetur)
        val btnProsesRetur = findViewById<MaterialButton>(R.id.btnProsesRetur)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarRetur)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnCariNota.setOnClickListener {
            val nomorNota = etNomorNota.text.toString().trim()

            if (nomorNota.isEmpty()) {
                etNomorNota.error = "Nomor nota wajib diisi!"
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@ReturActivity)
                val tx = db.transactionDao().getTransactionByNota(nomorNota)
                val items = db.transactionDao().getItemsByNota(nomorNota)

                withContext(Dispatchers.Main) {
                    if (tx != null) {
                        transactionAktif = tx
                        itemAktif = items
                        
                        if (tx.statusTransaksi == "Diretur") {
                            Toast.makeText(this@ReturActivity, "Nota ini sudah pernah diretur!", Toast.LENGTH_LONG).show()
                            layoutHasilRetur.visibility = View.GONE
                            return@withContext
                        }

                        tvDetailNota.text = "Tanggal: ${tx.tanggal}\nTotal: Rp ${tx.totalHarga}\nStatus: ${tx.statusTransaksi}"
                        
                        containerItems.removeAllViews()
                        items.forEach { item ->
                            val tvItem = TextView(this@ReturActivity)
                            tvItem.text = "• ${item.nama} (${item.qty} x Rp ${item.harga})"
                            tvItem.setPadding(0, 8, 0, 8)
                            containerItems.addView(tvItem)
                        }
                        
                        layoutHasilRetur.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(this@ReturActivity, "Nota Tidak Ditemukan!", Toast.LENGTH_LONG).show()
                        layoutHasilRetur.visibility = View.GONE
                    }
                }
            }
        }

        btnProsesRetur.setOnClickListener {
            val alasan = etAlasanRetur.text.toString()
            val tx = transactionAktif

            if (tx == null) return@setOnClickListener

            if (alasan.isEmpty()) {
                etAlasanRetur.error = "Mohon isi alasan retur"
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@ReturActivity)
                
                // 1. Update Status Transaksi
                tx.statusTransaksi = "Diretur"
                db.transactionDao().updateTransaction(tx)
                
                // 2. Kembalikan Stok ke database
                itemAktif.forEach { item ->
                    db.productDao().tambahStok(item.barcode, item.qty)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ReturActivity, "Retur Berhasil! Stok telah diperbarui.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }
}
