package com.school.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.app.domain.model.AcademicRecord
import com.school.app.domain.model.Role
import com.school.app.domain.model.User
import com.school.app.domain.usecase.GetAcademicRecordsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AcademicHistoryViewModel(
    private val getAcademicRecordsUseCase: GetAcademicRecordsUseCase,
    private val currentUser: User
) : ViewModel() {

    private val _records = MutableStateFlow<List<AcademicRecord>>(emptyList())
    val records: StateFlow<List<AcademicRecord>> = _records

    private val _filteredRecords = MutableStateFlow<List<AcademicRecord>>(emptyList())
    val filteredRecords: StateFlow<List<AcademicRecord>> = _filteredRecords

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedSemester = MutableStateFlow<String?>(null)
    val selectedSemester: StateFlow<String?> = _selectedSemester

    private val _selectedYear = MutableStateFlow<Int?>(null)
    val selectedYear: StateFlow<Int?> = _selectedYear

    private val _sortOption = MutableStateFlow(SortOption.YEAR_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption

    fun loadAcademicRecords(studentId: String) {
        if (!canViewAcademicRecords(studentId)) {
            _error.value = "Access denied"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val recordsData = getAcademicRecordsUseCase(studentId)
                _records.value = recordsData
                applyFiltersAndSort()
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSemesterFilter(semester: String?) {
        _selectedSemester.value = semester
        applyFiltersAndSort()
    }

    fun setYearFilter(year: Int?) {
        _selectedYear.value = year
        applyFiltersAndSort()
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        var filtered = _records.value

        _selectedSemester.value?.let { semester ->
            filtered = filtered.filter { it.semester == semester }
        }

        _selectedYear.value?.let { year ->
            filtered = filtered.filter { it.year == year }
        }

        filtered = when (_sortOption.value) {
            SortOption.YEAR_DESC -> filtered.sortedByDescending { it.year }
            SortOption.YEAR_ASC -> filtered.sortedBy { it.year }
            SortOption.SUBJECT_ASC -> filtered.sortedBy { it.subject }
            SortOption.GRADE_DESC -> filtered.sortedByDescending { it.grade }
        }

        _filteredRecords.value = filtered
    }

    private fun canViewAcademicRecords(studentId: String): Boolean {
        return when (currentUser.role) {
            Role.ADMIN -> true
            Role.TEACHER -> true // Assume teachers can view their students' records
            Role.STUDENT -> currentUser.id == studentId
        }
    }
}

enum class SortOption {
    YEAR_DESC,
    YEAR_ASC,
    SUBJECT_ASC,
    GRADE_DESC
}