package com.school.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.app.domain.model.Role
import com.school.app.domain.model.Student
import com.school.app.domain.model.User
import com.school.app.domain.usecase.GetStudentUseCase
import com.school.app.domain.usecase.GetStudentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentDetailsViewModel(
    private val getStudentUseCase: GetStudentUseCase,
    private val getStudentsUseCase: GetStudentsUseCase,
    private val currentUser: User // Assume this is passed or injected
) : ViewModel() {

    private val _student = MutableStateFlow<Student?>(null)
    val student: StateFlow<Student?> = _student

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadStudent(studentId: String) {
        if (!canViewStudent(studentId)) {
            _error.value = "Access denied"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val studentData = getStudentUseCase(studentId)
                _student.value = studentData
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadStudents() {
        if (!canViewAllStudents()) {
            _error.value = "Access denied"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val studentsData = getStudentsUseCase()
                _students.value = studentsData
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun canViewStudent(studentId: String): Boolean {
        return when (currentUser.role) {
            Role.ADMIN -> true
            Role.TEACHER -> true // Assume teachers can view their students; in real app, check assignment
            Role.STUDENT -> currentUser.id == studentId
        }
    }

    private fun canViewAllStudents(): Boolean {
        return currentUser.role == Role.ADMIN || currentUser.role == Role.TEACHER
    }
}