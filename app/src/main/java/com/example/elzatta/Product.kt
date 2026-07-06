package com.example.elzatta

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val barcode: String,
    val nama: String,
    val harga: Int,
    var stok: Int,
    @ColumnInfo(name = "harga_promo") var hargaPromo: Int = 0
)
