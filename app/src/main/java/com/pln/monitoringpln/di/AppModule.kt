package com.pln.monitoringpln.di

import com.pln.monitoringpln.presentation.auth.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.pln.monitoringpln.presentation.dashboard.DashboardViewModel
import com.pln.monitoringpln.presentation.task.TaskListViewModel
import com.pln.monitoringpln.presentation.profile.ProfileViewModel
import com.pln.monitoringpln.presentation.task.addedit.AddEditTaskViewModel
import com.pln.monitoringpln.presentation.task.detail.TaskDetailViewModel
import com.pln.monitoringpln.presentation.equipment.list.EquipmentListViewModel
import com.pln.monitoringpln.presentation.equipment.detail.EquipmentDetailViewModel
import com.pln.monitoringpln.presentation.equipment.addedit.AddEditEquipmentViewModel
import com.pln.monitoringpln.presentation.technician.list.TechnicianListViewModel
import com.pln.monitoringpln.presentation.technician.add.AddTechnicianViewModel

val appModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { TaskListViewModel(get()) }
    viewModel { AddEditTaskViewModel() }
    viewModel { TaskDetailViewModel(get()) }
    viewModel { EquipmentListViewModel(get()) }
    viewModel { EquipmentDetailViewModel(get()) }
    viewModel { AddEditEquipmentViewModel() }
    viewModel { TechnicianListViewModel() }
    viewModel { AddTechnicianViewModel() }
    viewModel { SearchViewModel() }
    viewModel { EditProfileViewModel() }
}
