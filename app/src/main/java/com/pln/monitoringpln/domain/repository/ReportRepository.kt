package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.ExportFormat
import java.util.Date

interface ReportRepository {
    suspend fun exportTaskReport(
        startDate: Date,
        endDate: Date,
        format: ExportFormat,
    ): Result<String>
}
