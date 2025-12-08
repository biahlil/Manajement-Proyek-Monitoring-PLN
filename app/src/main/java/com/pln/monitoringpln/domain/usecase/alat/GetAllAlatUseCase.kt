package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AlatRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlatUseCase(
    private val alatRepository: AlatRepository,
) {
    operator fun invoke(): Flow<List<Alat>> {
        return alatRepository.getAllAlat()
    }
}
