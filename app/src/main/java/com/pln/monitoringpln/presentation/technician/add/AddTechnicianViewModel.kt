package com.pln.monitoringpln.presentation.technician.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddTechnicianViewModel(
    private val addTeknisiUseCase: com.pln.monitoringpln.domain.usecase.user.AddTeknisiUseCase,
    private val uploadPhotoUseCase: com.pln.monitoringpln.domain.usecase.storage.UploadPhotoUseCase,
    private val context: android.content.Context, // Need context for ContentResolver
) : ViewModel() {

    private val _state = MutableStateFlow(AddTechnicianState())
    val state: StateFlow<AddTechnicianState> = _state.asStateFlow()

    fun onNamaChange(value: String) {
        _state.update { it.copy(namaLengkap = value, namaError = null) }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value) }
    }

    fun onPhotoSelected(uri: android.net.Uri?) {
        _state.update { it.copy(photoUri = uri) }
    }

    fun onSaveTechnician() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val currentState = _state.value

            // Validation
            if (currentState.namaLengkap.isBlank() || currentState.email.isBlank() || currentState.password.isBlank()) {
                _state.update { it.copy(isSaving = false, error = "Semua field harus diisi") }
                return@launch
            }

            var photoUrl: String? = null

            // Upload Photo if exists
            if (currentState.photoUri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(currentState.photoUri)
                    val byteArray = inputStream?.readBytes()
                    inputStream?.close()

                    if (byteArray != null) {
                        val fileName = "technician_${System.currentTimeMillis()}.jpg"
                        val uploadResult = uploadPhotoUseCase(byteArray, fileName)

                        uploadResult.fold(
                            onSuccess = { url -> photoUrl = url },
                            onFailure = { error ->
                                _state.update {
                                    it.copy(
                                        isSaving = false,
                                        error = "Gagal upload foto: ${error.message}",
                                    )
                                }
                                return@launch
                            },
                        )
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isSaving = false, error = "Gagal memproses foto: ${e.message}") }
                    return@launch
                }
            }

            val result = addTeknisiUseCase(
                email = currentState.email,
                password = currentState.password,
                namaLengkap = currentState.namaLengkap,
                photoUrl = photoUrl,
            )

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false, isSaved = true) }
                },
                onFailure = { error ->
                    val exception = error
                    if (exception is IllegalArgumentException) {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                namaError = if (exception.message?.contains("Nama") == true) exception.message else null,
                                emailError = if (exception.message?.contains("Email") == true) exception.message else null,
                                error = if (exception.message?.contains("Nama") == false && exception.message?.contains(
                                        "Email",
                                    ) == false
                                ) {
                                    exception.message
                                } else {
                                    null
                                },
                            )
                        }
                    } else {
                        _state.update { it.copy(isSaving = false, error = error.message ?: "Gagal menyimpan teknisi") }
                    }
                },
            )
        }
    }
}
