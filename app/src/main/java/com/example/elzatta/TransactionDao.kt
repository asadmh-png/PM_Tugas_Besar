package com.example.elzatta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: SaleTransaction)

    @Insert
    suspend fun insertTransactionItems(items: List<TransactionItem>)

    @Query("SELECT * FROM sale_transactions WHERE nomorNota = :nota LIMIT 1")
    suspend fun getTransactionByNota(nota: String): SaleTransaction?

    @Query("SELECT * FROM transaction_items WHERE nomorNota = :nota")
    suspend fun getItemsByNota(nota: String): List<TransactionItem>

    @Update
    suspend fun updateTransaction(transaction: SaleTransaction)

    @androidx.room.Transaction
    suspend fun insertFullTransaction(tx: SaleTransaction, items: List<TransactionItem>) {
        insertTransaction(tx)
        insertTransactionItems(items)
    }

    @Query("SELECT SUM(totalHarga) FROM sale_transactions WHERE statusTransaksi = 'Lunas'")
    suspend fun getTotalPendapatanShift(): Int?
}
