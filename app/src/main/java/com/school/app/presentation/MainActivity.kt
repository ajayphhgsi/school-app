package com.school.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.school.app.presentation.screen.AcademicHistoryScreen
import com.school.app.presentation.screen.AttendanceMarkingScreen
import com.school.app.presentation.screen.AttendanceViewScreen
import com.school.app.presentation.screen.FeesScreen
import com.school.app.presentation.screen.GradeManagementScreen
import com.school.app.presentation.screen.StudentDetailsScreen
import com.school.app.presentation.screen.StudentListScreen
import com.school.app.presentation.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "student_list") {
                        composable("student_list") {
                            StudentListScreen(onStudentSelected = { studentId ->
                                navController.navigate("student_details/$studentId")
                            })
                        }
                        composable("student_details/{studentId}") { backStackEntry ->
                            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                            StudentDetailsScreen(studentId = studentId, navController = navController)
                        }
                        composable("fees") {
                            FeesScreen()
                        }
                        composable("academic_history/{studentId}") { backStackEntry ->
                            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                            AcademicHistoryScreen(studentId = studentId)
                        }
                        composable("attendance_marking") {
                            // TODO: Pass currentUser
                            AttendanceMarkingScreen(currentUser = User("", "", "", Role.TEACHER, "")) // Placeholder
                        }
                        composable("attendance_view") {
                            // TODO: Pass currentUser
                            AttendanceViewScreen(currentUser = User("", "", "", Role.STUDENT, "")) // Placeholder
                        }
                        composable("grade_management") {
                            // TODO: Pass currentUser
                            GradeManagementScreen(currentUser = User("", "", "", Role.TEACHER, "")) // Placeholder
                        }
                    }
                }
            }
        }
    }
}