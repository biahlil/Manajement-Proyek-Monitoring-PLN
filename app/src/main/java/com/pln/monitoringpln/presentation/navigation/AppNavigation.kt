package com.pln.monitoringpln.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pln.monitoringpln.presentation.auth.LoginScreen
import com.pln.monitoringpln.presentation.auth.LoginViewModel
import com.pln.monitoringpln.presentation.dashboard.DashboardScreen
import com.pln.monitoringpln.presentation.dashboard.DashboardViewModel
import com.pln.monitoringpln.presentation.equipment.addedit.AddEditEquipmentScreen
import com.pln.monitoringpln.presentation.equipment.addedit.AddEditEquipmentViewModel
import com.pln.monitoringpln.presentation.equipment.detail.EquipmentDetailScreen
import com.pln.monitoringpln.presentation.equipment.detail.EquipmentDetailViewModel
import com.pln.monitoringpln.presentation.equipment.list.EquipmentListScreen
import com.pln.monitoringpln.presentation.equipment.list.EquipmentListViewModel
import com.pln.monitoringpln.presentation.profile.edit.EditProfileScreen
import com.pln.monitoringpln.presentation.profile.edit.EditProfileViewModel
import com.pln.monitoringpln.presentation.search.SearchScreen
import com.pln.monitoringpln.presentation.search.SearchViewModel
import com.pln.monitoringpln.presentation.task.TaskListScreen
import com.pln.monitoringpln.presentation.task.TaskListViewModel
import com.pln.monitoringpln.presentation.task.addedit.AddEditTaskScreen
import com.pln.monitoringpln.presentation.task.addedit.AddEditTaskViewModel
import com.pln.monitoringpln.presentation.task.detail.TaskDetailScreen
import com.pln.monitoringpln.presentation.task.detail.TaskDetailViewModel
import com.pln.monitoringpln.presentation.technician.add.AddTechnicianScreen
import com.pln.monitoringpln.presentation.technician.add.AddTechnicianViewModel
import com.pln.monitoringpln.presentation.technician.list.TechnicianListScreen
import com.pln.monitoringpln.presentation.technician.list.TechnicianListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var lastClickTime by androidx.compose.runtime.remember { androidx.compose.runtime.mutableLongStateOf(0L) }

    fun navigateWithDebounce(
        route: String,
        popUpTo: String? = null,
        inclusive: Boolean = false,
        saveState: Boolean = false,
        restoreState: Boolean = false,
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 150) {
            lastClickTime = currentTime
            scope.launch {
                kotlinx.coroutines.delay(150)
                navController.navigate(route) {
                    if (popUpTo != null) {
                        popUpTo(popUpTo) {
                            this.inclusive = inclusive
                            this.saveState = saveState
                        }
                    }
                    launchSingleTop = true
                    this.restoreState = restoreState
                }
            }
        }
    }

    Scaffold(
        topBar = {
            val showHeader = currentRoute == Screen.Dashboard.route ||
                currentRoute == Screen.Search.route ||
                currentRoute == Screen.Profile.route

            androidx.compose.animation.AnimatedVisibility(
                visible = showHeader,
                enter = androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.fadeOut(),
            ) {
                AppHeader()
            }
        },
        bottomBar = {
            if (currentRoute != Screen.Login.route && currentRoute != Screen.Splash.route) {
                BottomNavigationBar(
                    navController = navController,
                    onItemClick = { item ->
                        navigateWithDebounce(
                            route = item.route,
                            popUpTo = Screen.Dashboard.route,
                            saveState = true,
                            restoreState = true,
                        )
                    },
                )
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            // ... (Splash, Login, Dashboard, Search remain same)

            composable(Screen.Splash.route) {
                val viewModel: com.pln.monitoringpln.presentation.splash.SplashViewModel = koinViewModel()
                com.pln.monitoringpln.presentation.splash.SplashScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = {
                        navigateWithDebounce(Screen.Login.route, popUpTo = Screen.Splash.route, inclusive = true)
                    },
                    onNavigateToDashboard = {
                        navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Splash.route, inclusive = true)
                    },
                )
            }

            composable(Screen.Login.route) {
                val loginViewModel: LoginViewModel = koinViewModel()
                val loginState by loginViewModel.state.collectAsState()

                LoginScreen(
                    state = loginState,
                    onEvent = loginViewModel::onEvent,
                    onLoginSuccess = {
                        navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Login.route, inclusive = true)
                    },
                )
            }

            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = koinViewModel()
                val dashboardState by dashboardViewModel.state.collectAsState()

                DashboardScreen(
                    onNavigate = { route -> navigateWithDebounce(route) },
                    viewModel = dashboardViewModel,
                )
            }

            composable(Screen.Search.route) {
                val viewModel: SearchViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                androidx.activity.compose.BackHandler {
                    navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route)
                }

                SearchScreen(
                    navController = navController,
                    state = state,
                    onQueryChange = viewModel::onQueryChange,
                )
            }

            composable(Screen.TaskList.route) {
                val taskListViewModel: TaskListViewModel = koinViewModel()
                val taskListState by taskListViewModel.state.collectAsState()

                androidx.activity.compose.BackHandler {
                    navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route)
                }

                TaskListScreen(
                    state = taskListState,
                    onSearchQueryChange = taskListViewModel::onSearchQueryChange,
                    onAddTask = { navigateWithDebounce(Screen.AddEditTask.createRoute(null)) },
                    onTaskClick = { taskId -> navigateWithDebounce(Screen.DetailTask.createRoute(taskId)) },
                    onBack = { navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route) },
                )
            }

            composable(
                route = Screen.AddEditTask.route,
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.StringType
                        nullable = true
                    },
                ),
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")
                val viewModel: AddEditTaskViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(taskId) {
                    viewModel.loadTask(taskId)
                }

                androidx.activity.compose.BackHandler {
                    navigateWithDebounce(Screen.TaskList.route, popUpTo = Screen.TaskList.route)
                }

                LaunchedEffect(state.isTaskSaved) {
                    if (state.isTaskSaved) {
                        val savedTaskId = state.savedTaskId
                        if (savedTaskId != null) {
                            navigateWithDebounce(Screen.DetailTask.createRoute(savedTaskId), popUpTo = Screen.TaskList.route)
                        } else {
                            navigateWithDebounce(Screen.TaskList.route, popUpTo = Screen.TaskList.route)
                        }
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
                    onBack = { navigateWithDebounce(Screen.TaskList.route, popUpTo = Screen.TaskList.route) },
                )
            }

            composable(
                route = Screen.DetailTask.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
                val viewModel: TaskDetailViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(taskId) {
                    viewModel.loadTask(taskId)
                }

                LaunchedEffect(state.isDeleted) {
                    if (state.isDeleted) {
                        navigateWithDebounce(Screen.TaskList.route, popUpTo = Screen.TaskList.route)
                    }
                }

                TaskDetailScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onEdit = { navigateWithDebounce(Screen.AddEditTask.createRoute(taskId)) },
                    onDelete = viewModel::onDeleteTask,
                    onConfirmDelete = viewModel::onConfirmDelete,
                    onDismissDelete = viewModel::onDismissDeleteDialog,
                    onCompleteTask = { navigateWithDebounce(Screen.CompleteTask.createRoute(taskId)) },
                )
            }

            composable(
                route = Screen.CompleteTask.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
                val viewModel: com.pln.monitoringpln.presentation.task.complete.CompleteTaskViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(taskId) {
                    viewModel.loadTask(taskId)
                }

                com.pln.monitoringpln.presentation.task.complete.CompleteTaskScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onConditionChange = viewModel::onConditionChange,
                    onEquipmentStatusChange = viewModel::onEquipmentStatusChange,
                    onProofSelected = viewModel::onProofSelected,
                    onCompleteTask = viewModel::onCompleteTask,
                )
            }

            composable(
                route = Screen.EquipmentList.route,
                arguments = listOf(navArgument("filterType") { type = NavType.StringType }),
            ) { backStackEntry ->
                val filterType = backStackEntry.arguments?.getString("filterType") ?: "all_equipment"
                val viewModel: EquipmentListViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                androidx.activity.compose.BackHandler {
                    navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route)
                }

                LaunchedEffect(filterType) {
                    viewModel.setFilter(filterType)
                }

                EquipmentListScreen(
                    state = state,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onBack = { navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route) },
                    onAddEquipment = { navigateWithDebounce(Screen.AddEditEquipment.createRoute(null)) },
                    onDeleteEquipment = viewModel::onDeleteEquipment,
                    onConfirmDelete = viewModel::onConfirmDelete,
                    onDismissDelete = viewModel::onDismissDeleteDialog,
                    onItemClick = { equipment -> navigateWithDebounce(Screen.DetailEquipment.createRoute(equipment.id)) },
                )
            }

            composable(
                route = Screen.DetailEquipment.route,
                arguments = listOf(navArgument("equipmentId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val equipmentId = backStackEntry.arguments?.getString("equipmentId") ?: return@composable
                val viewModel: EquipmentDetailViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(equipmentId) {
                    viewModel.loadEquipment(equipmentId)
                }

                LaunchedEffect(state.isDeleted) {
                    if (state.isDeleted) {
                        navigateWithDebounce(Screen.EquipmentList.createRoute("all_equipment"), popUpTo = Screen.EquipmentList.route)
                    }
                }

                EquipmentDetailScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onEdit = { navigateWithDebounce(Screen.AddEditEquipment.createRoute(equipmentId)) },
                    onDelete = viewModel::onDeleteClick,
                    onConfirmDelete = viewModel::onConfirmDelete,
                    onDismissDelete = viewModel::onDismissDeleteDialog,
                    onAddTask = { navigateWithDebounce(Screen.AddEditTask.route) },
                    onTaskClick = { taskId -> navigateWithDebounce(Screen.DetailTask.createRoute(taskId)) },
                )
            }

            composable(
                route = Screen.AddEditEquipment.route,
                arguments = listOf(
                    navArgument("equipmentId") {
                        type = NavType.StringType
                        nullable = true
                    },
                ),
            ) { backStackEntry ->
                val equipmentId = backStackEntry.arguments?.getString("equipmentId")
                val viewModel: AddEditEquipmentViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                val navigateBack = {
                    if (equipmentId != null) {
                        navigateWithDebounce(Screen.DetailEquipment.createRoute(equipmentId), popUpTo = Screen.DetailEquipment.route)
                    } else {
                        navigateWithDebounce(Screen.EquipmentList.createRoute("all_equipment"), popUpTo = Screen.EquipmentList.route)
                    }
                }

                androidx.activity.compose.BackHandler {
                    navigateBack()
                }

                LaunchedEffect(equipmentId) {
                    viewModel.loadEquipment(equipmentId)
                }

                LaunchedEffect(state.isSaved) {
                    if (state.isSaved) {
                        navigateBack()
                    }
                }

                AddEditEquipmentScreen(
                    state = state,
                    onNamaChange = viewModel::onNamaChange,
                    onKodeChange = viewModel::onKodeChange,
                    onTipeChange = viewModel::onTipeChange,
                    onStatusChange = viewModel::onStatusChange,
                    onLokasiChange = viewModel::onLokasiChange,
                    onLatChange = viewModel::onLatitudeChange,
                    onLngChange = viewModel::onLongitudeChange,
                    onSave = viewModel::onSaveEquipment,
                    onBack = navigateBack,
                    viewModel = viewModel,
                )
            }

            composable(Screen.TechnicianList.route) {
                val viewModel: TechnicianListViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                androidx.activity.compose.BackHandler {
                    navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route)
                }

                TechnicianListScreen(
                    state = state,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onBack = { navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route) },
                    onAddTechnician = { navigateWithDebounce(Screen.AddTechnician.route) },
                    onDeleteTechnician = viewModel::onDeleteTechnician,
                    onConfirmDelete = viewModel::onConfirmDelete,
                    onDismissDelete = viewModel::onDismissDeleteDialog,
                    onRefresh = viewModel::refreshTechnicians,
                )
            }

            composable(Screen.AddTechnician.route) {
                val viewModel: AddTechnicianViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                androidx.activity.compose.BackHandler {
                    navigateWithDebounce(Screen.TechnicianList.route, popUpTo = Screen.TechnicianList.route)
                }

                LaunchedEffect(state.isSaved) {
                    if (state.isSaved) {
                        navigateWithDebounce(Screen.TechnicianList.route, popUpTo = Screen.TechnicianList.route)
                    }
                }

                AddTechnicianScreen(
                    state = state,
                    onNamaChange = viewModel::onNamaChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onPhotoSelected = viewModel::onPhotoSelected,
                    onSave = viewModel::onSaveTechnician,
                    onBack = { navigateWithDebounce(Screen.TechnicianList.route, popUpTo = Screen.TechnicianList.route) },
                )
            }

            composable(Screen.Report.route) {
                val viewModel: com.pln.monitoringpln.presentation.report.ReportViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                androidx.activity.compose.BackHandler {
                    navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route)
                }

                com.pln.monitoringpln.presentation.report.ReportScreen(
                    state = state,
                    onStartDateChange = viewModel::onStartDateChange,
                    onEndDateChange = viewModel::onEndDateChange,
                    onFormatChange = viewModel::onFormatChange,
                    onExport = viewModel::onExport,
                    onBack = { navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route) },
                    onClearMessages = viewModel::clearMessages,
                    onFullReportChange = viewModel::onFullReportChange,
                )
            }

            composable(Screen.Profile.route) {
                val profileViewModel: com.pln.monitoringpln.presentation.profile.ProfileViewModel = koinViewModel()
                val profileState by profileViewModel.state.collectAsState()

                androidx.activity.compose.BackHandler {
                    navigateWithDebounce(Screen.Dashboard.route, popUpTo = Screen.Dashboard.route)
                }

                // Handle Logout Navigation
                LaunchedEffect(profileState.isLoggedOut) {
                    if (profileState.isLoggedOut) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                com.pln.monitoringpln.presentation.profile.ProfileScreen(
                    state = profileState,
                    onLogout = {
                        profileViewModel.onLogout()
                    },
                    onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                )
            }

            composable(Screen.EditProfile.route) {
                val viewModel: EditProfileViewModel = koinViewModel()
                val state by viewModel.state.collectAsState()

                androidx.activity.compose.BackHandler {
                    navigateWithDebounce(Screen.Profile.route, popUpTo = Screen.Profile.route)
                }

                LaunchedEffect(state.isSaved) {
                    if (state.isSaved) {
                        navigateWithDebounce(Screen.Profile.route, popUpTo = Screen.Profile.route)
                    }
                }

                EditProfileScreen(
                    state = state,
                    onNameChange = viewModel::onNameChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                    onAvatarSelected = viewModel::onAvatarSelected,
                    onSave = viewModel::onSave,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
