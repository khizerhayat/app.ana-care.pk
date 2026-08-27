package com.example.ui.screens.subtabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DailyActivityEntity
import com.example.data.local.entities.MedicationEntity
import com.example.data.local.entities.VitalSignEntity
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.PortalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ViewRecordsScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSubTab by viewModel.viewRecordsSubTab.collectAsState()
    val vitalsList by viewModel.vitalsList.collectAsState()
    val medicationsList by viewModel.runningMedicationsList.collectAsState()
    val activitiesList by viewModel.activitiesList.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val patientAlertNotes by viewModel.patientAlertNotes.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("view_records_screen")
    ) {
        // Patient Custom Greeting / Banner configured live by Admin
        if (appConfig.patientGreetingMessage.isNotBlank()) {
            Surface(
                color = NavyDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👋 ${appConfig.patientGreetingMessage}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        // Sub Tabs Header Bar
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = NavyPrimary,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                        color = SkyLight,
                        height = 3.dp
                    )
                }
            ) {
                if (appConfig.showVitalsSummary) {
                    Tab(
                        selected = selectedSubTab == 0,
                        onClick = { viewModel.setViewRecordsSubTab(0) },
                        text = { Text("Vitals Records", fontSize = 12.sp, fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "View Vitals", modifier = Modifier.size(18.dp)) },
                        selectedContentColor = SkyLight,
                        unselectedContentColor = Color(0xFF94A3B8),
                        modifier = Modifier.testTag("subtab_view_vitals")
                    )
                }
                if (appConfig.showMedicationSection) {
                    Tab(
                        selected = selectedSubTab == 1,
                        onClick = { viewModel.setViewRecordsSubTab(1) },
                        text = { Text("Medication Records", fontSize = 12.sp, fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.Medication, contentDescription = "View Medications", modifier = Modifier.size(18.dp)) },
                        selectedContentColor = SkyLight,
                        unselectedContentColor = Color(0xFF94A3B8),
                        modifier = Modifier.testTag("subtab_view_medications")
                    )
                }
                if (appConfig.showDailyActivities) {
                    Tab(
                        selected = selectedSubTab == 2,
                        onClick = { viewModel.setViewRecordsSubTab(2) },
                        text = { Text("Daily Activities", fontSize = 12.sp, fontWeight = if (selectedSubTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "View Activities", modifier = Modifier.size(18.dp)) },
                        selectedContentColor = SkyLight,
                        unselectedContentColor = Color(0xFF94A3B8),
                        modifier = Modifier.testTag("subtab_view_activities")
                    )
                }
            }
        }

        // Sub Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            when (selectedSubTab) {
                0 -> ViewVitalsSubTab(
                    vitals = vitalsList,
                    onExportPdf = { viewModel.exportVitalsPdfReport() }
                )
                1 -> ViewMedicationsSubTab(
                    medications = medicationsList,
                    onMarkTaken = { id -> viewModel.markMedicationTaken(id) },
                    onMarkSkipped = { id -> viewModel.markMedicationSkipped(id) },
                    onExportPdf = { viewModel.exportMedicationsPdfReport() }
                )
                2 -> ViewActivitiesSubTab(
                    activities = activitiesList,
                    onExportPdf = { viewModel.exportActivitiesPdfReport() }
                )
            }
        }
    }
}


