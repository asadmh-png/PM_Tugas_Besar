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

// 1. Kita buat struktur data sederhana untuk Barang
data class Barang(val nama: String, val qty: Int, val hargaSatuan: Int) {
    val subtotal get() = qty * hargaSatuan
}

class TransaksiActivity : AppCompatActivity() {

    // Siapkan wadah untuk daftar belanjaan
    private val daftarKeranjang = mutableListOf<Barang>()
    private lateinit var adapter: KeranjangAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi)

        // Kenalkan UI ke Kotlin
        val rvKeranjang = findViewById<RecyclerView>(R.id.rvKeranjang)
        val tvTotalBayar = findViewById<TextView>(R.id.tvTotalBayar)
        val etBarcode = findViewById<TextInputEditText>(R.id.etBarcode)
        val btnBayar = findViewById<MaterialButton>(R.id.btnBayar)
        val btnTunda = findViewById<MaterialButton>(R.id.btnTunda)

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
            if (daftarKeranjang.isEmpty()) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Lanjut ke pop-up pembayaran...", Toast.LENGTH_SHORT).show()
                // Nanti di sini kita munculkan Dialog/Pop-up untuk kembalian uang
            }
        }

        btnTunda.setOnClickListener {
            Toast.makeText(this, "Transaksi disimpan sementara", Toast.LENGTH_SHORT).show()
            // Logika membersihkan layar jika ditunda
            daftarKeranjang.clear()
            adapter.notifyDataSetChanged()
            tvTotalBayar.text = "Rp 0"
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