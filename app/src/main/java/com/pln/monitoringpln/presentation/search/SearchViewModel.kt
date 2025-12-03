package com.pln.monitoringpln.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import com.pln.monitoringpln.domain.repository.UserRepository
import kotlinx.coroutines.flow.first

class SearchViewModel(
    private val tugasRepository: TugasRepository,
    private val alatRepository: AlatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, isLoading = true) }
        
        viewModelScope.launch {
            delay(300) // Debounce
            
            if (query.isBlank()) {
                _state.update { it.copy(results = emptyList(), isLoading = false) }
                return@launch
            }

            val results = mutableListOf<SearchResult>()

            // 1. Search Tasks
            // Note: We might need a more generic search in repositories or fetch all and filter here for now
            // For efficiency, repositories should support search. TugasRepository already does.
            val tasksResult = tugasRepository.getTasksByTeknisi("", query) // Empty ID to search all if logic allows, or we need a new method
            // Actually getTasksByTeknisi filters by ID. We need a general search or search by current user role.
            // Let's assume we search all tasks for now or use observeAllTasks and filter.
            
            // Using observeAllTasks().first() to get current snapshot
            val allTasks = tugasRepository.observeAllTasks().first()
            val filteredTasks = allTasks.filter { 
                it.judul.contains(query, ignoreCase = true) || 
                it.deskripsi.contains(query, ignoreCase = true) 
            }
            results.addAll(filteredTasks.map { 
                SearchResult(it.id, it.judul, it.status, SearchResultType.TASK) 
            })

            // 2. Search Equipment
            val allEquipment = alatRepository.getAllAlat().first()
            val filteredEquipment = allEquipment.filter { 
                it.namaAlat.contains(query, ignoreCase = true) || 
                it.kodeAlat.contains(query, ignoreCase = true) 
            }
            results.addAll(filteredEquipment.map { 
                SearchResult(it.id, it.namaAlat, "${it.kodeAlat} - ${it.kondisi}", SearchResultType.EQUIPMENT) 
            })

            // 3. Search Technicians
            val techniciansResult = userRepository.getAllTeknisi()
            val technicians = techniciansResult.getOrDefault(emptyList())
            val filteredTechnicians = technicians.filter { 
                it.namaLengkap.contains(query, ignoreCase = true) || 
                it.email.contains(query, ignoreCase = true) 
            }
            results.addAll(filteredTechnicians.map { 
                SearchResult(it.id, it.namaLengkap, "Teknisi", SearchResultType.TECHNICIAN) 
            })
            
            _state.update { it.copy(results = results, isLoading = false) }
        }
    }
}
