package com.pln.monitoringpln.di

import org.koin.dsl.module

import com.pln.monitoringpln.domain.usecase.auth.CheckUserRoleUseCase
import com.pln.monitoringpln.domain.usecase.auth.LoginUseCase
import com.pln.monitoringpln.domain.usecase.auth.CreateUserUseCase
import com.pln.monitoringpln.domain.usecase.auth.GetCurrentUserIdUseCase

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { CheckUserRoleUseCase(get()) }
    factory { GetCurrentUserIdUseCase(get()) }
    factory { CreateUserUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.dashboard.GetDashboardSummaryUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.tugas.ObserveTasksUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.technician.GetTechniciansUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.alat.GetAllAlatUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.dashboard.GetDashboardTechniciansUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.user.DeleteUserUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.report.ExportReportUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.report.ExportFullReportUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.tugas.CompleteTaskUseCase(get(), get()) }
}
