package com.school.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.app.domain.model.AcademicRecord
import com.school.app.domain.model.Student
import com.school.app.domain.model.User
import com.school.app.domain.usecase.AddAcademicRecordUseCase
import com.school.app.domain.usecase.GetStudentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class GradeManagementViewModel(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val addAcademicRecordUseCase: AddAcademicRecordUseCase,
    private val currentUser: User
) : ViewModel() {

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

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
            } catch (e: Exception) {
                _message.value = "Failed to load students: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addGrade(studentId: String, subject: String, grade: String, semester: String, year: Int, gpa: Double?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val record = AcademicRecord(
                    id = UUID.randomUUID().toString(),
                    studentId = studentId,
                    subject = subject,
                    grade = grade,
                    semester = semester,
                    year = year,
                    gpa = gpa
                )
                val success = addAcademicRecordUseCase(record, currentUser)
                if (success) {
                    _message.value = "Grade added successfully"
                } else {
                    _message.value = "Failed to add grade"
                }
            } catch (e: Exception) {
                _message.value = "Failed to add grade: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}