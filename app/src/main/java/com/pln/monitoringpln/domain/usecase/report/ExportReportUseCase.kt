package com.pln.monitoringpln.domain.usecase.report

import com.pln.monitoringpln.domain.model.ExportFormat
import com.pln.monitoringpln.domain.repository.ReportRepository
import java.util.Date

class ExportReportUseCase(private val repository: ReportRepository) {

    suspend operator fun invoke(
        startDate: Date,
        endDate: Date,
        format: ExportFormat
    ): Result<String> {
        // Validasi: Start Date tidak boleh lebih besar dari End Date
        if (startDate.after(endDate)) {
            return Result.failure(IllegalArgumentException("Tanggal mulai tidak boleh setelah tanggal akhir."))
        }

        return repository.exportTaskReport(startDate, endDate, format)
    }
}