package com.example.elzatta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ShiftSessionDao {
    @Insert
    suspend fun insertShift(shift: ShiftSession)

    @Query("SELECT * FROM shift_sessions ORDER BY id DESC")
    suspend fun getAllShiftSessions(): List<ShiftSession>
}
