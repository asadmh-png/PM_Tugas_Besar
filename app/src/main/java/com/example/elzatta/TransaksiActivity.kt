package com.example.elzatta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.content.Intent
import android.view.inputmethod.EditorInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 1. Kita buat struktur data sederhana untuk Barang
data class Barang(val barcode: String, val nama: String, var qty: Int, val hargaSatuan: Int) {
    val subtotal get() = qty * hargaSatuan
}

class TransaksiActivity : AppCompatActivity() {

    // Siapkan wadah untuk daftar belanjaan
    private val daftarKeranjang = mutableListOf<Barang>()
    private lateinit var adapter: KeranjangAdapter

    // Tambahkan fungsi ini di dalam kelas TransaksiActivity
    private fun loadDataTertunda(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@TransaksiActivity)
            val pendingOrder = db.pendingOrderDao().getPendingOrderById(id.toInt())

            pendingOrder?.let { order ->
                withContext(Dispatchers.Main) {
                    daftarKeranjang.clear()
                    // Deserialize string ke object Barang
                    order.daftarBarang.split("|").forEach { stringBarang ->
                        val part = stringBarang.split(":")
                        if (part.size == 4) {
                            daftarKeranjang.add(Barang(part[0], part[1], part[2].toInt(), part[3].toInt()))
                        }
                    }
                    adapter.notifyDataSetChanged()
                    hitungTotalBayar()

                    // Hapus dari daftar tertunda karena sudah diproses kembali
                    CoroutineScope(Dispatchers.IO).launch {
                        db.pendingOrderDao().deletePendingOrder(order)
                    }
                }
            }
        }
    }

    private fun hitungTotalBayar() {
        var total = 0
        for (barang in daftarKeranjang) {
            total += barang.subtotal
        }
        val tvTotalBayar = findViewById<TextView>(R.id.tvTotalBayar)
        tvTotalBayar.text = "Rp $total"
    }

    private fun tambahBarangKeKeranjang(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@TransaksiActivity)
            val product = db.productDao().getProductByBarcode(barcode)

            withContext(Dispatchers.Main) {
                if (product != null) {
                    val existingItem = daftarKeranjang.find { it.barcode == barcode }
                    if (existingItem != null) {
                        existingItem.qty += 1
                    } else {
                        daftarKeranjang.add(Barang(product.barcode, product.nama, 1, product.harga))
                    }
                    adapter.notifyDataSetChanged()
                    hitungTotalBayar()
                    findViewById<TextInputEditText>(R.id.etBarcode).text?.clear()
                } else {
                    Toast.makeText(this@TransaksiActivity, "Produk tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi)

        // Kenalkan UI ke Kotlin
        val rvKeranjang = findViewById<RecyclerView>(R.id.rvKeranjang)
        val tvTotalBayar = findViewById<TextView>(R.id.tvTotalBayar)
        val etBarcode = findViewById<TextInputEditText>(R.id.etBarcode)
        val btnBayar = findViewById<MaterialButton>(R.id.btnBayar)
        val btnTunda = findViewById<MaterialButton>(R.id.btnTunda)
        val btnAksesTertunda = findViewById<MaterialButton>(R.id.btnAksesTertunda)
        val idTertunda = intent.getStringExtra("ID_TRANSAKSI_TERTUNDA")

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarTransaksi)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 3. Setup RecyclerView
        adapter = KeranjangAdapter(daftarKeranjang)
        rvKeranjang.layoutManager = LinearLayoutManager(this)
        rvKeranjang.adapter = adapter

        etBarcode.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                val barcode = etBarcode.text.toString()
                if (barcode.isNotEmpty()) {
                    tambahBarangKeKeranjang(barcode)
                }
                true
            } else {
                false
            }
        }

        // 4. Logika Tombol
        btnBayar.setOnClickListener {
            if (daftarKeranjang.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dialog = PembayaranDialogFragment()
            dialog.show(supportFragmentManager, "PembayaranDialog")
        }

        btnTunda.setOnClickListener {
            if (daftarKeranjang.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = daftarKeranjang.sumOf { it.subtotal }
            // Serialize data barang menjadi string sederhana
            val dataBarang = daftarKeranjang.joinToString("|") {
                "${it.barcode}:${it.nama}:${it.qty}:${it.hargaSatuan}"
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@TransaksiActivity)
                db.pendingOrderDao().insertPendingOrder(
                    PendingOrder(totalHarga = total, daftarBarang = dataBarang)
                )

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TransaksiActivity, "Pesanan ditunda", Toast.LENGTH_SHORT).show()
                    daftarKeranjang.clear()
                    adapter.notifyDataSetChanged()
                    tvTotalBayar.text = "Rp 0"
                }
            }
        }

        btnAksesTertunda.setOnClickListener {
            val intent = Intent(this, PesananTertundaActivity::class.java)
            startActivity(intent)
        }

        if (idTertunda != null) {
            // Panggil fungsi untuk ambil data dari database lokal berdasarkan ID tersebut
            loadDataTertunda(idTertunda)
        }
    }

    // =========================================================================
    // INNER CLASS: ADAPTER (Ini adalah "jembatan" antara data dan RecyclerView)
    // =========================================================================
    class KeranjangAdapter(private val listBarang: List<Barang>) : RecyclerView.Adapter<KeranjangAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvNama: TextView = view.findViewById(R.id.tvNamaBarang)
            val tvQtyHarga: TextView = view.findViewById(R.id.tvQtyHarga)
            val tvSubtotal: TextView = view.findViewById(R.id.tvSubtotal)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val barang = listBarang[position]
            holder.tvNama.text = barang.nama
            holder.tvQtyHarga.text = "${barang.qty} x Rp ${barang.hargaSatuan}"
            holder.tvSubtotal.text = "Rp ${barang.subtotal}"
        }

        override fun getItemCount(): Int = listBarang.size
    }
}