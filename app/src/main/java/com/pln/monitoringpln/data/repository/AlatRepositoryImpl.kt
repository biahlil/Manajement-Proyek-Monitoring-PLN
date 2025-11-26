package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.data.model.AlatDto
import com.pln.monitoringpln.data.model.AlatUpdateInfoDto
import com.pln.monitoringpln.data.model.AlatArchiveDto
import com.pln.monitoringpln.data.model.AlatConditionDto
import com.pln.monitoringpln.data.model.toDomain
import com.pln.monitoringpln.data.model.toInsertDto
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AlatRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class AlatRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : AlatRepository {

    override suspend fun insertAlat(alat: Alat): Result<Unit> {
        return try {
            val dto = alat.toInsertDto()
            supabaseClient.postgrest["alat"].insert(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAlatDetail(id: String): Result<Alat> {
        return try {
            val dto = supabaseClient.postgrest["alat"]
                .select {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<AlatDto>()
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAlatByKode(kode: String): Result<Alat> {
        return try {
            val dto = supabaseClient.postgrest["alat"]
                .select {
                    filter {
                        eq("kode_alat", kode)
                    }
                }.decodeSingle<AlatDto>()
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAlatInfo(
        id: String,
        nama: String,
        kode: String,
        lat: Double,
        lng: Double
    ): Result<Unit> {
        return try {
            val updateData = AlatUpdateInfoDto(
                namaAlat = nama,
                kodeAlat = kode,
                latitude = lat,
                longitude = lng
            )
            supabaseClient.postgrest["alat"].update(updateData) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun archiveAlat(id: String): Result<Unit> {
        return try {
            val updateData = AlatArchiveDto(
                status = "ARCHIVED",
                isArchived = true
            )
            supabaseClient.postgrest["alat"].update(updateData) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAlatCondition(id: String, kondisi: String): Result<Unit> {
        return try {
            val updateData = AlatConditionDto(
                kondisi = kondisi
            )
            supabaseClient.postgrest["alat"].update(updateData) {
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
