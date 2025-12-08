package com.pln.monitoringpln.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.ExportFormat
import com.pln.monitoringpln.domain.usecase.report.ExportFullReportUseCase
import com.pln.monitoringpln.domain.usecase.report.ExportReportUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class ReportState(
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val format: ExportFormat = ExportFormat.PDF,
    val isFullReport: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

class ReportViewModel(
    private val exportReportUseCase: ExportReportUseCase,
    private val exportFullReportUseCase: ExportFullReportUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    fun onStartDateChange(date: Date) {
        _state.update { it.copy(startDate = date) }
    }

    fun onEndDateChange(date: Date) {
        _state.update { it.copy(endDate = date) }
    }

    fun onFormatChange(format: ExportFormat) {
        _state.update { it.copy(format = format) }
    }

    fun onFullReportChange(isFull: Boolean) {
        _state.update { it.copy(isFullReport = isFull) }
    }

    fun onExport() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, successMessage = null, errorMessage = null) }

            val result = if (_state.value.isFullReport) {
                exportFullReportUseCase(_state.value.format)
            } else {
                exportReportUseCase(
                    startDate = _state.value.startDate,
                    endDate = _state.value.endDate,
                    format = _state.value.format,
                )
            }

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Laporan berhasil disimpan di: ${result.getOrNull()}",
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Gagal membuat laporan",
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(successMessage = null, errorMessage = null) }
    }
}
