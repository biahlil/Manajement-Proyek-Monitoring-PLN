package com.pln.monitoringpln.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun checkSessionAndRole(
        onLogin: () -> Unit,
        onDashboard: () -> Unit
    ) {
        viewModelScope.launch {
            // 1. Load Session
            try {
                authRepository.loadSession()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 2. Check if logged in
            val isLoggedIn = authRepository.isUserLoggedIn().first()
            
            // Artificial delay for branding (optional, keep it short)
            delay(1000)

            if (isLoggedIn) {
                // 3. Pre-fetch role (optional, but good for caching if repo supports it)
                // For now, just navigate to Dashboard. 
                // To fix the "Technician -> Admin" blink, DashboardViewModel needs to handle "Loading" state.
                onDashboard()
            } else {
                onLogin()
            }
        }
    }
}
