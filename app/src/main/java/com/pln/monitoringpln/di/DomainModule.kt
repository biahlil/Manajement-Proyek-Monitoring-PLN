package com.pln.monitoringpln.di

import org.koin.dsl.module

import com.pln.monitoringpln.domain.usecase.auth.CheckUserRoleUseCase
import com.pln.monitoringpln.domain.usecase.auth.LoginUseCase
import com.pln.monitoringpln.domain.usecase.auth.CreateUserUseCase

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { CheckUserRoleUseCase(get()) }
    factory { CreateUserUseCase(get()) }
}
