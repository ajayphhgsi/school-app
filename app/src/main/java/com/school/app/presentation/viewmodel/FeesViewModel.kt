package com.school.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.app.domain.model.Fee
import com.school.app.domain.model.FeeStatus
import com.school.app.domain.model.User
import com.school.app.domain.usecase.GetFeesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FeesViewModel(
    private val getFeesUseCase: GetFeesUseCase,
    private val currentUser: User
) : ViewModel() {

    private val _fees = MutableStateFlow<List<Fee>>(emptyList())
    val fees: StateFlow<List<Fee>> = _fees

    private val _filteredFees = MutableStateFlow<List<Fee>>(emptyList())
    val filteredFees: StateFlow<List<Fee>> = _filteredFees

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _statusFilter = MutableStateFlow<FeeStatus?>(null)
    val statusFilter: StateFlow<FeeStatus?> = _statusFilter

    private val _sortBy = MutableStateFlow<SortOption>(SortOption.DUE_DATE)
    val sortBy: StateFlow<SortOption> = _sortBy

    enum class SortOption {
        DUE_DATE, AMOUNT, STATUS
    }

    init {
        loadFees()
        setupFiltering()
    }

    private fun loadFees() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val feesData = getFeesUseCase(currentUser)
                _fees.value = feesData
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun setupFiltering() {
        viewModelScope.launch {
            combine(_fees, _statusFilter, _sortBy) { fees, statusFilter, sortBy ->
                var filtered = fees
                if (statusFilter != null) {
                    filtered = filtered.filter { it.status == statusFilter }
                }
                filtered = when (sortBy) {
                    SortOption.DUE_DATE -> filtered.sortedBy { it.dueDate }
                    SortOption.AMOUNT -> filtered.sortedBy { it.amount }
                    SortOption.STATUS -> filtered.sortedBy { it.status.name }
                }
                filtered
            }.collect {
                _filteredFees.value = it
            }
        }
    }

    fun setStatusFilter(status: FeeStatus?) {
        _statusFilter.value = status
    }

    fun setSortBy(sortOption: SortOption) {
        _sortBy.value = sortOption
    }
}