package com.pln.monitoringpln.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Search : Screen("search")
    object TaskList : Screen("task_list")
    object AddEditTask : Screen("add_edit_task?taskId={taskId}") {
        fun createRoute(taskId: String? = null) =
            if (taskId != null) "add_edit_task?taskId=$taskId" else "add_edit_task"
    }
    object DetailTask : Screen("detail_task/{taskId}") {
        fun createRoute(taskId: String) = "detail_task/$taskId"
    }
    object CompleteTask : Screen("complete_task/{taskId}") {
        fun createRoute(taskId: String) = "complete_task/$taskId"
    }
    object EquipmentList : Screen("equipment_list/{filterType}") {
        fun createRoute(filterType: String) = "equipment_list/$filterType"
    }
    object DetailEquipment : Screen("equipment_detail/{equipmentId}") {
        fun createRoute(equipmentId: String) = "equipment_detail/$equipmentId"
    }
    object AddEditEquipment : Screen("add_edit_equipment?equipmentId={equipmentId}") {
        fun createRoute(equipmentId: String? = null) =
            if (equipmentId != null) "add_edit_equipment?equipmentId=$equipmentId" else "add_edit_equipment"
    }
    object TechnicianList : Screen("technician_list")
    object AddTechnician : Screen("add_technician")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Report : Screen("report")
    object ListScreen : Screen("list_screen")
}
