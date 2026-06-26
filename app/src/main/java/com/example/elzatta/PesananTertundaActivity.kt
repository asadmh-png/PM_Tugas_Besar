package com.example.elzatta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
data class TransaksiTertunda(val id: String, val waktu: String, val total: String)

class PesananTertundaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan_tertunda)

        val rv = findViewById<RecyclerView>(R.id.rvPesananTertunda)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarPesananTertunda)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Simulasi data (nanti ambil dari database lokal/Room)
        val listData = listOf(
            TransaksiTertunda("TRX-001", "10:05", "Rp 150.000"),
            TransaksiTertunda("TRX-002", "10:15", "Rp 85.000")
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = PesananTertundaAdapter(listData)
    }
}