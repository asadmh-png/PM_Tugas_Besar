package com.example.elzatta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    @Query("UPDATE products SET stok = stok - :qty WHERE barcode = :barcode")
    suspend fun updateStok(barcode: String, qty: Int)

    @Query("UPDATE products SET stok = stok + :qty WHERE barcode = :barcode")
    suspend fun tambahStok(barcode: String, qty: Int)

    @Query("UPDATE products SET harga_promo = 0")
    suspend fun clearAllPromos()

    @Query("UPDATE products SET harga_promo = :harga WHERE barcode = :barcode")
    suspend fun setPromoPrice(barcode: String, harga: Int)

    @androidx.room.Transaction
    suspend fun updatePromoSimulation() {
        setPromoPrice("8881", 30000)
        setPromoPrice("8882", 10000)
    }
}
