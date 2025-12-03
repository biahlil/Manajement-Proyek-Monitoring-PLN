package com.pln.monitoringpln.data.local.datasource

import com.pln.monitoringpln.data.local.dao.AlatDao
import com.pln.monitoringpln.data.local.entity.AlatEntity
import kotlinx.coroutines.flow.Flow

class AlatLocalDataSource(private val alatDao: AlatDao) {

    fun getAllAlat(): Flow<List<AlatEntity>> = alatDao.getAllAlat()

    suspend fun insertAlat(alat: AlatEntity) = alatDao.insertAlat(alat)
    
    suspend fun insertAll(list: List<AlatEntity>) = alatDao.insertAll(list)

    suspend fun getAlatDetail(id: String): AlatEntity? = alatDao.getAlatDetail(id)

    fun observeAlatDetail(id: String): Flow<AlatEntity?> = alatDao.observeAlatDetail(id)

    suspend fun getAlatByKode(kode: String): AlatEntity? = alatDao.getAlatByKode(kode)

    suspend fun updateAlat(alat: AlatEntity) = alatDao.updateAlat(alat)
    
    suspend fun archiveAlat(id: String) = alatDao.archiveAlat(id)

    suspend fun getUnsyncedAlat(): List<AlatEntity> = alatDao.getUnsyncedAlat()
}