@Composable
private fun ViewVitalsSubTab(
    vitals: List<VitalSignEntity>,
    onExportPdf: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault())

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // PDF Export Banner Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2544)),
                modifier = Modifier.fillMaxWidth().testTag("vitals_pdf_export_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDC2626),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Official Vitals Report", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${vitals.size} logged sessions • AES Encrypted", fontSize = 11.sp, color = Color(0xFF93C5FD))
                        }
                    }

                    Button(
                        onClick = onExportPdf,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyLight, contentColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("export_vitals_pdf_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export as PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Summary Metric Strip with BP, HR, SpO2, Glucose labels
        if (vitals.isNotEmpty()) {
            item {
                val latest = vitals.first()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricHighlightCard("BP", "${latest.systolicBp}/${latest.diastolicBp}", "mmHg", Modifier.weight(1f))
                    MetricHighlightCard("HR", "${latest.heartRate}", "bpm", Modifier.weight(1f))
                    MetricHighlightCard("SpO2", "${latest.oxygenSaturation}", "%", Modifier.weight(1f))
                    MetricHighlightCard("Glucose", "${latest.bloodGlucose}", "mg/dL", Modifier.weight(1f))
                }
            }
        }

        if (vitals.isEmpty()) {
            item {
                EmptyStateCard("No Vital Signs Recorded", "Use the 'Add Vitals' tab above to securely record your first measurement.")
            }
        } else {
            item {
                // Table of Vitals: Sr. No., Date/time, BP, HR, Temp., Pulse, Resp., Glucose
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vitals_table_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .background(NavyPrimary)
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = "Sr. No.", width = 56.dp, isHeader = true)
                            TableCell(text = "Date/time", width = 130.dp, isHeader = true)
                            TableCell(text = "BP", width = 76.dp, isHeader = true)
                            TableCell(text = "HR", width = 60.dp, isHeader = true)
                            TableCell(text = "Temp.", width = 68.dp, isHeader = true)
                            TableCell(text = "Pulse", width = 60.dp, isHeader = true)
                            TableCell(text = "Resp.", width = 60.dp, isHeader = true)
                            TableCell(text = "Glucose", width = 74.dp, isHeader = true)
                        }

                        // Data Rows
                        vitals.forEachIndexed { index, item ->
                            val isEven = index % 2 == 0
                            Row(
                                modifier = Modifier
                                    .background(if (isEven) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(text = "${index + 1}", width = 56.dp)
                                TableCell(text = dateFormat.format(Date(item.timestamp)), width = 130.dp)
                                TableCell(text = "${item.systolicBp}/${item.diastolicBp}", width = 76.dp, isBold = true)
                                TableCell(text = "${item.heartRate}", width = 60.dp)
                                TableCell(text = "${item.temperatureF}°F", width = 68.dp)
                                TableCell(text = "${item.heartRate}", width = 60.dp)
                                TableCell(text = "${item.respiratoryRate}", width = 60.dp)
                                TableCell(text = "${item.bloodGlucose}", width = 74.dp)
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false,
    isBold: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontSize = if (isHeader) 11.sp else 11.5.sp,
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        color = if (isHeader) Color.White else NavyPrimary,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun ViewMedicationsSubTab(
    medications: List<MedicationEntity>,
    onMarkTaken: (Long) -> Unit,
    onMarkSkipped: (Long) -> Unit,
    onExportPdf: () -> Unit
) {
    val takenCount = medications.count { it.lastAction == "TAKEN" || it.isTakenToday }
    val progress = if (medications.isNotEmpty()) takenCount.toFloat() / medications.size else 0f

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // PDF Export Banner Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2544)),
                modifier = Modifier.fillMaxWidth().testTag("medications_pdf_export_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDC2626),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Medication Schedule PDF", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${medications.size} Running Prescriptions", fontSize = 11.sp, color = Color(0xFF93C5FD))
                        }
                    }

                    Button(
                        onClick = onExportPdf,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyLight, contentColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("export_medications_pdf_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export as PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Daily Adherence Progress
        if (medications.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Today's Medication Adherence", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Text("$takenCount of ${medications.size} taken", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealAccent)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = if (progress == 1f) HealthNormalGreen else TealAccent,
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        if (medications.isEmpty()) {
            item {
                EmptyStateCard(
                    "No Running Medications",
                    "All medicines are stopped or none registered. Manage medicine names, dosages, and running/stop status in Profile."
                )
            }
        } else {
            items(medications, key = { it.id }) { med ->
                MedicationRecordCard(
                    item = med,
                    onMarkTaken = { onMarkTaken(med.id) },
                    onMarkSkipped = { onMarkSkipped(med.id) }
                )
            }
        }
    }
}

@Composable
private fun MedicationRecordCard(
    item: MedicationEntity,
    onMarkTaken: () -> Unit,
    onMarkSkipped: () -> Unit
) {
    val isTaken = item.lastAction == "TAKEN" || item.isTakenToday
    val isSkipped = item.lastAction == "SKIPPED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("med_record_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isTaken -> Color(0xFFF0FDF4)
                isSkipped -> Color(0xFFFFFBEB)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
                isTaken -> Color(0xFF86EFAC)
                isSkipped -> Color(0xFFFDE68A)
                else -> Color(0xFFE2E8F0)
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = SkyLight
                    ) {
                        Text(
                            text = item.dosage,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE0F2FE)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = item.scheduledTime,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${item.frequency} • ${item.route}",
                fontSize = 12.sp,
                color = Color(0xFF475569)
            )

            if (item.instructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Instructions: ${item.instructions}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action status badge with auto-saved date/time
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = when {
                    isTaken -> Color(0xFFDCFCE7)
                    isSkipped -> Color(0xFFFEF3C7)
                    else -> Color(0xFFF1F5F9)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when {
                            isTaken -> Icons.Default.CheckCircle
                            isSkipped -> Icons.Default.Cancel
                            else -> Icons.Default.History
                        },
                        contentDescription = null,
                        tint = when {
                            isTaken -> HealthNormalGreen
                            isSkipped -> HealthWarningAmber
                            else -> Color(0xFF64748B)
                        },
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = when {
                            isTaken -> "✓ Taken: ${item.lastActionDateFormatted.ifEmpty { "Today" }}"
                            isSkipped -> "⊘ Skipped: ${item.lastActionDateFormatted.ifEmpty { "Today" }}"
                            else -> "Pending dose"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            isTaken -> Color(0xFF166534)
                            isSkipped -> Color(0xFF92400E)
                            else -> Color(0xFF475569)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mark Taken / Skip Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkTaken,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTaken) Color(0xFF059669) else HealthNormalGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("view_mark_taken_${item.id}")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTaken) "Taken ✓" else "Mark Taken",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onMarkSkipped,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSkipped) Color(0xFFB45309) else Color(0xFFD97706)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("view_mark_skipped_${item.id}")
                ) {
                    Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFD97706))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSkipped) "Skipped ⊘" else "Skip Dose",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rx by: ${item.prescribedBy}",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "${item.refillsRemaining} refills left",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.refillsRemaining <= 1) HealthWarningAmber else Color(0xFF047857)
                )
            }
        }
    }
}


