package com.example.elzatta

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CekStokActivity : AppCompatActivity() {

    private lateinit var rvProdukStok: RecyclerView
    private lateinit var adapter: ProdukStokAdapter
    private val listProduk = mutableListOf<Product>()
    private val listFilter = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_stok)

        val etBarcodeCek = findViewById<TextInputEditText>(R.id.etBarcodeCek)
        rvProdukStok = findViewById(R.id.rvProdukStok)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarCekStok)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = ProdukStokAdapter(listFilter)
        rvProdukStok.layoutManager = LinearLayoutManager(this)
        rvProdukStok.adapter = adapter

        loadDataFromDb()

        // Logic Pencarian
        etBarcodeCek.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadDataFromDb() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@CekStokActivity)
            val products = db.productDao().getAllProducts()
            
            withContext(Dispatchers.Main) {
                listProduk.clear()
                listProduk.addAll(products)
                listFilter.clear()
                listFilter.addAll(products)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun filter(text: String) {
        listFilter.clear()
        if (text.isEmpty()) {
            listFilter.addAll(listProduk)
        } else {
            val query = text.lowercase()
            for (item in listProduk) {
                if (item.nama.lowercase().contains(query) || item.barcode.contains(query)) {
                    listFilter.add(item)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    class ProdukStokAdapter(private val list: List<Product>) :
        RecyclerView.Adapter<ProdukStokAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvNama: TextView = view.findViewById(R.id.tvNamaProduk)
            val tvBarcode: TextView = view.findViewById(R.id.tvBarcodeProduk)
            val tvHarga: TextView = view.findViewById(R.id.tvHargaProduk)
            val tvStok: TextView = view.findViewById(R.id.tvStokProduk)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_produk_stok, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.tvNama.text = item.nama
            holder.tvBarcode.text = item.barcode
            holder.tvHarga.text = "Rp ${item.harga}"
            holder.tvStok.text = item.stok.toString()
            
            if (item.stok < 10) {
                holder.tvStok.setTextColor(android.graphics.Color.RED)
            } else {
                holder.tvStok.setTextColor(android.graphics.Color.parseColor("#1976D2"))
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
