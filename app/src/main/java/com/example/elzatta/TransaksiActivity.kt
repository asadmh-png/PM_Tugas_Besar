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

/**
 * Model Data untuk Barang yang ada di dalam Keranjang Belanja
 */
data class Barang(
    val barcode: String,
    val nama: String,
    var qty: Int,
    val hargaSatuan: Int,
    val hargaAsli: Int = hargaSatuan
) {
    // Menghitung subtotal otomatis
    val subtotal get() = qty * hargaSatuan
    // Menghitung total diskon jika ada harga promo
    val totalDiskon get() = (hargaAsli - hargaSatuan) * qty
}

/**
 * Activity Transaksi: Fitur Utama Kasir untuk melakukan penjualan.
 * Mendukung Scan Barcode, Tunda Pesanan, dan Pembayaran.
 */
class TransaksiActivity : AppCompatActivity() {

    // List untuk menyimpan daftar barang yang sedang dibeli
    private val daftarKeranjang = mutableListOf<Barang>()
    private lateinit var adapter: KeranjangAdapter

    /**
     * Mengambil data pesanan yang sebelumnya ditunda dari database lokal
     */
    private fun loadDataTertunda(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@TransaksiActivity)
            val pendingOrder = db.pendingOrderDao().getPendingOrderById(id.toInt())

