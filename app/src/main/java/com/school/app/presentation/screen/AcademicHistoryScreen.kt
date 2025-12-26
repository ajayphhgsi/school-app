package com.school.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.school.app.domain.model.AcademicRecord
import com.school.app.presentation.viewmodel.AcademicHistoryViewModel
import com.school.app.presentation.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicHistoryScreen(
    studentId: String,
    viewModel: AcademicHistoryViewModel = viewModel()
) {
    val records by viewModel.filteredRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadAcademicRecords(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Academic History") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Filters and Sort
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = selectedSemester ?: "",
                    onValueChange = { viewModel.setSemesterFilter(it.takeIf { it.isNotBlank() }) },
                    label = { Text("Semester") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = selectedYear?.toString() ?: "",
                    onValueChange = { viewModel.setYearFilter(it.toIntOrNull()) },
                    label = { Text("Year") },
                    modifier = Modifier.weight(1f)
                )
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = when (sortOption) {
                            SortOption.YEAR_DESC -> "Year Desc"
                            SortOption.YEAR_ASC -> "Year Asc"
                            SortOption.SUBJECT_ASC -> "Subject Asc"
                            SortOption.GRADE_DESC -> "Grade Desc"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sort") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Year Desc") },
                            onClick = {
                                viewModel.setSortOption(SortOption.YEAR_DESC)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Year Asc") },
                            onClick = {
                                viewModel.setSortOption(SortOption.YEAR_ASC)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Subject Asc") },
                            onClick = {
                                viewModel.setSortOption(SortOption.SUBJECT_ASC)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Grade Desc") },
                            onClick = {
                                viewModel.setSortOption(SortOption.GRADE_DESC)
                                expanded = false
                            }
                        )
                    }
                }
            }

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                }
                error != null -> {
                    Text(text = "Error: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(records) { record ->
                            AcademicRecordItem(record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AcademicRecordItem(record: AcademicRecord) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Subject: ${record.subject}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Grade: ${record.grade}")
            Text(text = "Semester: ${record.semester}")
            Text(text = "Year: ${record.year}")
            record.gpa?.let { Text(text = "GPA: $it") }
        }
    }
}