package com.pln.monitoringpln.presentation.equipment.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AlatRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditEquipmentViewModel(
    private val alatRepository: AlatRepository
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
                        status = equipment.kondisi,
                        lokasi = equipment.locationName ?: "",
                        latitude = equipment.latitude,
                        longitude = equipment.longitude
                    ) 
                }
            } else {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Alat tidak ditemukan: ${result.exceptionOrNull()?.message}" 
                    ) 
                }
            }
        }
    }

    fun onNamaChange(value: String) {
        _state.update { it.copy(namaAlat = value) }
    }

    fun onKodeChange(value: String) {
        _state.update { it.copy(kodeAlat = value) }
    }

    fun onTipeChange(value: String) {
        _state.update { it.copy(tipePeralatan = value) }
    }

    fun onStatusChange(value: String) {
        _state.update { it.copy(status = value) }
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
                            val locationName = "${address.locality ?: ""}, ${address.subAdminArea ?: ""}".trim(',').trim()
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
            if (currentState.namaAlat.isBlank() || currentState.kodeAlat.isBlank()) {
                _state.update { it.copy(error = "Nama dan Kode Alat wajib diisi") }
                return@launch
            }

            _state.update { it.copy(isSaving = true, error = null) }
            
            val id = currentState.equipmentId ?: UUID.randomUUID().toString()
            
            val alat = Alat(
                id = id,
                namaAlat = currentState.namaAlat,
                kodeAlat = currentState.kodeAlat,
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                kondisi = currentState.status,
                tipe = currentState.tipePeralatan,
                status = "ACTIVE",
                isArchived = false,
                locationName = currentState.lokasi.ifBlank { null }
            )
            
            val result = alatRepository.insertAlat(alat)
            
            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSaved = true) }
            } else {
                _state.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Gagal menyimpan: ${result.exceptionOrNull()?.message}" 
                    ) 
                }
            }
        }
    }
}
