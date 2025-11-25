package com.pln.monitoringpln.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pln.monitoringpln.data.local.entity.AlatEntity

@Dao
interface AlatDao {
    // UC1a: Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlat(alat: AlatEntity)

    // UC1a: Get Detail (Only Active)
    @Query("SELECT * FROM alat WHERE id = :id AND isArchived = 0")
    suspend fun getAlatDetail(id: String): AlatEntity?

    // UC1b: Soft Delete (Archive)
    @Query("UPDATE alat SET isArchived = 1, status = 'ARCHIVED' WHERE id = :id")
    suspend fun archiveAlat(id: String)

    // Helper for testing/admin: Get All Active
    @Query("SELECT * FROM alat WHERE isArchived = 0")
    suspend fun getAllActiveAlat(): List<AlatEntity>
}
