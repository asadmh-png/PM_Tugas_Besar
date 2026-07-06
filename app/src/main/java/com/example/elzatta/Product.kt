package com.example.elzatta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val barcode: String,
    val nama: String,
    val harga: Int,
    val stok: Int
)
