package com.example.ui.screens.subtabs

import android.content.res.Configuration
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entities.MedicalGalleryEntity
import com.example.ui.components.GalleryCaseVideoCallDialog
import java.io.File
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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

    val galleryList by viewModel.galleryList.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
                        text = if (isLandscape) { { Text("Vitals Records", fontSize = 12.sp, fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Normal) } } else null,
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
                        text = if (isLandscape) { { Text("Medication Records", fontSize = 12.sp, fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Normal) } } else null,
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
                        text = if (isLandscape) { { Text("Daily Activities", fontSize = 12.sp, fontWeight = if (selectedSubTab == 2) FontWeight.Bold else FontWeight.Normal) } } else null,
                        icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "View Activities", modifier = Modifier.size(18.dp)) },
                        selectedContentColor = SkyLight,
                        unselectedContentColor = Color(0xFF94A3B8),
                        modifier = Modifier.testTag("subtab_view_activities")
                    )
                }
                Tab(
                    selected = selectedSubTab == 3,
                    onClick = { viewModel.setViewRecordsSubTab(3) },
                    text = if (isLandscape) { { Text("Medical Gallery", fontSize = 12.sp, fontWeight = if (selectedSubTab == 3) FontWeight.Bold else FontWeight.Normal) } } else null,
                    icon = { Icon(Icons.Default.Collections, contentDescription = "View Gallery", modifier = Modifier.size(18.dp)) },
                    selectedContentColor = SkyLight,
                    unselectedContentColor = Color(0xFF94A3B8),
                    modifier = Modifier.testTag("subtab_view_gallery")
                )
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
                3 -> ViewGallerySubTab(
                    galleryItems = galleryList,
                    viewModel = viewModel,
                    onDeleteItem = { id -> viewModel.deleteGalleryImage(id) }
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

@Composable
private fun ViewGallerySubTab(
    galleryItems: List<MedicalGalleryEntity>,
    viewModel: PortalViewModel,
    onDeleteItem: (Long) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf("All Categories") }
    var viewingItem by remember { mutableStateOf<MedicalGalleryEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<MedicalGalleryEntity?>(null) }
    var videoCallTargetItem by remember { mutableStateOf<MedicalGalleryEntity?>(null) }
    var showVideoCallDialog by remember { mutableStateOf(false) }

    val categories = listOf("All Categories") + galleryItems.map { it.category }.distinct()
    val filteredItems = if (selectedCategoryFilter == "All Categories") {
        galleryItems
    } else {
        galleryItems.filter { it.category == selectedCategoryFilter }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("view_gallery_subtab")
    ) {
        // Gallery Header Summary Banner with Video Call Action
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2544)),
                modifier = Modifier.fillMaxWidth().testTag("gallery_view_header_card")
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
                            shape = CircleShape,
                            color = Color(0xFF1E3A5F),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Collections,
                                    contentDescription = null,
                                    tint = SkyLight,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Medical & Clinical Gallery",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${galleryItems.size} total clinical attachments",
                                fontSize = 11.5.sp,
                                color = SkyLight
                            )
                        }
                    }

                    Button(
                        onClick = {
                            videoCallTargetItem = galleryItems.firstOrNull()
                            showVideoCallDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("view_gallery_start_call_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("3-Way Video Call", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Category Filter Chips
        if (categories.size > 2) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategoryFilter == category
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NavyPrimary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.clickable { selectedCategoryFilter = category }
                        ) {
                            Text(
                                text = category,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        if (filteredItems.isEmpty()) {
            item {
                EmptyStateCard(
                    title = if (galleryItems.isEmpty()) "No Medical Images Uploaded" else "No records in this category",
                    subtitle = if (galleryItems.isEmpty()) "Uploaded clinical photos, prescriptions, and lab scans from the Add Records > Medical Gallery tab will appear here." else "Try selecting another category filter above."
                )
            }
        } else {
            item {
                Text(
                    text = "Medical Attachments & Scans (${filteredItems.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            // Grid items chunked in pairs for LazyColumn
            val chunkedItems = filteredItems.chunked(2)
            items(chunkedItems) { rowPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (item in rowPair) {
                        Box(modifier = Modifier.weight(1f)) {
                            ViewGalleryGridCard(
                                item = item,
                                onClick = { viewingItem = item }
                            )
                        }
                    }
                    if (rowPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    // Fullscreen View Modal Dialog
    viewingItem?.let { item ->
        Dialog(onDismissRequest = { viewingItem = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewingItem = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Big Image Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(getViewCategoryGradient(item.category)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.imageUri.startsWith("/") || item.imageUri.startsWith("file://") || item.imageUri.startsWith("content://")) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(if (item.imageUri.startsWith("/")) File(item.imageUri) else item.imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = item.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            ViewClinicalArtIllustration(
                                category = item.category,
                                title = item.title
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getViewCategoryPillColor(item.category)
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = getViewCategoryTextColor(item.category),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val formatted = if (item.formattedDate.isNotEmpty()) {
                        item.formattedDate
                    } else {
                        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
                    }

                    Text(
                        text = "📅 Recorded: $formatted",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "👤 Logged by: ${item.loggedByName} (${item.loggedByRole})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )

                    if (item.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Clinical Notes / Observation:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(item.notes, fontSize = 12.5.sp, color = NavyPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                videoCallTargetItem = item
                                viewingItem = null
                                showVideoCallDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("view_modal_discuss_in_video_call")
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Discuss in 3-Way Call", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row {
                            OutlinedButton(
                                onClick = {
                                    viewingItem = null
                                    itemToDelete = item
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthCriticalRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", fontSize = 11.5.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = { viewingItem = null },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Close", fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Live 3-Way Video Consultation Dialog
    if (showVideoCallDialog) {
        GalleryCaseVideoCallDialog(
            initialGalleryItem = videoCallTargetItem,
            allGalleryItems = galleryItems,
            viewModel = viewModel,
            onDismiss = {
                showVideoCallDialog = false
                videoCallTargetItem = null
            }
        )
    }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Gallery Image?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${item.title}' from the patient's medical gallery?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteItem(item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthCriticalRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ViewGalleryGridCard(
    item: MedicalGalleryEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("view_gallery_card_${item.id}")
    ) {
        Column {
            // Image / Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(getViewCategoryGradient(item.category)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUri.startsWith("/") || item.imageUri.startsWith("file://") || item.imageUri.startsWith("content://")) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(if (item.imageUri.startsWith("/")) File(item.imageUri) else item.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    ViewClinicalArtIllustration(
                        category = item.category,
                        title = item.title,
                        isCompact = true
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.ZoomIn,
                            contentDescription = "Zoom",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = getViewCategoryPillColor(item.category)
                ) {
                    Text(
                        text = item.category.split(" ").first(),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = getViewCategoryTextColor(item.category),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "By ${item.loggedByRole}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = item.formattedDate.ifEmpty { SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(Date(item.timestamp)) },
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun ViewClinicalArtIllustration(
    category: String,
    title: String,
    isCompact: Boolean = false
) {
    val icon = when {
        category.contains("Rx") || category.contains("Prescription") -> Icons.Default.Medication
        category.contains("Wound") -> Icons.Default.Healing
        category.contains("Lab") -> Icons.Default.Science
        category.contains("Therapy") -> Icons.Default.FitnessCenter
        category.contains("Diet") -> Icons.Default.Restaurant
        else -> Icons.Default.Description
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(if (isCompact) 32.dp else 52.dp)
        )
        if (!isCompact) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Clinical Verification Record",
                fontSize = 11.sp,
                color = SkyLight,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getViewCategoryGradient(category: String): Brush {
    return when {
        category.contains("Rx") || category.contains("Prescription") -> Brush.linearGradient(
            listOf(Color(0xFF0284C7), Color(0xFF0369A1))
        )
        category.contains("Wound") -> Brush.linearGradient(
            listOf(Color(0xFFE11D48), Color(0xFF9F1239))
        )
        category.contains("Lab") -> Brush.linearGradient(
            listOf(Color(0xFF7C3AED), Color(0xFF5B21B6))
        )
        category.contains("Therapy") -> Brush.linearGradient(
            listOf(Color(0xFF0D9488), Color(0xFF0F766E))
        )
        category.contains("Diet") -> Brush.linearGradient(
            listOf(Color(0xFFD97706), Color(0xFFB45309))
        )
        else -> Brush.linearGradient(
            listOf(NavyPrimary, NavyDark)
        )
    }
}

private fun getViewCategoryPillColor(category: String): Color {
    return when {
        category.contains("Rx") || category.contains("Prescription") -> Color(0xFFE0F2FE)
        category.contains("Wound") -> Color(0xFFFEE2E2)
        category.contains("Lab") -> Color(0xFFEDE9FE)
        category.contains("Therapy") -> Color(0xFFCCFBF1)
        category.contains("Diet") -> Color(0xFFFEF3C7)
        else -> Color(0xFFF1F5F9)
    }
}

private fun getViewCategoryTextColor(category: String): Color {
    return when {
        category.contains("Rx") || category.contains("Prescription") -> Color(0xFF0369A1)
        category.contains("Wound") -> Color(0xFFBE123C)
        category.contains("Lab") -> Color(0xFF6D28D9)
        category.contains("Therapy") -> Color(0xFF0F766E)
        category.contains("Diet") -> Color(0xFF92400E)
        else -> NavyPrimary
    }
}

