package com.pln.monitoringpln.data.local.datasource

import com.pln.monitoringpln.data.local.dao.TugasDao
import com.pln.monitoringpln.data.local.entity.TugasEntity
import kotlinx.coroutines.flow.Flow

class TugasLocalDataSource(
    private val tugasDao: TugasDao
) {
    fun getAllTugas(): Flow<List<TugasEntity>> = tugasDao.getAllTugas()

    fun getTugasByTeknisi(teknisiId: String): Flow<List<TugasEntity>> = tugasDao.getTugasByTeknisi(teknisiId)

    suspend fun getTugasByTeknisiSuspend(teknisiId: String): List<TugasEntity> = tugasDao.getTugasByTeknisiSuspend(teknisiId)

    fun getTugasByAlat(alatId: String): Flow<List<TugasEntity>> = tugasDao.getTugasByAlat(alatId)

    suspend fun getTugasById(id: String): TugasEntity? = tugasDao.getTugasById(id)

    suspend fun insertTugas(tugas: TugasEntity) = tugasDao.insertTugas(tugas)

    suspend fun insertAll(tugasList: List<TugasEntity>) = tugasDao.insertAll(tugasList)

    suspend fun updateTugas(tugas: TugasEntity) = tugasDao.updateTugas(tugas)

    suspend fun getUnsyncedTugas(): List<TugasEntity> = tugasDao.getUnsyncedTugas()

    suspend fun deleteTask(id: String) = tugasDao.deleteTask(id)
}
