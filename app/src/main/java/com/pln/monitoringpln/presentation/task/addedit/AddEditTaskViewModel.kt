package com.pln.monitoringpln.presentation.task.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.UserRepository
import com.pln.monitoringpln.domain.usecase.tugas.CreateTaskUseCase
import com.pln.monitoringpln.domain.usecase.tugas.GetTaskDetailUseCase
import com.pln.monitoringpln.domain.usecase.tugas.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class AddEditTaskViewModel(
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val alatRepository: AlatRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditTaskState())
    val state: StateFlow<AddEditTaskState> = _state.asStateFlow()

    private var allEquipmentsCache: List<Alat> = emptyList()

    init {
        loadData()
    }

    fun loadTask(taskId: String?) {
        val actualTaskId = if (taskId == "null" || taskId.isNullOrBlank()) null else taskId
        _state.update { it.copy(taskId = actualTaskId, error = null) } // Clear error on load
        if (actualTaskId != null) {
            loadTaskDetail(actualTaskId)
        } else {
            // Reset state for new task if needed, or keep defaults
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load Equipments
            alatRepository.getAllAlat().collect { list ->
                // Filter out equipment with empty IDs
                val validList = list.filter { alat -> alat.id.isNotBlank() }
                allEquipmentsCache = validList
                _state.update { it.copy(availableEquipments = validList) }
            }
        }
        viewModelScope.launch {
            // Load Technicians
            val techniciansResult = userRepository.getAllTeknisi()
            if (techniciansResult.isSuccess) {
                val allUsers = techniciansResult.getOrNull() ?: emptyList()
                // Filter only users with role "Teknisi" (Case Insensitive check)
                val technicians = allUsers.filter { it.role.equals("Teknisi", ignoreCase = true) }
                _state.update { it.copy(availableTechnicians = technicians) }
            }
        }
    }

    private fun loadTaskDetail(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = getTaskDetailUseCase(id)
            if (result.isSuccess) {
                val detail = result.getOrNull()!!
                val task = detail.task
                val deadline = task.tglJatuhTempo.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                _state.update {
                    it.copy(
                        isLoading = false,
                        title = task.judul,
                        description = task.deskripsi,
                        deadline = deadline,
                        selectedEquipment = detail.equipment,
                        selectedTechnician = detail.technician,
                        equipmentSearchQuery = detail.equipment?.let { eq -> "${eq.namaAlat} - ${eq.kodeAlat}" } ?: "",
                        status = task.status,
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Gagal memuat detail tugas") }
            }
        }
    }

    fun onTitleChange(title: String) {
        _state.update { it.copy(title = title, titleError = null) }
    }

    fun onDescriptionChange(description: String) {
        _state.update { it.copy(description = description, descriptionError = null) }
    }

    fun onEquipmentSearchQueryChange(query: String) {
        _state.update {
            it.copy(
                equipmentSearchQuery = query,
                availableEquipments = if (query.isBlank()) {
                    allEquipmentsCache
                } else {
                    allEquipmentsCache.filter { eq ->
                        eq.namaAlat.contains(query, ignoreCase = true) ||
                                eq.kodeAlat.contains(query, ignoreCase = true)
                    }
                },
            )
        }
    }

    fun onEquipmentSelected(equipment: Alat) {
        _state.update {
            it.copy(
                selectedEquipment = equipment,
                equipmentSearchQuery = "${equipment.namaAlat} - ${equipment.kodeAlat}",
            )
        }
    }

    fun onDeadlineSelected(date: LocalDate) {
        _state.update { it.copy(deadline = date) }
    }

    fun onTechnicianSelected(technician: User) {
        _state.update { it.copy(selectedTechnician = technician) }
    }

    fun onSaveTask() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val currentState = _state.value

            // Validation handled in UseCase, but basic UI check here
            if (currentState.title.isBlank() ||
                currentState.selectedEquipment == null ||
                currentState.deadline == null ||
                currentState.selectedTechnician == null
            ) {
                _state.update { it.copy(isSaving = false, error = "Mohon lengkapi semua field bertanda *") }
                return@launch
            }

            val deadlineDate = Date.from(currentState.deadline!!.atStartOfDay(ZoneId.systemDefault()).toInstant())

            if (currentState.taskId != null) {
                // Update
                val result = updateTaskUseCase(
                    taskId = currentState.taskId,
                    judul = currentState.title,
                    deskripsi = currentState.description,
                    idAlat = currentState.selectedEquipment!!.id,
                    idTeknisi = currentState.selectedTechnician!!.id,
                    tglJatuhTempo = deadlineDate,
                    status = currentState.status,
                )

                if (result.isSuccess) {
                    _state.update { it.copy(isSaving = false, isTaskSaved = true, savedTaskId = currentState.taskId) }
                } else {
                    val exception = result.exceptionOrNull()
                    if (exception is IllegalArgumentException) {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                titleError = if (exception.message?.contains("Judul") == true) exception.message else null,
                                descriptionError = if (exception.message?.contains("Deskripsi") == true) exception.message else null,
                                error = if (exception.message?.contains("Judul") == false && exception.message?.contains(
                                        "Deskripsi"
                                    ) == false
                                ) exception.message else null
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                error = exception?.message ?: "Gagal menyimpan tugas"
                            )
                        }
                    }
                }
            } else {
                // Create
                val result = createTaskUseCase(
                    judul = currentState.title,
                    deskripsi = currentState.description,
                    idAlat = currentState.selectedEquipment!!.id,
                    idTeknisi = currentState.selectedTechnician!!.id,
                    tglJatuhTempo = deadlineDate,
                )

                if (result.isSuccess) {
                    val newTaskId = result.getOrNull()
                    _state.update { it.copy(isSaving = false, isTaskSaved = true, savedTaskId = newTaskId) }
                } else {
                    val exception = result.exceptionOrNull()
                    if (exception is IllegalArgumentException) {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                titleError = if (exception.message?.contains("Judul") == true) exception.message else null,
                                descriptionError = if (exception.message?.contains("Deskripsi") == true) exception.message else null,
                                error = if (exception.message?.contains("Judul") == false && exception.message?.contains(
                                        "Deskripsi"
                                    ) == false
                                ) exception.message else null
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                error = exception?.message ?: "Gagal menyimpan tugas"
                            )
                        }
                    }
                }
            }
        }
    }
}