            pendingOrder?.let { order ->
                withContext(Dispatchers.Main) {
                    daftarKeranjang.clear()
                    // Mengubah string data barang kembali menjadi object (Deserialization)
                    order.daftarBarang.split("|").forEach { stringBarang ->
                        val part = stringBarang.split(":")
                        if (part.size >= 4) {
                            val barcode = part[0]
                            val nama = part[1]
                            val qty = part[2].toInt()
                            val hargaSatuan = part[3].toInt()
                            val hargaAsli = if (part.size == 5) part[4].toInt() else hargaSatuan
                            daftarKeranjang.add(Barang(barcode, nama, qty, hargaSatuan, hargaAsli))
                        }
                    }
                    adapter.notifyDataSetChanged()
                    hitungTotalBayar()

                    // Hapus dari daftar tertunda karena sudah diproses kembali (Recall)
                    CoroutineScope(Dispatchers.IO).launch {
                        db.pendingOrderDao().deletePendingOrder(order)
                    }
                }
            }
        }
    }

    /**
     * Fungsi untuk menghitung total belanjaan dan update ke UI
     */
    private fun hitungTotalBayar() {
        var total = 0
        for (barang in daftarKeranjang) {
            total += barang.subtotal
        }
        val tvTotalBayar = findViewById<TextView>(R.id.tvTotalBayar)
        tvTotalBayar.text = "Rp $total"
    }

    /**
     * Mencari produk berdasarkan barcode dan menambahkannya ke keranjang
     */
    private fun tambahBarangKeKeranjang(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@TransaksiActivity)
            val product = db.productDao().getProductByBarcode(barcode)

            withContext(Dispatchers.Main) {
                if (product != null) {
                    val existingItem = daftarKeranjang.find { it.barcode == barcode }
                    if (existingItem != null) {
                        // Jika barang sudah ada, cukup tambah jumlahnya (Qty)
                        existingItem.qty += 1
                    } else {
                        // Jika barang baru, masukkan ke list dengan mengecek harga promo
                        val hargaFinal = if (product.hargaPromo > 0) product.hargaPromo else product.harga
                        daftarKeranjang.add(Barang(product.barcode, product.nama, 1, hargaFinal, product.harga))
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

        // Inisialisasi UI
        val rvKeranjang = findViewById<RecyclerView>(R.id.rvKeranjang)
        val tvTotalBayar = findViewById<TextView>(R.id.tvTotalBayar)
        val etBarcode = findViewById<TextInputEditText>(R.id.etBarcode)
        val btnBayar = findViewById<MaterialButton>(R.id.btnBayar)
        val btnTunda = findViewById<MaterialButton>(R.id.btnTunda)
        val btnAksesTertunda = findViewById<MaterialButton>(R.id.btnAksesTertunda)
        val idTertunda = intent.getStringExtra("ID_TRANSAKSI_TERTUNDA")

        // Setup Toolbar & Tombol Kembali
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarTransaksi)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Setup RecyclerView untuk daftar belanja
        adapter = KeranjangAdapter(daftarKeranjang)
        rvKeranjang.layoutManager = LinearLayoutManager(this)
        rvKeranjang.adapter = adapter

        // Listener untuk Input Barcode (Merespon tombol Enter/Next pada keyboard/scanner)
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

        // Tombol untuk Membuka Dialog Pembayaran
        btnBayar.setOnClickListener {
            if (daftarKeranjang.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dialog = PembayaranDialogFragment()
            dialog.show(supportFragmentManager, "PembayaranDialog")
        }

        // Tombol untuk Menunda Transaksi (Save for later)
        btnTunda.setOnClickListener {
            if (daftarKeranjang.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = daftarKeranjang.sumOf { it.subtotal }
            // Serialisasi data keranjang menjadi string agar bisa disimpan di DB
            val dataBarang = daftarKeranjang.joinToString("|") {
                "${it.barcode}:${it.nama}:${it.qty}:${it.hargaSatuan}:${it.hargaAsli}"
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

        // Tombol untuk melihat daftar pesanan yang sedang ditunda
        btnAksesTertunda.setOnClickListener {
            val intent = Intent(this, PesananTertundaActivity::class.java)
            startActivity(intent)
        }

        // Cek apakah activity dibuka karena ingin memanggil pesanan tertunda
        if (idTertunda != null) {
            loadDataTertunda(idTertunda)
        }
    }

    /**
     * Fungsi Finalisasi Transaksi: Simpan ke Database, Update Stok, dan Bersihkan Keranjang.
     */
    fun selesaikanTransaksi(metode: String): String {
        val nomorNota = "ELZ-${System.currentTimeMillis() % 1000000}"
        val total = getTotalBelanja()
        val tanggal = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        val copyKeranjang = ArrayList(daftarKeranjang)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@TransaksiActivity)
            
            // 1. Simpan Header Transaksi (Metadata Transaksi)
            val transaction = SaleTransaction(
                nomorNota = nomorNota,
                tanggal = tanggal,
                totalHarga = total,
                statusTransaksi = "Lunas",
                metodeBayar = metode
            )
            
            // 2. Simpan Detail Item Transaksi
            val items = copyKeranjang.map { 
                TransactionItem(
                    nomorNota = nomorNota,
                    barcode = it.barcode,
                    nama = it.nama,
                    qty = it.qty,
                    harga = it.hargaSatuan
                )
            }
            
            db.transactionDao().insertFullTransaction(transaction, items)
            
            // 3. Update Stok di Inventory (Pengurangan Stok)
            copyKeranjang.forEach { barang ->
                db.productDao().updateStok(barang.barcode, barang.qty)
            }

            withContext(Dispatchers.Main) {
                daftarKeranjang.clear()
                adapter.notifyDataSetChanged()
                hitungTotalBayar()
            }
        }
        return nomorNota
    }

    fun getTotalBelanja(): Int {
        return daftarKeranjang.sumOf { it.subtotal }
    }

    /**
     * Memformat daftar belanja menjadi teks untuk dicetak pada struk
     */
    fun getDaftarBarangString(): String {
        val sb = StringBuilder()
        daftarKeranjang.forEach { 
            val diskonPerItem = it.hargaAsli - it.hargaSatuan
            sb.append("${it.nama.padEnd(20)} x${it.qty}\n")
            if (diskonPerItem > 0) {
                sb.append("  Harga Normal: Rp ${it.hargaAsli}\n")
                sb.append("  Diskon Promo: -Rp ${diskonPerItem * it.qty}\n")
            } else {
                sb.append("  Harga: Rp ${it.hargaSatuan}\n")
            }
            sb.append("  Subtotal: Rp ${it.subtotal}\n")
            sb.append("--------------------------------\n")
        }
        return sb.toString()
    }

    // =========================================================================
    // INNER CLASS: ADAPTER (Jembatan antara data List Barang dan Tampilan List)
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
