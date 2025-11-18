package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AlatRepository
import java.lang.IllegalArgumentException

class GetAlatDetailUseCase(private val repository: AlatRepository) {

    suspend operator fun invoke(id: String): Result<Alat> {
        if (id.isBlank()) {
            return Result.failure(IllegalArgumentException("ID Alat tidak boleh kosong"))
        }
        return repository.getAlatDetail(id)
    }
}
