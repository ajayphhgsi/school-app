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
import com.school.app.domain.model.AttendanceStatus
import com.school.app.domain.model.User
import com.school.app.presentation.viewmodel.AttendanceMarkingViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceMarkingScreen(
    currentUser: User,
    viewModel: AttendanceMarkingViewModel = viewModel { AttendanceMarkingViewModel(/* dependencies */) } // TODO: inject properly
) {
    val students by viewModel.students.collectAsState()
    val attendanceMap by viewModel.attendanceMap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    var selectedDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mark Attendance", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.markAttendance(selectedDate) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mark Attendance")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        message?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            LaunchedEffect(it) {
                // Clear message after delay
            }
        }

        LazyColumn {
            items(students) { student ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(student.name, modifier = Modifier.weight(1f))
                    AttendanceStatusDropdown(
                        currentStatus = attendanceMap[student.id] ?: AttendanceStatus.PRESENT,
                        onStatusChange = { viewModel.updateAttendance(student.id, it) }
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceStatusDropdown(
    currentStatus: AttendanceStatus,
    onStatusChange: (AttendanceStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = AttendanceStatus.values()

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(currentStatus.name)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            statuses.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.name) },
                    onClick = {
                        onStatusChange(status)
                        expanded = false
                    }
                )
            }
        }
    }
}