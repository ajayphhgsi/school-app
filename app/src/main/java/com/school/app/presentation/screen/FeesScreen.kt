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
import com.school.app.domain.model.FeeStatus
import com.school.app.presentation.viewmodel.FeesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesScreen(
    viewModel: FeesViewModel = viewModel()
) {
    val fees by viewModel.filteredFees.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Fees", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Filters and Sorting
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Status Filter
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = statusFilter?.name ?: "All Statuses",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filter by Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().weight(1f)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Statuses") },
                        onClick = {
                            viewModel.setStatusFilter(null)
                            expanded = false
                        }
                    )
                    FeeStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.name) },
                            onClick = {
                                viewModel.setStatusFilter(status)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Sort By
            var sortExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = !sortExpanded }
            ) {
                TextField(
                    value = when (sortBy) {
                        FeesViewModel.SortOption.DUE_DATE -> "Due Date"
                        FeesViewModel.SortOption.AMOUNT -> "Amount"
                        FeesViewModel.SortOption.STATUS -> "Status"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sort by") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                    modifier = Modifier.menuAnchor().weight(1f)
                )
                ExposedDropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    FeesViewModel.SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(
                                when (option) {
                                    FeesViewModel.SortOption.DUE_DATE -> "Due Date"
                                    FeesViewModel.SortOption.AMOUNT -> "Amount"
                                    FeesViewModel.SortOption.STATUS -> "Status"
                                }
                            ) },
                            onClick = {
                                viewModel.setSortBy(option)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(fees) { fee ->
                    FeeItem(fee)
                }
            }
        }
    }
}

@Composable
fun FeeItem(fee: com.school.app.domain.model.Fee) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Fee Type: ${fee.type}")
            Text("Amount: $${fee.amount}")
            Text("Due Date: ${fee.dueDate}")
            Text("Status: ${fee.status.name}")
        }
    }
}