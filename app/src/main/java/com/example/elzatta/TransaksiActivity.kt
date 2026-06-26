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

// 1. Kita buat struktur data sederhana untuk Barang
data class Barang(val nama: String, val qty: Int, val hargaSatuan: Int) {
    val subtotal get() = qty * hargaSatuan
}

class TransaksiActivity : AppCompatActivity() {

    // Siapkan wadah untuk daftar belanjaan
    private val daftarKeranjang = mutableListOf<Barang>()
    private lateinit var adapter: KeranjangAdapter

    // Tambahkan fungsi ini di dalam kelas TransaksiActivity
    private fun loadDataTertunda(id: String) {
        // 1. Logika untuk mengambil data dari database/list berdasarkan ID
        // Contoh sederhana:
        Toast.makeText(this, "Memuat data pesanan: $id", Toast.LENGTH_SHORT).show()

        // 2. Di sini kamu akan mengisi 'daftarKeranjang' dengan data yang diambil
        // Contoh:
        // val data = database.getTransaksi(id)
        // daftarKeranjang.clear()
        // daftarKeranjang.addAll(data)

        // 3. Update tampilan agar RecyclerView menampilkan data baru
        adapter.notifyDataSetChanged()

        // 4. Hitung ulang total bayar
        hitungTotalBayar()
    }

    // Pastikan kamu punya fungsi untuk menghitung ulang total
    private fun hitungTotalBayar() {
        var total = 0
        for (barang in daftarKeranjang) {
            total += barang.subtotal
        }
        val tvTotalBayar = findViewById<TextView>(R.id.tvTotalBayar)
        tvTotalBayar.text = "Rp $total"
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

        // 2. Isi dengan Dummy Data (Data Palsu untuk cek UI)
        daftarKeranjang.add(Barang("Tunik Elzatta Pink", 1, 150000))
        daftarKeranjang.add(Barang("Scarf Motif Bunga", 2, 75000))
        daftarKeranjang.add(Barang("Bergo Instan Hitam", 1, 85000))

        // 3. Setup RecyclerView
        adapter = KeranjangAdapter(daftarKeranjang)
        rvKeranjang.layoutManager = LinearLayoutManager(this)
        rvKeranjang.adapter = adapter

        // Hitung total bayar
        var totalKeseluruhan = 0
        for (barang in daftarKeranjang) {
            totalKeseluruhan += barang.subtotal
        }
        tvTotalBayar.text = "Rp $totalKeseluruhan"

        // 4. Logika Tombol
        btnBayar.setOnClickListener {
            val dialog = PembayaranDialogFragment()
            dialog.show(supportFragmentManager, "PembayaranDialog")
        }

        btnTunda.setOnClickListener {
            Toast.makeText(this, "Transaksi disimpan sementara", Toast.LENGTH_SHORT).show()
            // Logika membersihkan layar jika ditunda
            daftarKeranjang.clear()
            adapter.notifyDataSetChanged()
            tvTotalBayar.text = "Rp 0"
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