@Composable
private fun ViewActivitiesSubTab(
    activities: List<DailyActivityEntity>,
    onExportPdf: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault())

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // PDF Export Banner Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2544)),
                modifier = Modifier.fillMaxWidth().testTag("activities_pdf_export_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDC2626),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Activities & Mobility Report", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${activities.size} Logged Wellness Sessions", fontSize = 11.sp, color = Color(0xFF93C5FD))
                        }
                    }

                    Button(
                        onClick = onExportPdf,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyLight, contentColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("export_activities_pdf_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export as PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (activities.isEmpty()) {
            item {
                EmptyStateCard("No Activities Logged", "Track physical therapy, walking steps, sleep, and pain levels in the 'Add Vitals -> Daily Activities' tab.")
            }
        } else {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activities_table_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .background(NavyPrimary)
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = "Sr. No.", width = 56.dp, isHeader = true)
                            TableCell(text = "Date/time", width = 130.dp, isHeader = true)
                            TableCell(text = "Activity", width = 120.dp, isHeader = true)
                            TableCell(text = "Metric", width = 100.dp, isHeader = true)
                            TableCell(text = "Duration", width = 76.dp, isHeader = true)
                            TableCell(text = "Pain", width = 56.dp, isHeader = true)
                            TableCell(text = "Mood", width = 70.dp, isHeader = true)
                            TableCell(text = "Notes", width = 140.dp, isHeader = true)
                        }

                        // Data Rows
                        activities.forEachIndexed { index, item ->
                            val isEven = index % 2 == 0
                            Row(
                                modifier = Modifier
                                    .background(if (isEven) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(text = "${index + 1}", width = 56.dp)
                                TableCell(text = dateFormat.format(Date(item.timestamp)), width = 130.dp)
                                TableCell(text = item.activityType, width = 120.dp, isBold = true)
                                TableCell(text = item.metricValue, width = 100.dp)
                                TableCell(text = if (item.durationMinutes > 0) "${item.durationMinutes} min" else "-", width = 76.dp)
                                TableCell(text = "${item.painScore}/10", width = 56.dp)
                                TableCell(text = item.mood, width = 70.dp)
                                TableCell(text = item.notes.ifEmpty { "-" }, width = 140.dp)
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun MetricHighlightCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TealAccent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Text(
                text = unit,
                fontSize = 9.5.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
