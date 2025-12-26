package com.school.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.app.domain.model.Attendance
import com.school.app.domain.model.User
import com.school.app.domain.usecase.GetAttendancesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val getAttendancesUseCase: GetAttendancesUseCase,
    private val currentUser: User
) : ViewModel() {

    private val _attendances = MutableStateFlow<List<Attendance>>(emptyList())
    val attendances: StateFlow<List<Attendance>> = _attendances

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        loadAttendances()
    }

    private fun loadAttendances() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val attendanceList = getAttendancesUseCase(currentUser)
                _attendances.value = attendanceList
            } catch (e: Exception) {
                _message.value = "Failed to load attendances: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}