package com.pln.monitoringpln.di

import com.pln.monitoringpln.presentation.auth.LoginViewModel
import com.pln.monitoringpln.presentation.dashboard.DashboardViewModel
import com.pln.monitoringpln.presentation.equipment.addedit.AddEditEquipmentViewModel
import com.pln.monitoringpln.presentation.equipment.detail.EquipmentDetailViewModel
import com.pln.monitoringpln.presentation.equipment.list.EquipmentListViewModel
import com.pln.monitoringpln.presentation.profile.ProfileViewModel
import com.pln.monitoringpln.presentation.profile.edit.EditProfileViewModel
import com.pln.monitoringpln.presentation.search.SearchViewModel
import com.pln.monitoringpln.presentation.task.TaskListViewModel
import com.pln.monitoringpln.presentation.task.addedit.AddEditTaskViewModel
import com.pln.monitoringpln.presentation.task.complete.CompleteTaskViewModel
import com.pln.monitoringpln.presentation.task.detail.TaskDetailViewModel
import com.pln.monitoringpln.presentation.technician.add.AddTechnicianViewModel
import com.pln.monitoringpln.presentation.technician.list.TechnicianListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { com.pln.monitoringpln.presentation.splash.SplashViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { DashboardViewModel(get(), get(), get(), get(), get(), get()) }

    // Use Cases (Task)
    // Use Cases (Task)
    factory { com.pln.monitoringpln.domain.usecase.tugas.CreateTaskUseCase(get(), get(), get()) }
    factory { com.pln.monitoringpln.domain.usecase.tugas.SyncTasksUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.tugas.GetTaskDetailUseCase(get(), get(), get()) }
    factory { com.pln.monitoringpln.domain.usecase.tugas.DeleteTaskUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.tugas.UpdateTaskStatusUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.tugas.UpdateTaskUseCase(get(), get(), get()) }

    // Use Cases (Profile)
    factory { com.pln.monitoringpln.domain.usecase.user.GetUserProfileUseCase(get(), get()) }
    single<com.pln.monitoringpln.domain.repository.StorageRepository> { com.pln.monitoringpln.data.repository.StorageRepositoryImpl(get()) }
    factory { com.pln.monitoringpln.domain.usecase.storage.UploadPhotoUseCase(get()) }

    factory { com.pln.monitoringpln.domain.usecase.user.UpdateUserProfileUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.user.ObserveUserProfileUseCase(get(), get()) }
    factory { com.pln.monitoringpln.domain.usecase.user.UploadAvatarUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.auth.UpdatePasswordUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.dashboard.GetDashboardSummaryUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.dashboard.GetDashboardTechniciansUseCase(get()) }
    factory { com.pln.monitoringpln.domain.usecase.auth.LogoutUseCase(get()) }

    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { DashboardViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { TaskListViewModel(get(), get(), get(), get(), get()) }
    viewModel { AddEditTaskViewModel(get(), get(), get(), get(), get()) }
    viewModel { TaskDetailViewModel(get(), get(), get(), get()) }
    viewModel { CompleteTaskViewModel(get(), get()) }
    viewModel { EquipmentListViewModel(get(), get(), get()) }
    viewModel { EquipmentDetailViewModel(get(), get(), get()) }
    viewModel { AddEditEquipmentViewModel(get()) }
    viewModel { TechnicianListViewModel(get(), get(), get(), get(), get()) }
    viewModel { AddTechnicianViewModel(get(), get(), androidContext()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { EditProfileViewModel(get(), get(), get(), get(), get()) }
    viewModel { com.pln.monitoringpln.presentation.report.ReportViewModel(get(), get()) }
}
