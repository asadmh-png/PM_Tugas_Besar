package com.example.elzatta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KasirDao {
    @Insert
    suspend fun insertSession(session: KasirSession)

    @Query("SELECT * FROM kasir_session WHERE isClosed = 0 LIMIT 1")
    suspend fun getActiveSession(): KasirSession?

    @Query("UPDATE kasir_session SET isClosed = 1 WHERE isClosed = 0")
    suspend fun closeActiveSession()
}
