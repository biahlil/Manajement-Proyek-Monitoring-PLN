package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import com.pln.monitoringpln.domain.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetTaskDetailUseCase(
    private val tugasRepository: TugasRepository,
    private val alatRepository: AlatRepository,
    private val userRepository: UserRepository,
) {
    data class TaskDetail(
        val task: Tugas,
        val equipment: Alat?,
        val technician: User?,
    )

    suspend operator fun invoke(taskId: String): Result<TaskDetail> = coroutineScope {
        try {
            val taskResult = tugasRepository.getTaskDetail(taskId)
            if (taskResult.isFailure) {
                return@coroutineScope Result.failure(taskResult.exceptionOrNull() ?: Exception("Tugas tidak ditemukan"))
            }
            val task = taskResult.getOrNull()!!

            val equipmentDeferred = async { alatRepository.getAlatDetail(task.idAlat) }
            val technicianDeferred = async { userRepository.getTeknisiDetail(task.idTeknisi) }

            val equipmentResult = equipmentDeferred.await()
            val technicianResult = technicianDeferred.await()

            Result.success(
                TaskDetail(
                    task = task,
                    equipment = equipmentResult.getOrNull(),
                    technician = technicianResult.getOrNull(),
                ),
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
