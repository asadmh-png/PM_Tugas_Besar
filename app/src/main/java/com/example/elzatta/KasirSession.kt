package com.example.elzatta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kasir_session")
data class KasirSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nomorMesin: String,
    val namaKasir: String,
    val modalAwal: Double,
    val shift: String,
    val waktuBuka: Long = System.currentTimeMillis(),
    val isClosed: Boolean = false
)
