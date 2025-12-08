package com.pln.monitoringpln.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            android.util.Log.d("LoginViewModel", "Checking session...")
            try {
                loginUseCase.loadSession()
            } catch (e: Exception) {
                android.util.Log.e("LoginViewModel", "Error loading session", e)
            }

            loginUseCase.isUserLoggedIn().collect { isLoggedIn ->
                android.util.Log.d("LoginViewModel", "Session status: $isLoggedIn")
                if (isLoggedIn) {
                    _state.update { it.copy(isSuccess = true) }
                }
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> {
                login(event.email, event.password)
            }
            is LoginEvent.ErrorDismissed -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = loginUseCase(email, password)
            result.onSuccess {
                // Trigger immediate sync on login
                val workManager = androidx.work.WorkManager.getInstance(com.pln.monitoringpln.app.BaseApplication.instance)
                val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.pln.monitoringpln.data.worker.SyncWorker>()
                    .setConstraints(
                        androidx.work.Constraints.Builder()
                            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                            .build(),
                    )
                    .addTag("SyncWorker")
                    .build()

                workManager.enqueueUniqueWork(
                    "SyncWorker",
                    androidx.work.ExistingWorkPolicy.REPLACE,
                    syncRequest,
                )

                _state.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message ?: "Login failed") }
            }
        }
    }
}
