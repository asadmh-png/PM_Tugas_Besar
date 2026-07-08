package com.example.elzatta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_orders")
data class PendingOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val totalHarga: Int,
    val waktu: Long = System.currentTimeMillis(),
    val daftarBarang: String // Format: "Barcode:Nama:Qty:Harga|..."
)
