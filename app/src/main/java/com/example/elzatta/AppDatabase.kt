package com.example.elzatta

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [KasirSession::class, User::class, Product::class, PendingOrder::class, SaleTransaction::class, TransactionItem::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kasirDao(): KasirDao
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun pendingOrderDao(): PendingOrderDao
    abstract fun transactionDao(): TransactionDao

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
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val productDao = database.productDao()
                                productDao.insertProduct(Product("8881", "Hijab Segi Empat Polos", 45000, 50))
                                productDao.insertProduct(Product("899123456000", "Gamis Elzatta Premium", 350000, 15))
                                productDao.insertProduct(Product("899123456001", "Tunik Elzatta Pink", 150000, 12))
                                productDao.insertProduct(Product("899123456002", "Scarf Motif Bunga", 75000, 45))
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
