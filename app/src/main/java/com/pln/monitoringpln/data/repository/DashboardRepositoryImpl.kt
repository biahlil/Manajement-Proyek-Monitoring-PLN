package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.model.DashboardSummary
import com.pln.monitoringpln.domain.repository.DashboardRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DashboardRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : DashboardRepository {

    override suspend fun getDashboardSummary(): Result<DashboardSummary> = coroutineScope {
        try {
            val totalAlatDeferred = async {
                supabaseClient.postgrest["alat"].select(head = true) {
                    count(Count.EXACT)
                }.countOrNull() ?: 0
            }

            val totalTeknisiDeferred = async {
                supabaseClient.postgrest["profiles"].select(head = true) {
                    count(Count.EXACT)
                    filter { eq("role", "TEKNISI") }
                }.countOrNull() ?: 0
            }

            val totalTugasDeferred = async {
                supabaseClient.postgrest["tugas"].select(head = true) {
                    count(Count.EXACT)
                }.countOrNull() ?: 0
            }

            val tugasToDoDeferred = async {
                supabaseClient.postgrest["tugas"].select(head = true) {
                    count(Count.EXACT)
                    filter { eq("status", "To Do") }
                }.countOrNull() ?: 0
            }

            val tugasInProgressDeferred = async {
                supabaseClient.postgrest["tugas"].select(head = true) {
                    count(Count.EXACT)
                    filter { eq("status", "In Progress") }
                }.countOrNull() ?: 0
            }

            val tugasDoneDeferred = async {
                supabaseClient.postgrest["tugas"].select(head = true) {
                    count(Count.EXACT)
                    filter { eq("status", "Done") }
                }.countOrNull() ?: 0
            }

            val summary = DashboardSummary(
                totalAlat = totalAlatDeferred.await().toInt(),
                totalTeknisi = totalTeknisiDeferred.await().toInt(),
                totalTugas = totalTugasDeferred.await().toInt(),
                tugasToDo = tugasToDoDeferred.await().toInt(),
                tugasInProgress = tugasInProgressDeferred.await().toInt(),
                tugasDone = tugasDoneDeferred.await().toInt()
            )

            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
