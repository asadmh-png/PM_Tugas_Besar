package com.example.elzatta

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "shift_sessions")
data class ShiftSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "waktu_tutup") val waktuTutup: String,
    @ColumnInfo(name = "total_sistem") val totalSistem: Int,
    @ColumnInfo(name = "total_fisik") val totalFisik: Int,
    val selisih: Int,
    val catatan: String
)
