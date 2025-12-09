package com.pln.monitoringpln.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pln.monitoringpln.data.local.entity.TugasEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TugasDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTugas(tugas: TugasEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tugasList: List<TugasEntity>)

    @androidx.room.Update
    suspend fun updateTugas(tugas: TugasEntity)

    @Query("SELECT * FROM tugas WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllTugas(): Flow<List<TugasEntity>>

    @Query("SELECT * FROM tugas WHERE isArchived = 0 ORDER BY updatedAt DESC")
    suspend fun getAllTasksSync(): List<TugasEntity>

    @Query("SELECT * FROM tugas WHERE isSynced = 0")
    suspend fun getUnsyncedTugas(): List<TugasEntity>

    @Query("SELECT * FROM tugas WHERE idTeknisi = :teknisiId AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getTugasByTeknisi(teknisiId: String): Flow<List<TugasEntity>>

    @Query("SELECT * FROM tugas WHERE idTeknisi = :teknisiId AND isArchived = 0 ORDER BY updatedAt DESC")
    suspend fun getTugasByTeknisiSuspend(teknisiId: String): List<TugasEntity>

    @Query("SELECT * FROM tugas WHERE idAlat = :alatId AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getTugasByAlat(alatId: String): Flow<List<TugasEntity>>

    @Query("SELECT * FROM tugas WHERE id = :id")
    suspend fun getTugasById(id: String): TugasEntity?

    @Query("UPDATE tugas SET status = :status, isSynced = 0 WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE tugas SET status = 'Done', buktiFoto = :buktiPath, kondisiAkhir = :kondisiAkhir, isSynced = 0 WHERE id = :id")
    suspend fun completeTugas(id: String, buktiPath: String, kondisiAkhir: String)

    @Query("UPDATE tugas SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT COUNT(*) FROM tugas WHERE isArchived = 0")
    suspend fun countAll(): Int

    @Query("SELECT COUNT(*) FROM tugas WHERE status = :status AND isArchived = 0")
    suspend fun countByStatus(status: String): Int

    @Query("SELECT * FROM tugas WHERE tglJatuhTempo BETWEEN :startDate AND :endDate AND isArchived = 0 ORDER BY updatedAt DESC")
    suspend fun getTasksByDateRange(startDate: Long, endDate: Long): List<TugasEntity>

    @Query("SELECT COUNT(*) FROM tugas WHERE isArchived = 0")
    fun observeCountAll(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tugas WHERE status = :status AND isArchived = 0")
    fun observeCountByStatus(status: String): Flow<Int>

    @Query("DELETE FROM tugas WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("SELECT COUNT(*) FROM tugas WHERE idTeknisi = :teknisiId AND isArchived = 0")
    fun observeCountByTechnician(teknisiId: String): Flow<Int>

    @Query("SELECT COUNT(DISTINCT idAlat) FROM tugas WHERE idTeknisi = :teknisiId AND isArchived = 0")
    fun observeDistinctEquipmentCountByTechnician(teknisiId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM tugas WHERE status = :status AND idTeknisi = :teknisiId AND isArchived = 0")
    fun observeCountByStatusAndTechnician(status: String, teknisiId: String): Flow<Int>
}
