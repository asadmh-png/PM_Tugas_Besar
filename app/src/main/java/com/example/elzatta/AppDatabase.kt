package com.example.elzatta

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [KasirSession::class, User::class, Product::class, PendingOrder::class, SaleTransaction::class, TransactionItem::class, ShiftSession::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kasirDao(): KasirDao
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun pendingOrderDao(): PendingOrderDao
    abstract fun transactionDao(): TransactionDao
    abstract fun shiftSessionDao(): ShiftSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "elzatta_bpos_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Dipanggil saat DB dibuat pertama kali
                        INSTANCE?.let { database ->
                            populateDatabase(database)
                        }
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        // Dipanggil setelah database di-reset karena migrasi
                        INSTANCE?.let { database ->
                            populateDatabase(database)
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private fun populateDatabase(database: AppDatabase) {
            CoroutineScope(Dispatchers.IO).launch {
                val productDao = database.productDao()
                // Cek apakah data sudah ada untuk menghindari duplikasi
                val existing = productDao.getAllProducts()
                if (existing.isEmpty()) {
                    productDao.insertProduct(Product("8881", "Hijab Segi Empat Polos", 45000, 50))
                    productDao.insertProduct(Product("899123456000", "Gamis Elzatta Premium", 350000, 15))
                    productDao.insertProduct(Product("899123456001", "Tunik Elzatta Pink", 150000, 12))
                    productDao.insertProduct(Product("899123456002", "Scarf Motif Bunga", 75000, 45))
                }
            }
        }
    }
}
