package com.example.elzatta

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PendingOrderDao {
    @Insert
    suspend fun insertPendingOrder(order: PendingOrder)

    @Query("SELECT * FROM pending_orders ORDER BY waktu DESC")
    suspend fun getAllPendingOrders(): List<PendingOrder>

    @Query("SELECT * FROM pending_orders WHERE id = :id")
    suspend fun getPendingOrderById(id: Int): PendingOrder?

    @Delete
    suspend fun deletePendingOrder(order: PendingOrder)
}
