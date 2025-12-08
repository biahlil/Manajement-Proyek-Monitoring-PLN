package com.pln.monitoringpln.presentation.search

data class SearchState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
)

data class SearchResult(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: SearchResultType,
)

enum class SearchResultType {
    TASK, EQUIPMENT, TECHNICIAN
}
