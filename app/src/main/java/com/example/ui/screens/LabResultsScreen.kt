package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.LabResultEntity
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.PortalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LabResultsScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val labResults by viewModel.labResultsList.collectAsState()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    var showAddLabDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("lab_results_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row with Title and + Add Lab Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Diagnostic Lab Reports",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    Button(
                        onClick = { showAddLabDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_lab_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Lab", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Lab", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (labResults.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No Diagnostic Reports Found", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Text("Tap '+ Add Lab' above to enter a new lab result.", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            } else {
                items(labResults, key = { it.id }) { report ->
                    LabReportItemCard(report = report, dateString = dateFormat.format(Date(report.datePerformed)))
                }
            }
        }

        // Add Lab Dialog
        if (showAddLabDialog) {
            AddLabDialog(
                onDismiss = { showAddLabDialog = false },
                onAdd = { testName, category, status, summary, parameters, notes ->
                    viewModel.recordLabResult(
                        testName = testName,
                        category = category,
                        status = status,
                        summary = summary,
                        keyParameters = parameters,
                        doctorNotes = notes
                    )
                    showAddLabDialog = false
                }
            )
        }
    }
}

@Composable
private fun AddLabDialog(
    onDismiss: () -> Unit,
    onAdd: (testName: String, category: String, status: String, summary: String, parameters: String, notes: String) -> Unit
) {
    var testName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Blood Chemistry") }
    var status by remember { mutableStateOf("NORMAL") }
    var summary by remember { mutableStateOf("") }
    var parameters by remember { mutableStateOf("") }
    var doctorNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Lab Diagnostic Report", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = NavyPrimary)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = testName,
                    onValueChange = { testName = it },
                    label = { Text("Test Name *") },
                    placeholder = { Text("e.g. Lipid Panel, HbA1c, CBC") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_lab_name_input")
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    placeholder = { Text("e.g. Biochemistry, Hematology") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("NORMAL", "ELEVATED", "CRITICAL").forEach { st ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (status == st) {
                                when (st) {
                                    "NORMAL" -> HealthNormalGreen
                                    "ELEVATED" -> HealthWarningAmber
                                    else -> HealthCriticalRed
                                }
                            } else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { status = st }
                        ) {
                            Text(
                                text = st,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (status == st) Color.White else Color(0xFF475569),
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Summary Findings") },
                    placeholder = { Text("e.g. All parameters within optimal limits.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = parameters,
                    onValueChange = { parameters = it },
                    label = { Text("Key Parameters & Values") },
                    placeholder = { Text("e.g. Glucose: 95 mg/dL, HbA1c: 5.4%") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (testName.isNotBlank()) {
                        onAdd(
                            testName.trim(),
                            category.trim(),
                            status,
                            summary.ifBlank { "Diagnostic lab test performed." },
                            parameters.trim(),
                            doctorNotes.trim()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("dialog_save_lab_button")
            ) {
                Text("Save Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun LabReportItemCard(
    report: LabResultEntity,
    dateString: String
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lab_report_card_${report.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.testName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "${report.category} • Date: $dateString",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (report.status) {
                        "NORMAL" -> Color(0xFFD1FAE5)
                        "ELEVATED" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFEE2E2)
                    }
                ) {
                    Text(
                        text = report.status,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (report.status) {
                            "NORMAL" -> Color(0xFF065F46)
                            "ELEVATED" -> HealthWarningAmber
                            else -> HealthCriticalRed
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clinical Summary Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Clinical Findings Summary:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(report.summary, fontSize = 12.sp, color = Color(0xFF1E293B))
                }
            }

            // Expandable Key Parameters Section
            if (expanded && report.keyParameters.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Detailed Blood Chemistry Parameters:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.keyParameters,
                            fontSize = 11.5.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (report.doctorNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = TealAccent, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Physician Interpretation: ${report.doctorNotes}",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ordered by: ${report.orderedBy}\nFacility: ${report.facility}",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Details",
                        tint = TealAccent
                    )
                }
            }
        }
    }
}
