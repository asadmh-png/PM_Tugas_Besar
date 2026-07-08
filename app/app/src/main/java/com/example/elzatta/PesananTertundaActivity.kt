package com.example.elzatta

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TransaksiTertunda(val id: String, val waktu: String, val total: String)

class PesananTertundaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan_tertunda)

        val rv = findViewById<RecyclerView>(R.id.rvPesananTertunda)
        val tvEmpty = findViewById<TextView>(R.id.tvEmptyState)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarPesananTertunda)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        rv.layoutManager = LinearLayoutManager(this)
        loadData(rv, tvEmpty)
    }

    private fun loadData(rv: RecyclerView, tvEmpty: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@PesananTertundaActivity)
            val listFromDb = db.pendingOrderDao().getAllPendingOrders()

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

            val uiList = listFromDb.map {
                TransaksiTertunda(
                    id = it.id.toString(),
                    waktu = sdf.format(Date(it.waktu)),
                    total = "Rp ${it.totalHarga}"
                )
            }

            withContext(Dispatchers.Main) {
                if (uiList.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    rv.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.GONE
                    rv.visibility = View.VISIBLE
                    rv.adapter = PesananTertundaAdapter(uiList)
                }
            }
        }
    }
}