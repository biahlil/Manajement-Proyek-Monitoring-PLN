package com.pln.monitoringpln.presentation.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.repository.UserRepository

class EditProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val updateUserProfileUseCase: com.pln.monitoringpln.domain.usecase.user.UpdateUserProfileUseCase,
    private val updatePasswordUseCase: com.pln.monitoringpln.domain.usecase.auth.UpdatePasswordUseCase,
    private val uploadAvatarUseCase: com.pln.monitoringpln.domain.usecase.user.UploadAvatarUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val userResult = userRepository.getTeknisiDetail(userId)
            val user = userResult.getOrNull()

            if (user != null) {
                _state.update { 
                    it.copy(
                        name = user.namaLengkap,
                        id = user.id,
                        email = user.email,
                        phone = "-", // Placeholder
                        role = user.role,
                        photoUrl = user.photoUrl,
                        isLoading = false
                    ) 
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNameChange(newValue: String) {
        _state.update { it.copy(name = newValue, nameError = null) }
    }

    fun onEmailChange(newValue: String) {
        _state.update { it.copy(email = newValue, emailError = null) }
    }

    fun onPhoneChange(newValue: String) {
        _state.update { it.copy(phone = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _state.update { it.copy(password = newValue, passwordError = null) }
    }

    fun onConfirmPasswordChange(newValue: String) {
        _state.update { it.copy(confirmPassword = newValue, passwordError = null) }
    }

    fun onAvatarSelected(byteArray: ByteArray) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = _state.value.id
            if (userId.isNotEmpty()) {
                val result = uploadAvatarUseCase(userId, byteArray)
                if (result.isSuccess) {
                    val newUrl = result.getOrNull()
                    _state.update { it.copy(isLoading = false, photoUrl = newUrl) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Gagal mengupload foto") }
                }
            }
        }
    }

    fun onSave() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, nameError = null, emailError = null, passwordError = null) }
            
            val currentUserState = _state.value

            // 1. Update Profile
            val currentRole = if (currentUserState.role.isNotEmpty()) currentUserState.role else "TEKNISI"
            
            val user = com.pln.monitoringpln.domain.model.User(
                id = currentUserState.id,
                email = currentUserState.email,
                namaLengkap = currentUserState.name,
                role = currentRole,
                photoUrl = currentUserState.photoUrl
            )

            val profileResult = updateUserProfileUseCase(user)
            
            if (profileResult.isFailure) {
                val exception = profileResult.exceptionOrNull()
                if (exception is com.pln.monitoringpln.domain.exception.ValidationException) {
                    when {
                        exception.message?.contains("Nama") == true -> _state.update { it.copy(isLoading = false, nameError = exception.message) }
                        exception.message?.contains("Email") == true -> _state.update { it.copy(isLoading = false, emailError = exception.message) }
                        else -> _state.update { it.copy(isLoading = false, error = exception.message) }
                    }
                    return@launch
                }
            }
            
            // 2. Update Password (if provided)
            var passwordResult: Result<Unit> = Result.success(Unit)
            if (currentUserState.password.isNotEmpty()) {
                passwordResult = updatePasswordUseCase(currentUserState.password, currentUserState.confirmPassword)
                
                if (passwordResult.isFailure) {
                    val exception = passwordResult.exceptionOrNull()
                    if (exception is com.pln.monitoringpln.domain.exception.ValidationException) {
                         _state.update { it.copy(isLoading = false, passwordError = exception.message) }
                        return@launch
                    }
                    // Handle Supabase specific error "New password should be different from the old password"
                    if (exception?.message?.contains("different from the old password", ignoreCase = true) == true) {
                         _state.update { it.copy(isLoading = false, passwordError = "Password baru tidak boleh sama dengan password lama") }
                         return@launch
                    }
                }
            }

            if (profileResult.isSuccess && passwordResult.isSuccess) {
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } else {
                val errorMessage = profileResult.exceptionOrNull()?.message 
                    ?: passwordResult.exceptionOrNull()?.message 
                    ?: "Gagal menyimpan perubahan"
                _state.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }
}
