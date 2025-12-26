package com.school.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.school.app.domain.model.Fee
import com.school.app.domain.model.AcademicRecord
import com.school.app.domain.model.Expense
import com.school.app.presentation.viewmodel.StudentDetailsViewModel

@Composable
fun StudentDetailsScreen(
    studentId: String,
    navController: NavController,
    viewModel: StudentDetailsViewModel = viewModel()
) {
    val student by viewModel.student.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadStudent(studentId)
    }

    when {
        isLoading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        error != null -> {
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
        }
        student != null -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                item {
                    Text(text = "Name: ${student!!.name}", style = MaterialTheme.typography.headlineMedium)
                    Text(text = "ID: ${student!!.id}")
                    Text(text = "Grade: ${student!!.grade}")
                    Text(text = "Email: ${student!!.email}")
                    Text(text = "Phone: ${student!!.phone}")
                    Text(text = "Address: ${student!!.address}")
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(text = "Fees", style = MaterialTheme.typography.headlineSmall)
                }
                items(student!!.fees) { fee ->
                    FeeItem(fee)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Academic History", style = MaterialTheme.typography.headlineSmall)
                        Button(onClick = { navController.navigate("academic_history/$studentId") }) {
                            Text("View Full History")
                        }
                    }
                }
                items(student!!.academicHistory.take(3)) { record -> // Show only first 3
                    AcademicRecordItem(record)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Expenses", style = MaterialTheme.typography.headlineSmall)
                }
                items(student!!.expenses) { expense ->
                    ExpenseItem(expense)
                }
            }
        }
        else -> {
            Text(text = "Student not found")
        }
    }
}

@Composable
fun FeeItem(fee: Fee) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Amount: $${fee.amount}")
            Text(text = "Due Date: ${fee.dueDate}")
            Text(text = "Status: ${fee.status}")
        }
    }
}

@Composable
fun AcademicRecordItem(record: AcademicRecord) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Subject: ${record.subject}")
            Text(text = "Grade: ${record.grade}")
            Text(text = "Semester: ${record.semester}")
            Text(text = "Year: ${record.year}")
            record.gpa?.let { Text(text = "GPA: $it") }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Description: ${expense.description}")
            Text(text = "Amount: $${expense.amount}")
            Text(text = "Date: ${expense.date}")
        }
    }
}