package com.example.elzatta

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [KasirSession::class, User::class, Product::class, PendingOrder::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kasirDao(): KasirDao
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun pendingOrderDao(): PendingOrderDao

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
                .fallbackToDestructiveMigration() // Penting saat tahap pengembangan untuk reset DB jika skema berubah
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
