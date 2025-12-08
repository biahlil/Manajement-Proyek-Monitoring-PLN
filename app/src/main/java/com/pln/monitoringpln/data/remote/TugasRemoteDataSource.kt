package com.pln.monitoringpln.data.remote

import com.pln.monitoringpln.data.model.TugasDto
import com.pln.monitoringpln.data.model.TugasInsertDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

class TugasRemoteDataSource(
    private val supabaseClient: SupabaseClient,
) {
    suspend fun fetchAllTugas(): Result<List<TugasDto>> {
        return try {
            val result = supabaseClient.postgrest["tugas"]
                .select()
                .decodeList<TugasDto>()
            Result.success(result)
        } catch (e: Exception) {
            android.util.Log.e("TugasRemoteDataSource", "fetchAllTugas failed", e)
            Result.failure(e)
        }
    }

    suspend fun fetchTugasByTeknisi(teknisiId: String): Result<List<TugasDto>> {
        return try {
            val result = supabaseClient.postgrest["tugas"]
                .select {
                    filter {
                        eq("teknisi_id", teknisiId)
                    }
                }
                .decodeList<TugasDto>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertTugas(dto: TugasInsertDto, upsert: Boolean = false): Result<Unit> {
        return try {
            if (upsert) {
                supabaseClient.postgrest["tugas"].upsert(dto)
            } else {
                supabaseClient.postgrest["tugas"].insert(dto)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTugasStatus(id: String, status: String): Result<Unit> {
        return try {
            supabaseClient.postgrest["tugas"].update(
                mapOf("status" to status),
            ) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeTaskRemote(id: String, status: String, proofUrl: String, condition: String): Result<Unit> {
        return try {
            supabaseClient.postgrest["tugas"].update(
                mapOf(
                    "status" to status,
                    "bukti_foto" to proofUrl,
                    "kondisi_akhir" to condition,
                ),
            ) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadTaskProof(taskId: String, photoBytes: ByteArray): Result<String> {
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

    suspend fun fetchTasksByAlat(alatId: String): Result<List<TugasDto>> {
        return try {
            val result = supabaseClient.postgrest["tugas"]
                .select {
                    filter {
                        eq("alat_id", alatId)
                    }
                }
                .decodeList<TugasDto>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTaskRemote(id: String): Result<Unit> {
        return try {
            supabaseClient.postgrest["tugas"].delete {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTugas(id: String, dto: TugasInsertDto): Result<Unit> {
        return try {
            supabaseClient.postgrest["tugas"].update(dto) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
