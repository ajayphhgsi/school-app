package com.school.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.school.app.domain.model.User
import com.school.app.presentation.viewmodel.GradeManagementViewModel

@Composable
fun GradeManagementScreen(
    currentUser: User,
    viewModel: GradeManagementViewModel = viewModel { GradeManagementViewModel(/* dependencies */) } // TODO: inject properly
) {
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    var selectedStudentId by remember { mutableStateOf<String?>(null) }
    var subject by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var gpa by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Manage Grades", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Student selection
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = students.find { it.id == selectedStudentId }?.name ?: "Select Student",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                students.forEach { student ->
                    DropdownMenuItem(
                        text = { Text(student.name) },
                        onClick = {
                            selectedStudentId = student.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = grade,
            onValueChange = { grade = it },
            label = { Text("Grade") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = semester,
            onValueChange = { semester = it },
            label = { Text("Semester") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = year,
            onValueChange = { year = it },
            label = { Text("Year") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = gpa,
            onValueChange = { gpa = it },
            label = { Text("GPA (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedStudentId?.let {
                    val gpaValue = gpa.toDoubleOrNull()
                    val yearValue = year.toIntOrNull() ?: 0
                    viewModel.addGrade(it, subject, grade, semester, yearValue, gpaValue)
                    // Clear fields
                    subject = ""
                    grade = ""
                    semester = ""
                    year = ""
                    gpa = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedStudentId != null && subject.isNotBlank() && grade.isNotBlank()
        ) {
            Text("Add Grade")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        message?.let {
            Text(it, color = if (it.contains("successfully")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }
    }
}