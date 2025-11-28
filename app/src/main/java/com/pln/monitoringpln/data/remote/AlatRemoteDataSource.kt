package com.pln.monitoringpln.data.remote

import com.pln.monitoringpln.data.model.AlatDto
import com.pln.monitoringpln.data.model.AlatInsertDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class AlatRemoteDataSource(private val supabaseClient: SupabaseClient) {

    suspend fun fetchAllAlat(): Result<List<AlatDto>> {
        return try {
            val data = supabaseClient.postgrest["alat"]
                .select()
                .decodeList<AlatDto>()
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertAlat(dto: AlatInsertDto, upsert: Boolean = false): Result<Unit> {
        return try {
            if (upsert) {
                supabaseClient.postgrest["alat"].upsert(dto)
            } else {
                supabaseClient.postgrest["alat"].insert(dto)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlatById(id: String): Result<AlatDto> {
        return try {
            val data = supabaseClient.postgrest["alat"]
                .select {
                    filter { eq("id", id) }
                }.decodeSingle<AlatDto>()
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlatByKode(kode: String): Result<AlatDto> {
        return try {
            val data = supabaseClient.postgrest["alat"]
                .select {
                    filter { eq("kode_alat", kode) }
                }.decodeSingle<AlatDto>()
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAlat(id: String, updateData: Map<String, Any?>): Result<Unit> {
        return try {
            supabaseClient.postgrest["alat"].update(updateData) {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
