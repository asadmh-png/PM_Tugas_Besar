package com.example.elzatta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    val namaLengkap: String,
    val role: String
)
