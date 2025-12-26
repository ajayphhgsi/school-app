package com.school.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.app.domain.model.Attendance
import com.school.app.domain.model.AttendanceStatus
import com.school.app.domain.model.Student
import com.school.app.domain.model.User
import com.school.app.domain.usecase.GetStudentsUseCase
import com.school.app.domain.usecase.MarkAttendanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AttendanceMarkingViewModel(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val markAttendanceUseCase: MarkAttendanceUseCase,
    private val currentUser: User
) : ViewModel() {

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    private val _attendanceMap = MutableStateFlow<Map<String, AttendanceStatus>>(emptyMap())
    val attendanceMap: StateFlow<Map<String, AttendanceStatus>> = _attendanceMap

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val studentList = getStudentsUseCase(currentUser)
                _students.value = studentList
                // Initialize attendance map with PRESENT
                val initialMap = studentList.associate { it.id to AttendanceStatus.PRESENT }
                _attendanceMap.value = initialMap
            } catch (e: Exception) {
                _message.value = "Failed to load students: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAttendance(studentId: String, status: AttendanceStatus) {
        val currentMap = _attendanceMap.value.toMutableMap()
        currentMap[studentId] = status
        _attendanceMap.value = currentMap
    }

    fun markAttendance(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val attendances = _attendanceMap.value.map { (studentId, status) ->
                    Attendance(
                        id = UUID.randomUUID().toString(),
                        studentId = studentId,
                        date = date,
                        status = status
                    )
                }
                var successCount = 0
                for (attendance in attendances) {
                    if (markAttendanceUseCase(attendance, currentUser)) {
                        successCount++
                    }
                }
                _message.value = "Marked attendance for $successCount students"
            } catch (e: Exception) {
                _message.value = "Failed to mark attendance: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}