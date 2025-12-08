package com.pln.monitoringpln.domain.usecase.report

import com.pln.monitoringpln.domain.model.ExportFormat
import com.pln.monitoringpln.domain.repository.ReportRepository

class ExportFullReportUseCase(private val repository: ReportRepository) {

    suspend operator fun invoke(format: ExportFormat): Result<String> {
        return repository.exportFullDatabaseReport(format)
    }
}
