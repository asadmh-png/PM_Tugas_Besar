package com.example.elzatta

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PesananTertundaAdapter(private val listData: List<TransaksiTertunda>) :
    RecyclerView.Adapter<PesananTertundaAdapter.ViewHolder>() {

    // 1. Definisikan ViewHolder hanya satu kali
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvIdTransaksi)
        val tvWaktu: TextView = view.findViewById(R.id.tvWaktuTransaksi)
        val tvTotal: TextView = view.findViewById(R.id.tvTotalTertunda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Menggunakan layout kustom item_pesanan_tertunda.xml
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pesanan_tertunda, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.tvId.text = item.id
        holder.tvWaktu.text = "Waktu: ${item.waktu}"
        holder.tvTotal.text = item.total

        // 2. Logika klik di sini
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, TransaksiActivity::class.java)
            intent.putExtra("ID_TRANSAKSI_TERTUNDA", item.id)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listData.size
}