package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.model.DashboardSummary
import com.pln.monitoringpln.domain.repository.DashboardRepository

class DashboardRepositoryImpl(
    private val alatDao: com.pln.monitoringpln.data.local.dao.AlatDao,
    private val tugasDao: com.pln.monitoringpln.data.local.dao.TugasDao,
) : DashboardRepository {

    override fun getDashboardSummary(technicianId: String?): kotlinx.coroutines.flow.Flow<DashboardSummary> {
        if (technicianId == null) {
            // Admin Logic (Existing)
            val alatFlow = kotlinx.coroutines.flow.combine(
                alatDao.observeCountAll(),
                alatDao.observeCountByStatus("Normal"),
                alatDao.observeCountByStatus("Perlu Perhatian"),
                alatDao.observeCountByStatus("Rusak"),
            ) { total, normal, warning, broken ->
                Quad(total, normal, warning, broken)
            }

            val tugasFlow = kotlinx.coroutines.flow.combine(
                tugasDao.observeCountAll(),
                tugasDao.observeCountByStatus("To Do"),
                tugasDao.observeCountByStatus("In Progress"),
                tugasDao.observeCountByStatus("Done"),
            ) { total, todo, progress, done ->
                Quad(total, todo, progress, done)
            }

            return kotlinx.coroutines.flow.combine(alatFlow, tugasFlow) { alat, tugas ->
                DashboardSummary(
                    totalAlat = alat.first,
                    totalAlatNormal = alat.second,
                    totalAlatPerluPerhatian = alat.third,
                    totalAlatRusak = alat.fourth,
                    totalTeknisi = 0,
                    totalTugas = tugas.first,
                    tugasToDo = tugas.second,
                    tugasInProgress = tugas.third,
                    tugasDone = tugas.fourth,
                )
            }
        } else {
            // Technician Logic
            // Total Alat = Alat yang pernah dikerjakan (via tugas)
            val totalAlatFlow = tugasDao.observeDistinctEquipmentCountByTechnician(technicianId)

            // Tugas Stats specific to technician
            val tugasFlow = kotlinx.coroutines.flow.combine(
                tugasDao.observeCountByTechnician(technicianId),
                tugasDao.observeCountByStatusAndTechnician("To Do", technicianId),
                tugasDao.observeCountByStatusAndTechnician("In Progress", technicianId),
                tugasDao.observeCountByStatusAndTechnician("Done", technicianId),
            ) { total, todo, progress, done ->
                Quad(total, todo, progress, done)
            }

            return kotlinx.coroutines.flow.combine(totalAlatFlow, tugasFlow) { totalAlat, tugas ->
                DashboardSummary(
                    totalAlat = totalAlat,
                    totalAlatNormal = 0, // Not shown for technician
                    totalAlatPerluPerhatian = 0,
                    totalAlatRusak = 0,
                    totalTeknisi = 0,
                    totalTugas = tugas.first,
                    tugasToDo = tugas.second,
                    tugasInProgress = tugas.third,
                    tugasDone = tugas.fourth,
                )
            }
        }
    }

    data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
