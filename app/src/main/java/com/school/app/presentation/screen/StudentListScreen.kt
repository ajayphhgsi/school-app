package com.school.app.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.school.app.domain.model.Student
import com.school.app.presentation.viewmodel.StudentDetailsViewModel

@Composable
fun StudentListScreen(
    onStudentSelected: (String) -> Unit,
    viewModel: StudentDetailsViewModel = viewModel()
) {
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStudents()
    }

    when {
        isLoading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        error != null -> {
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(students) { student ->
                    StudentListItem(student = student, onClick = { onStudentSelected(student.id) })
                }
            }
        }
    }
}

@Composable
fun StudentListItem(student: Student, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = student.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Grade: ${student.grade}")
            Text(text = "Email: ${student.email}")
        }
    }
}