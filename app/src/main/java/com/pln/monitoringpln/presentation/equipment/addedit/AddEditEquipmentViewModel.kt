package com.pln.monitoringpln.presentation.equipment.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AlatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditEquipmentViewModel(
    private val alatRepository: AlatRepository, // Keep for getting detail
    private val addAlatUseCase: com.pln.monitoringpln.domain.usecase.alat.AddAlatUseCase,
    private val updateAlatInfoUseCase: com.pln.monitoringpln.domain.usecase.alat.UpdateAlatInfoUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditEquipmentState())
    val state: StateFlow<AddEditEquipmentState> = _state.asStateFlow()

    fun loadEquipment(id: String?) {
        if (id == null) {
            _state.update { it.copy(isEditMode = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isEditMode = true, equipmentId = id) }

            val result = alatRepository.getAlatDetail(id)

            if (result.isSuccess) {
                val equipment = result.getOrThrow()
                _state.update {
                    it.copy(
                        isLoading = false,
                        namaAlat = equipment.namaAlat,
                        kodeAlat = equipment.kodeAlat,
                        tipePeralatan = equipment.tipe,
                        status = equipment.status, // Health
                        description = equipment.kondisi, // Description
                        lokasi = equipment.locationName ?: "",
                        latitude = equipment.latitude,
                        longitude = equipment.longitude,
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Alat tidak ditemukan: ${result.exceptionOrNull()?.message}",
                    )
                }
            }
        }
    }

    fun onNamaChange(value: String) {
        _state.update { it.copy(namaAlat = value, namaAlatError = null) }
    }

    // onKodeChange removed (Auto-generated)

    fun onTipeChange(value: String) {
        _state.update { it.copy(tipePeralatan = value, tipeError = null) }
    }

    fun onStatusChange(value: String) {
        _state.update { it.copy(status = value) }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onLokasiChange(value: String) {
        _state.update { it.copy(lokasi = value) }
    }

    fun onLatitudeChange(value: String) {
        val doubleValue = value.toDoubleOrNull() ?: 0.0
        _state.update { it.copy(latitude = doubleValue) }
    }

    fun onLongitudeChange(value: String) {
        val doubleValue = value.toDoubleOrNull() ?: 0.0
        _state.update { it.copy(longitude = doubleValue) }
    }

    fun updateLocationName(context: android.content.Context, lat: Double, lon: Double) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lon, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val locationName =
                                "${address.locality ?: ""}, ${address.subAdminArea ?: ""}".trim(',').trim()
                            _state.update { it.copy(lokasi = locationName) }
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val locationName = "${address.locality ?: ""}, ${address.subAdminArea ?: ""}".trim(',').trim()
                        _state.update { it.copy(lokasi = locationName) }
                    }
                }
            } catch (e: Exception) {
                // Ignore geocoding errors
            }
        }
    }

    fun onSaveEquipment() {
        viewModelScope.launch {
            val currentState = _state.value

            // Basic Validation
            if (currentState.namaAlat.isBlank()) {
                _state.update { it.copy(error = "Nama Alat wajib diisi") }
                return@launch
            }

            _state.update { it.copy(isSaving = true, error = null) }

            val id = currentState.equipmentId ?: UUID.randomUUID().toString()

            val result = if (currentState.isEditMode && currentState.equipmentId != null) {
                updateAlatInfoUseCase(
                    id = currentState.equipmentId,
                    namaAlat = currentState.namaAlat,
                    kodeAlat = currentState.kodeAlat, // Use existing code
                    latitude = currentState.latitude,
                    longitude = currentState.longitude,
                    locationName = currentState.lokasi.ifBlank { null },
                    tipe = currentState.tipePeralatan,
                    status = currentState.status, // Health
                    kondisi = currentState.description, // Description
                )
            } else {
                // Auto-generate Kode Alat
                val randomNum = (0..1000).random()
                val safeName = currentState.namaAlat.replace(" ", "-").uppercase()
                val generatedKode = "$safeName-$randomNum"

                addAlatUseCase(
                    id = id,
                    namaAlat = currentState.namaAlat,
                    kodeAlat = generatedKode,
                    latitude = currentState.latitude,
                    longitude = currentState.longitude,
                    locationName = currentState.lokasi.ifBlank { null },
                    tipe = currentState.tipePeralatan,
                    status = currentState.status, // Health
                    kondisi = currentState.description, // Description
                )
            }

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSaved = true, savedCondition = currentState.status) }
            } else {
                val exception = result.exceptionOrNull()
                if (exception is IllegalArgumentException) {
                    when {
                        exception.message?.contains("Nama alat") == true ->
                            _state.update { it.copy(isSaving = false, namaAlatError = exception.message) }

                        exception.message?.contains("Tipe alat") == true ->
                            _state.update { it.copy(isSaving = false, tipeError = exception.message) }

                        else ->
                            _state.update { it.copy(isSaving = false, error = exception.message) }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = "Gagal menyimpan: ${exception?.message}",
                        )
                    }
                }
            }
        }
    }
}
