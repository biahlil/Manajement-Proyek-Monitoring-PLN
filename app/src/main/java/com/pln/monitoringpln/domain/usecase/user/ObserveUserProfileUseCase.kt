package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

class ObserveUserProfileUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.isUserLoggedIn().flatMapLatest { isLoggedIn ->
            if (isLoggedIn) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    userRepository.getUserProfileFlow(userId)
                } else {
                    emptyFlow()
                }
            } else {
                emptyFlow()
            }
        }
    }
}
