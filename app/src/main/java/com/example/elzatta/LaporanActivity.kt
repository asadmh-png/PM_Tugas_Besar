package com.example.elzatta

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LaporanActivity : AppCompatActivity() {

    private lateinit var rvLaporanShift: RecyclerView
    private lateinit var adapter: LaporanShiftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarLaporan)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        rvLaporanShift = findViewById(R.id.rvLaporanShift)
        rvLaporanShift.layoutManager = LinearLayoutManager(this)

        loadDataShift()
    }

    private fun loadDataShift() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@LaporanActivity)
            val listShift = db.shiftSessionDao().getAllShiftSessions()

            withContext(Dispatchers.Main) {
                adapter = LaporanShiftAdapter(listShift)
                rvLaporanShift.adapter = adapter
            }
        }
    }

    class LaporanShiftAdapter(private val list: List<ShiftSession>) :
        RecyclerView.Adapter<LaporanShiftAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvWaktu: TextView = view.findViewById(R.id.tvWaktuTutup)
            val tvTotalSistem: TextView = view.findViewById(R.id.tvTotalSistemLaporan)
            val tvTotalFisik: TextView = view.findViewById(R.id.tvTotalFisikLaporan)
            val tvSelisih: TextView = view.findViewById(R.id.tvSelisihLaporan)
            val tvCatatan: TextView = view.findViewById(R.id.tvCatatanLaporan)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_laporan_shift, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.tvWaktu.text = "Waktu: ${item.waktuTutup}"
            holder.tvTotalSistem.text = "Rp ${item.totalSistem}"
            holder.tvTotalFisik.text = "Rp ${item.totalFisik}"
            
            if (item.selisih == 0) {
                holder.tvSelisih.text = "Rp 0 (Sesuai)"
                holder.tvSelisih.setTextColor(Color.parseColor("#388E3C"))
            } else if (item.selisih < 0) {
                holder.tvSelisih.text = "Rp ${item.selisih} (Minus)"
                holder.tvSelisih.setTextColor(Color.RED)
            } else {
                holder.tvSelisih.text = "Rp +${item.selisih} (Lebih)"
                holder.tvSelisih.setTextColor(Color.parseColor("#F57C00"))
            }
            
            holder.tvCatatan.text = "Catatan: ${item.catatan}"
        }

        override fun getItemCount(): Int = list.size
    }
}
