package com.pln.monitoringpln.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    // Mock Data
    private val allData = listOf(
        SearchResult("1", "Perbaikan Gardu A", "Gardu Kayu Tangi 1", SearchResultType.TASK),
        SearchResult("2", "Inspeksi Rutin", "Gardu Kayu Tangi 2", SearchResultType.TASK),
        SearchResult("3", "Trafo 100kVA", "Normal - Gardu A", SearchResultType.EQUIPMENT),
        SearchResult("4", "Kabel Distribusi", "Rusak - Gardu B", SearchResultType.EQUIPMENT),
        SearchResult("5", "Budi Santoso", "TKN-001-PLN", SearchResultType.TECHNICIAN),
        SearchResult("6", "Ahmad Dani", "TKN-002-PLN", SearchResultType.TECHNICIAN)
    )

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, isLoading = true) }
        
        viewModelScope.launch {
            delay(500) // Simulate network delay
            
            if (query.isBlank()) {
                _state.update { it.copy(results = emptyList(), isLoading = false) }
                return@launch
            }

            val filtered = allData.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.subtitle.contains(query, ignoreCase = true) 
            }
            
            _state.update { it.copy(results = filtered, isLoading = false) }
        }
    }
}
