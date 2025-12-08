package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.data.local.datasource.TugasLocalDataSource
import com.pln.monitoringpln.data.mapper.toDomain
import com.pln.monitoringpln.data.mapper.toEntity
import com.pln.monitoringpln.data.mapper.toInsertDto
import com.pln.monitoringpln.data.remote.TugasRemoteDataSource
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.TugasRepository
import kotlinx.coroutines.flow.first

class TugasRepositoryImpl(
    private val localDataSource: TugasLocalDataSource,
    private val remoteDataSource: TugasRemoteDataSource,
) : TugasRepository {

    override suspend fun createTask(tugas: Tugas): Result<Tugas?> {
        return try {
            // 1. Insert Local (Unsynced)
            localDataSource.insertTugas(tugas.toEntity(isSynced = false))

            // 2. Try Push to Remote
            val remoteResult = remoteDataSource.insertTugas(tugas.toInsertDto())

            if (remoteResult.isSuccess) {
                // Mark as Synced
                localDataSource.insertTugas(tugas.toEntity(isSynced = true))
            }

            Result.success(tugas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTasksByTeknisi(idTeknisi: String, searchQuery: String?): Result<List<Tugas>> {
        return try {
            // 1. Fetch from Local
            val localEntities = localDataSource.getTugasByTeknisiSuspend(idTeknisi)
            val domainList = localEntities.map { it.toDomain() }

            // 2. Filter by Search Query if needed (Universal Search)
            val filteredList = if (!searchQuery.isNullOrBlank()) {
                domainList.filter { tugas ->
                    tugas.deskripsi.contains(searchQuery, ignoreCase = true) ||
                        tugas.status.contains(searchQuery, ignoreCase = true) ||
                        tugas.idAlat.contains(searchQuery, ignoreCase = true)
                }
            } else {
                domainList
            }

            Result.success(filteredList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTaskStatus(taskId: String, newStatus: String): Result<Unit> {
        return try {
            // 1. Update Local
            val existing = localDataSource.getTugasById(taskId) ?: return Result.failure(Exception("Tugas not found"))
            val updated = existing.copy(status = newStatus, isSynced = false)
            localDataSource.updateTugas(updated)

            // 2. Try Push to Remote
            val remoteResult = remoteDataSource.updateTugasStatus(taskId, newStatus)
            if (remoteResult.isSuccess) {
                localDataSource.updateTugas(updated.copy(isSynced = true))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeTugas(id: String, buktiFotoPath: String, kondisiAkhir: String): Result<Unit> {
        // TODO: Implement Complete Tugas with Image Upload
        // For now, just update status
        return updateTaskStatus(id, "Done")
    }

    override suspend fun sync(): Result<Unit> {
        return try {
            // 1. Push Unsynced
            val unsynced = localDataSource.getUnsyncedTugas()
            unsynced.forEach { entity ->
                // Try Insert first (if new)
                val insertResult = remoteDataSource.insertTugas(entity.toDomain().toInsertDto(), upsert = false)
                if (insertResult.isSuccess) {
                    localDataSource.insertTugas(entity.copy(isSynced = true))
                } else {
                    // If Insert failed, try Update (if existing)
                    // Let's use upsert=true as fallback
                    val upsertResult = remoteDataSource.insertTugas(entity.toDomain().toInsertDto(), upsert = true)
                    if (upsertResult.isSuccess) {
                        localDataSource.insertTugas(entity.copy(isSynced = true))
                    }
                }
            }

            // 2. Pull Latest
            val remoteData = remoteDataSource.fetchAllTugas()
            remoteData.getOrNull()?.let { dtos ->
                localDataSource.insertAll(dtos.map { it.toEntity(isSynced = true) })
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadTaskProof(taskId: String, photoBytes: ByteArray): Result<String> {
        return remoteDataSource.uploadTaskProof(taskId, photoBytes)
    }

    override suspend fun getTasksByAlat(idAlat: String): Result<List<Tugas>> {
        return try {
            val remoteResult = remoteDataSource.fetchTasksByAlat(idAlat)
            if (remoteResult.isSuccess) {
                val dtos = remoteResult.getOrNull() ?: emptyList()
                localDataSource.insertAll(dtos.map { it.toEntity(isSynced = true) })
            }
            // Always return local data (Source of Truth)
            val localData = localDataSource.getTugasByAlat(idAlat).first()
            Result.success(localData.map { it.toDomain() })
        } catch (e: Exception) {
            // Fallback to Local on Exception
            try {
                val localData = localDataSource.getTugasByAlat(idAlat).first()
                Result.success(localData.map { it.toDomain() })
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getTaskDetail(taskId: String): Result<Tugas> {
        return try {
            val local = localDataSource.getTugasById(taskId)
            if (local != null) {
                Result.success(local.toDomain())
            } else {
                Result.failure(Exception("Tugas not found locally"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
