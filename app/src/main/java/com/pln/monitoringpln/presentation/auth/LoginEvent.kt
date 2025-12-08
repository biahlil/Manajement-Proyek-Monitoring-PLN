package com.pln.monitoringpln.presentation.auth

sealed interface LoginEvent {
    data class Login(val email: String, val password: String) : LoginEvent
    data object ErrorDismissed : LoginEvent
}
