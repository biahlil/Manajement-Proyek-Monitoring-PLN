package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.data.model.TugasDto
import com.pln.monitoringpln.data.model.toDomain
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.TugasRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

import com.pln.monitoringpln.data.model.TugasUpdateStatusDto
import com.pln.monitoringpln.data.model.toDto
import io.github.jan.supabase.postgrest.from

class TugasRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : TugasRepository {

    override suspend fun createTask(tugas: Tugas): Tugas? {
        return try {
            val tugasDto = tugas.toDto()

            // 2. INSERT the DTO
            val result = supabaseClient.from("tugas")
                .insert(tugasDto) { // <--- Send DTO here
                    select()
                }
                .decodeSingleOrNull<TugasDto>()

            result?.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getTasksByTeknisi(idTeknisi: String, searchQuery: String?): Result<List<Tugas>> {
        return try {
            val result = supabaseClient.postgrest["tugas"]
                .select {
                    filter {
                        eq("teknisi_id", idTeknisi)
                        if (!searchQuery.isNullOrBlank()) {
                            ilike("title", "%$searchQuery%")
                        }
                    }
                }.decodeList<TugasDto>()
            
            Result.success(result.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTaskStatus(taskId: String, newStatus: String): Result<Unit> {
        return try {
            val updateData = TugasUpdateStatusDto(status = newStatus)
            supabaseClient.postgrest["tugas"].update(updateData) {
                filter {
                    eq("id", taskId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTaskDetail(taskId: String): Result<Tugas> {
        return try {
            val dto = supabaseClient.postgrest["tugas"]
                .select {
                    filter {
                        eq("id", taskId)
                    }
                }.decodeSingle<TugasDto>()
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadTaskProof(taskId: String, photoBytes: ByteArray): Result<String> {
        return try {
            val bucket = supabaseClient.storage.from("task-proofs")
            val fileName = "$taskId-${System.currentTimeMillis()}.jpg"
            bucket.upload(fileName, photoBytes)
            val publicUrl = bucket.publicUrl(fileName)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTasksByAlat(idAlat: String): Result<List<Tugas>> {
        return try {
            val result = supabaseClient.postgrest["tugas"]
                .select {
                    filter {
                        eq("alat_id", idAlat)
                    }
                }.decodeList<TugasDto>()
            Result.success(result.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
