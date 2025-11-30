package com.pln.monitoringpln.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pln.monitoringpln.presentation.auth.LoginScreen
import com.pln.monitoringpln.presentation.auth.LoginViewModel
import com.pln.monitoringpln.presentation.dashboard.DashboardScreen
import com.pln.monitoringpln.presentation.dashboard.DashboardViewModel
import com.pln.monitoringpln.presentation.task.TaskListScreen
import com.pln.monitoringpln.presentation.task.TaskListViewModel
import com.pln.monitoringpln.presentation.task.addedit.AddEditTaskScreen
import com.pln.monitoringpln.presentation.task.addedit.AddEditTaskViewModel
import com.pln.monitoringpln.presentation.task.detail.TaskDetailScreen
import com.pln.monitoringpln.presentation.task.detail.TaskDetailViewModel
import com.pln.monitoringpln.presentation.equipment.list.EquipmentListScreen
import com.pln.monitoringpln.presentation.equipment.list.EquipmentListViewModel
import com.pln.monitoringpln.presentation.equipment.detail.EquipmentDetailScreen
import com.pln.monitoringpln.presentation.equipment.detail.EquipmentDetailViewModel
import com.pln.monitoringpln.presentation.equipment.addedit.AddEditEquipmentScreen
import com.pln.monitoringpln.presentation.equipment.addedit.AddEditEquipmentViewModel
import com.pln.monitoringpln.presentation.technician.list.TechnicianListScreen
import com.pln.monitoringpln.presentation.technician.list.TechnicianListViewModel
import com.pln.monitoringpln.presentation.technician.add.AddTechnicianScreen
import com.pln.monitoringpln.presentation.technician.add.AddTechnicianViewModel
import com.pln.monitoringpln.presentation.search.SearchScreen
import com.pln.monitoringpln.presentation.search.SearchViewModel
import com.pln.monitoringpln.presentation.profile.edit.EditProfileScreen
import com.pln.monitoringpln.presentation.profile.edit.EditProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute != Screen.Login.route) {
                AppHeader()
            }
        },
        bottomBar = {
            if (currentRoute != Screen.Login.route) {
                BottomNavigationBar(
                    navController = navController,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                val loginViewModel: LoginViewModel = koinViewModel()
                val loginState by loginViewModel.state.collectAsState()

                // Navigate to Dashboard on Success
                LaunchedEffect(loginState.isSuccess) {
                    if (loginState.isSuccess) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }

                LoginScreen(
                    state = loginState,
                    onEvent = loginViewModel::onEvent
                )
            }

            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = koinViewModel()
                val dashboardState by dashboardViewModel.state.collectAsState()

                DashboardScreen(
                    state = dashboardState,
                    onEquipmentClick = { navController.navigate(Screen.EquipmentList.route) },
                    onTechnicianClick = { navController.navigate(Screen.TechnicianList.route) },
                    onTaskClick = { navController.navigate(Screen.TaskList.route) }
                )
            }

            composable(Screen.Search.route) {
                val viewModel: SearchViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                SearchScreen(
                    state = state,
                    onQueryChange = viewModel::onQueryChange
                )
            }

            composable(Screen.TaskList.route) {
                val taskListViewModel: TaskListViewModel = koinViewModel()
                val taskListState by taskListViewModel.state.collectAsState()

                TaskListScreen(
                    state = taskListState,
                    onSearchQueryChange = taskListViewModel::onSearchQueryChange,
                    onDeleteTask = taskListViewModel::onDeleteTask,
                    onConfirmDelete = taskListViewModel::onConfirmDelete,
                    onDismissDelete = taskListViewModel::onDismissDelete,
                    onAddTask = { navController.navigate(Screen.AddEditTask.route) },
                    onEditTask = { task -> navController.navigate(Screen.AddEditTask.route) } // Pass ID later
                )
            }

            composable(Screen.AddEditTask.route) {
                val viewModel: AddEditTaskViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()
                
                LaunchedEffect(state.isTaskSaved) {
                    if (state.isTaskSaved) {
                        navController.popBackStack()
                    }
                }

                AddEditTaskScreen(
                    state = state,
                    onTitleChange = viewModel::onTitleChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onEquipmentSearchQueryChange = viewModel::onEquipmentSearchQueryChange,
                    onEquipmentSelected = viewModel::onEquipmentSelected,
                    onDeadlineSelected = viewModel::onDeadlineSelected,
                    onTechnicianSelected = viewModel::onTechnicianSelected,
                    onSaveTask = viewModel::onSaveTask,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.DetailTask.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
                val viewModel: TaskDetailViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(taskId) {
                    viewModel.loadTask(taskId)
                }

                LaunchedEffect(state.isDeleted) {
                    if (state.isDeleted) {
                        navController.popBackStack()
                    }
                }

                TaskDetailScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Screen.AddEditTask.route) }, // For now just open AddEditTask, later pass ID
                    onDelete = viewModel::onDeleteTask,
                    onConfirmDelete = viewModel::onConfirmDelete,
                    onDismissDelete = viewModel::onDismissDeleteDialog
                )
            }

            composable(Screen.EquipmentList.route) {
                val viewModel: EquipmentListViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                EquipmentListScreen(
                    state = state,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onBack = { navController.popBackStack() },
                    onAddEquipment = { navController.navigate(Screen.AddEditEquipment.createRoute(null)) },
                    onDeleteEquipment = viewModel::onDeleteEquipment,
                    onConfirmDelete = viewModel::onConfirmDelete,
                    onDismissDelete = viewModel::onDismissDeleteDialog,
                    onItemClick = { equipment -> navController.navigate(Screen.DetailEquipment.createRoute(equipment.id)) }
                )
            }

            composable(
                route = Screen.DetailEquipment.route,
                arguments = listOf(navArgument("equipmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val equipmentId = backStackEntry.arguments?.getString("equipmentId") ?: return@composable
                val viewModel: EquipmentDetailViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(equipmentId) {
                    viewModel.loadEquipment(equipmentId)
                }

                LaunchedEffect(state.isDeleted) {
                    if (state.isDeleted) {
                        navController.popBackStack()
                    }
                }

                EquipmentDetailScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Screen.AddEditEquipment.createRoute(equipmentId)) },
                    onDelete = viewModel::onDeleteClick,
                    onConfirmDelete = viewModel::onConfirmDelete,
                    onDismissDelete = viewModel::onDismissDeleteDialog
                )
            }

            composable(
                route = Screen.AddEditEquipment.route,
                arguments = listOf(navArgument("equipmentId") { 
                    type = NavType.StringType 
                    nullable = true
                })
            ) { backStackEntry ->
                val equipmentId = backStackEntry.arguments?.getString("equipmentId")
                val viewModel: AddEditEquipmentViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(equipmentId) {
                    viewModel.loadEquipment(equipmentId)
                }

                LaunchedEffect(state.isSaved) {
                    if (state.isSaved) {
                        navController.popBackStack()
                    }
                }

                AddEditEquipmentScreen(
                    state = state,
                    onNamaChange = viewModel::onNamaChange,
                    onKodeChange = viewModel::onKodeChange,
                    onTipeChange = viewModel::onTipeChange,
                    onStatusChange = viewModel::onStatusChange,
                    onLokasiChange = viewModel::onLokasiChange,
                    onSave = viewModel::onSaveEquipment,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TechnicianList.route) {
                val viewModel: TechnicianListViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                TechnicianListScreen(
                    state = state,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onBack = { navController.popBackStack() },
                    onAddTechnician = { navController.navigate(Screen.AddTechnician.route) },
                    onDeleteTechnician = viewModel::onDeleteTechnician,
                    onConfirmDelete = viewModel::onConfirmDelete,
                    onDismissDelete = viewModel::onDismissDeleteDialog
                )
            }

            composable(Screen.AddTechnician.route) {
                val viewModel: AddTechnicianViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(state.isSaved) {
                    if (state.isSaved) {
                        navController.popBackStack()
                    }
                }

                AddTechnicianScreen(
                    state = state,
                    onNamaChange = viewModel::onNamaChange,
                    onIdChange = viewModel::onIdChange,
                    onEmailChange = viewModel::onEmailChange,
                    onNoTeleponChange = viewModel::onNoTeleponChange,
                    onAreaTugasChange = viewModel::onAreaTugasChange,
                    onSave = viewModel::onSaveTechnician,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                val profileViewModel: com.pln.monitoringpln.presentation.profile.ProfileViewModel = koinViewModel()
                val profileState by profileViewModel.state.collectAsState()

                // Handle Logout Navigation
                LaunchedEffect(profileState.isLoggedOut) {
                    if (profileState.isLoggedOut) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    }
                }

                com.pln.monitoringpln.presentation.profile.ProfileScreen(
                    state = profileState,
                    onLogout = profileViewModel::onLogout,
                    onEditProfile = { navController.navigate(Screen.EditProfile.route) }
                )
            }

            composable(Screen.EditProfile.route) {
                val viewModel: EditProfileViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(state.isSaved) {
                    if (state.isSaved) {
                        navController.popBackStack()
                    }
                }

                EditProfileScreen(
                    state = state,
                    onNameChange = viewModel::onNameChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onSave = viewModel::onSave,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
