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
    // UC1a: Get Detail (Include Archived)
    @Query("SELECT * FROM alat WHERE id = :id")
    suspend fun getAlatDetail(id: String): AlatEntity?

    // UC1b: Soft Delete (Archive)
    @Query("UPDATE alat SET isArchived = 1, status = 'ARCHIVED' WHERE id = :id")
    suspend fun archiveAlat(id: String)

    // Helper for testing/admin: Get All Active
    @Query("SELECT * FROM alat WHERE isArchived = 0")
    suspend fun getAllActiveAlat(): List<AlatEntity>

    @Query("SELECT * FROM alat")
    fun getAllAlat(): kotlinx.coroutines.flow.Flow<List<AlatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alatList: List<AlatEntity>)

    @androidx.room.Update
    suspend fun updateAlat(alat: AlatEntity)

    @Query("SELECT * FROM alat WHERE kodeAlat = :kode")
    suspend fun getAlatByKode(kode: String): AlatEntity?

    @Query("SELECT * FROM alat WHERE isSynced = 0")
    suspend fun getUnsyncedAlat(): List<AlatEntity>

    @Query("UPDATE alat SET isSynced = :isSynced WHERE id = :id")
    suspend fun updateSyncStatus(id: String, isSynced: Boolean)

    @Query("SELECT COUNT(*) FROM alat WHERE isArchived = 0")
    suspend fun countAll(): Int
}
