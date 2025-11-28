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

    @Query("SELECT * FROM tugas")
    fun getAllTugas(): Flow<List<TugasEntity>>

    @Query("SELECT * FROM tugas WHERE isSynced = 0")
    suspend fun getUnsyncedTugas(): List<TugasEntity>

    @Query("SELECT * FROM tugas WHERE idTeknisi = :teknisiId")
    fun getTugasByTeknisi(teknisiId: String): Flow<List<TugasEntity>>

    @Query("SELECT * FROM tugas WHERE idTeknisi = :teknisiId")
    suspend fun getTugasByTeknisiSuspend(teknisiId: String): List<TugasEntity>

    @Query("SELECT * FROM tugas WHERE idAlat = :alatId")
    fun getTugasByAlat(alatId: String): Flow<List<TugasEntity>>

    @Query("SELECT * FROM tugas WHERE id = :id")
    suspend fun getTugasById(id: String): TugasEntity?

    @Query("UPDATE tugas SET status = :status, isSynced = 0 WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE tugas SET status = 'Done', buktiFoto = :buktiPath, kondisiAkhir = :kondisiAkhir, isSynced = 0 WHERE id = :id")
    suspend fun completeTugas(id: String, buktiPath: String, kondisiAkhir: String)

    @Query("UPDATE tugas SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT COUNT(*) FROM tugas")
    suspend fun countAll(): Int

    @Query("SELECT COUNT(*) FROM tugas WHERE status = :status")
    suspend fun countByStatus(status: String): Int

    @Query("SELECT * FROM tugas WHERE tglJatuhTempo BETWEEN :startDate AND :endDate")
    suspend fun getTasksByDateRange(startDate: Long, endDate: Long): List<TugasEntity>
}
