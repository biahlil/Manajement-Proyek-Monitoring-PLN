package com.pln.monitoringpln.di

import com.pln.monitoringpln.domain.usecase.auth.CheckUserRoleUseCase
import com.pln.monitoringpln.domain.usecase.auth.CreateUserUseCase
import com.pln.monitoringpln.domain.usecase.auth.LoginUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { CheckUserRoleUseCase(get()) }
    factory { CreateUserUseCase(get()) }
}
