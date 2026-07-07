package com.example.elzatta

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo

@Entity(tableName = "sale_transactions")
data class SaleTransaction(
    @PrimaryKey
    val nomorNota: String,
    val tanggal: String,
    val totalHarga: Int,
    var statusTransaksi: String, // "Lunas" atau "Diretur"
    val metodeBayar: String,
    @ColumnInfo(name = "is_closed") var isClosed: Boolean = false
)

@Entity(
    tableName = "transaction_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleTransaction::class,
            parentColumns = ["nomorNota"],
            childColumns = ["nomorNota"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nomorNota: String,
    val barcode: String,
    val nama: String,
    val qty: Int,
    val harga: Int
)
