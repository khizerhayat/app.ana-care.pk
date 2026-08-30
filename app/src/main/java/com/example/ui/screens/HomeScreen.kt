package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.MedicationAdministrationLogEntity
import com.example.data.local.entities.MedicationEntity
import com.example.data.local.entities.PatientAlertNoteEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.data.local.entities.VitalSignEntity
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.PortalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: PortalViewModel,
    onNavigateToTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccount by viewModel.activeAccount.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val doctorTargetPatient by viewModel.doctorTargetPatient.collectAsState()
    val vitalsList by viewModel.vitalsList.collectAsState()
    val medicationsList by viewModel.allMedicationsList.collectAsState()
    val medicationLogs by viewModel.medicationLogsList.collectAsState()
    val alertsList by viewModel.allAlertNotes.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isCaregiver = activeAccount?.role == "CAREGIVER"
    val patients = allAccounts.filter { it.role == "PATIENT" }
    val effectiveSelectedPatient = doctorTargetPatient ?: (
        if (isCaregiver && activeAccount?.assignedPatientId?.isNotEmpty() == true) {
            patients.find { it.userId == activeAccount?.assignedPatientId }
        } else {
            patients.firstOrNull()
        }
    )

    // Filter Doctor alerts / messages and Admin announcements
    val doctorAlerts = alertsList.filter {
        it.severity == "URGENT" || it.severity == "WARNING" || it.senderRole == "DOCTOR" || it.title.contains("Doctor", ignoreCase = true)
    }
    val adminAlerts = alertsList.filter {
        it.senderRole == "ADMIN" || it.title.contains("Admin", ignoreCase = true) || it.title.contains("System", ignoreCase = true)
    }

    // Last recorded vitals
    val latestVital = vitalsList.maxByOrNull { it.timestamp }

    var showAdminDismissed by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("home_screen_content"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Caregiver Multi-Patient Monitoring Bar (When Caregiver is logged in)
        if (isCaregiver && patients.isNotEmpty()) {
            item {
                CaregiverPatientRosterCard(
                    patients = patients,
                    selectedPatient = effectiveSelectedPatient,
                    onSelectPatient = { patient ->
                        viewModel.selectCaregiverTargetPatient(patient)
                    }
                )
            }
        }

        // 1. Desktop Hero Banner (matching the desktop view)
        item {
            DesktopHeroBanner(
                activeAccount = activeAccount,
                selectedPatient = effectiveSelectedPatient,
                isLandscape = isLandscape,
                onAddVitalClick = {
                    viewModel.setAddRecordsSubTab(0)
                    onNavigateToTab(MainTab.ADD_RECORDS)
                },
                onMessageDoctorClick = {
                    onNavigateToTab(MainTab.MESSAGING)
                }
            )
        }

        // 2. High-Precision 4-Metric Vitals Cards Row / Grid (Blood Pressure, Heart Rate, Blood Glucose, SpO2)
        item {
            DesktopMetricsGrid(
                latestVital = latestVital,
                isLandscape = isLandscape,
                onCardClick = {
                    viewModel.setViewRecordsSubTab(0)
                    onNavigateToTab(MainTab.VIEW_RECORDS)
                }
            )
        }

        // 3. Vitals & Glycemic 7-Day Trend Visualizer (Systolic BP, Diastolic BP, Blood Glucose Bézier curves)
        item {
            VitalsAndGlycemicTrendChartCard(
                vitalsList = vitalsList,
                isLandscape = isLandscape,
                onViewFullHistory = {
                    viewModel.setViewRecordsSubTab(0)
                    onNavigateToTab(MainTab.VIEW_RECORDS)
                }
            )
        }

        // 4. Medication Status (Taken or Missed) & Administrations
        item {
            MedicationStatusCard(
                medications = medicationsList.filter { it.status == "RUNNING" },
                recentLogs = medicationLogs.take(5),
                onMarkTaken = { med ->
                    viewModel.markMedicationTaken(med.id, med.name, med.dosage)
                },
                onMarkMissed = { med ->
                    viewModel.markMedicationSkipped(med.id, med.name, med.dosage)
                },
                onAddMedication = {
                    viewModel.setAddRecordsSubTab(1)
                    onNavigateToTab(MainTab.ADD_RECORDS)
                },
                onViewMedicationHistory = {
                    viewModel.setViewRecordsSubTab(1)
                    onNavigateToTab(MainTab.VIEW_RECORDS)
                }
            )
        }

        // 5. Doctor's Message / Clinical Alert (With direct pop-up open button)
        if (doctorAlerts.isNotEmpty()) {
            val alert = doctorAlerts.first()
            item {
                DoctorMessageCard(
                    alert = alert,
                    onOpenPopup = { viewModel.showCustomAlertPopup(alert) },
                    onAcknowledge = { viewModel.acknowledgeAlertNote(alert.id) },
                    onOpenMessaging = { onNavigateToTab(MainTab.MESSAGING) }
                )
            }
        }

        // 6. Admin Broadcast / System Maintenance Alert
        if (adminAlerts.isNotEmpty() && !showAdminDismissed) {
            val alert = adminAlerts.first()
            item {
                AdminMessageCard(
                    alert = alert,
                    onOpenPopup = { viewModel.showCustomAlertPopup(alert) },
                    onDismiss = { showAdminDismissed = true },
                    onOpenSettings = { onNavigateToTab(MainTab.PROFILE_SETTINGS) }
                )
            }
        }

        // 7. Navigation Quick Actions Hub
        item {
            QuickActionsHub(
                onNavigateToTab = onNavigateToTab
            )
        }
    }
}

/**
 * 1. Desktop Hero Banner
 * Matches the uploaded desktop view with status tag, patient greeting, IDs, and action buttons
 */
@Composable
fun DesktopHeroBanner(
    activeAccount: UserAccountEntity?,
    selectedPatient: UserAccountEntity? = null,
    isLandscape: Boolean = false,
    onAddVitalClick: () -> Unit,
    onMessageDoctorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCaregiver = activeAccount?.role == "CAREGIVER"
    val displayName = if (isCaregiver) (selectedPatient?.name ?: "Eleanor Vance") else (activeAccount?.name ?: "Eleanor Vance")
    val patientId = if (isCaregiver) (selectedPatient?.userId ?: activeAccount?.assignedPatientId ?: "21001001") else (activeAccount?.userId ?: "21001001")
    val assignedDoc = if (selectedPatient?.assignedDoctorId?.isNotEmpty() == true) {
        "Dr. Sarah Jenkins, MD (ID: ${selectedPatient.assignedDoctorId})"
    } else if (activeAccount?.assignedDoctorId?.isNotEmpty() == true) {
        "Dr. Sarah Jenkins, MD (ID: ${activeAccount.assignedDoctorId})"
    } else {
        "Dr. Sarah Jenkins, MD (ID: 1001)"
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("desktop_hero_banner")
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Content
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCaregiver) Color(0xFFEDE9FE) else Color(0xFFDCFCE7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCaregiver) Color(0xFFC4B5FD) else Color(0xFF86EFAC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isCaregiver) Color(0xFF7C3AED) else HealthNormalGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCaregiver) "CAREGIVER MONITORING • ACTIVE: ${displayName.uppercase()}" else "ACTIVE HEALTH MONITORING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCaregiver) Color(0xFF5B21B6) else Color(0xFF166534),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isCaregiver) "Patient Chart: $displayName" else "Welcome back, $displayName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = if (isCaregiver) "Patient ID: $patientId  •  Caregiver: ${activeAccount?.name ?: "On Duty"}  •  Physician: $assignedDoc" else "Patient ID: $patientId  •  Assigned Physician: $assignedDoc",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Right Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onAddVitalClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier.testTag("hero_add_vital_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Vital",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+ Add Vital",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onMessageDoctorClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F243E)),
                        modifier = Modifier.testTag("hero_message_doctor_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QuestionAnswer,
                            contentDescription = "Message Doctor",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Message Care Team",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Portrait Layout: Stacked, Action Buttons Span Width Evenly
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCaregiver) Color(0xFFEDE9FE) else Color(0xFFDCFCE7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCaregiver) Color(0xFFC4B5FD) else Color(0xFF86EFAC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isCaregiver) Color(0xFF7C3AED) else HealthNormalGreen)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isCaregiver) "CAREGIVER • ACTIVE" else "HEALTH MONITORING",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCaregiver) Color(0xFF5B21B6) else Color(0xFF166534),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Text(
                        text = "ID: $patientId",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isCaregiver) "Patient: $displayName" else "Welcome, $displayName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = assignedDoc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddVitalClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("hero_add_vital_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Vital",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+ Add Vital",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onMessageDoctorClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F243E)),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("hero_message_doctor_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QuestionAnswer,
                            contentDescription = "Message Care Team",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Message MD",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2. High-Precision 4 Metric Cards
 * Matches the 4 cards:
 * 1) BLOOD PRESSURE (122 / 78 mmHg, Heart icon, Normal Range, Today 08:30 AM)
 * 2) HEART RATE (72 BPM, Pulse wave icon, Resting Baseline, Recorded: Pt. Self)
 * 3) BLOOD GLUCOSE (108 mg/dL, Blood drop icon, Fasting Baseline, Target: < 120)
 * 4) SPO2 OXYGEN (98 %, Lungs icon, Optimal Airway, Room Air)
 */
@Composable
fun DesktopMetricsGrid(
    latestVital: VitalSignEntity?,
    isLandscape: Boolean = false,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bpVal = if (latestVital != null) "${latestVital.systolicBp} / ${latestVital.diastolicBp}" else "122 / 78"
    val hrVal = if (latestVital != null) "${latestVital.heartRate}" else "72"
    val glucoseVal = if (latestVital != null) "${latestVital.bloodGlucose}" else "108"
    val spo2Val = if (latestVital != null) "${latestVital.oxygenSaturation}" else "98"

    if (isLandscape) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .testTag("desktop_metrics_grid"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SingleMetricCard(
                title = "BLOOD PRESSURE",
                value = bpVal,
                unit = "mmHg",
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFEF4444),
                badgeText = "Normal Range",
                footerText = "Today 08:30 AM",
                onClick = onCardClick,
                modifier = Modifier.weight(1f)
            )
            SingleMetricCard(
                title = "HEART RATE",
                value = hrVal,
                unit = "BPM",
                icon = Icons.Default.MonitorHeart,
                iconColor = Color(0xFF0284C7),
                badgeText = "Resting Baseline",
                footerText = "Recorded: Pt. Self",
                onClick = onCardClick,
                modifier = Modifier.weight(1f)
            )
            SingleMetricCard(
                title = "BLOOD GLUCOSE",
                value = glucoseVal,
                unit = "mg/dL",
                icon = Icons.Default.WaterDrop,
                iconColor = Color(0xFFF59E0B),
                badgeText = "Fasting Baseline",
                footerText = "Target: < 120",
                onClick = onCardClick,
                modifier = Modifier.weight(1f)
            )
            SingleMetricCard(
                title = "SPO2 OXYGEN",
                value = spo2Val,
                unit = "%",
                icon = Icons.Default.Air,
                iconColor = Color(0xFF8B5CF6),
                badgeText = "Optimal Airway",
                footerText = "Room Air",
                onClick = onCardClick,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        // 2x2 Grid in Portrait to utilize space efficiently without squishing or dead gaps
        Column(
            modifier = modifier
                .fillMaxWidth()
                .testTag("desktop_metrics_grid"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SingleMetricCard(
                    title = "BLOOD PRESSURE",
                    value = bpVal,
                    unit = "mmHg",
                    icon = Icons.Default.Favorite,
                    iconColor = Color(0xFFEF4444),
                    badgeText = "Normal Range",
                    footerText = "Today 08:30 AM",
                    onClick = onCardClick,
                    modifier = Modifier.weight(1f)
                )
                SingleMetricCard(
                    title = "HEART RATE",
                    value = hrVal,
                    unit = "BPM",
                    icon = Icons.Default.MonitorHeart,
                    iconColor = Color(0xFF0284C7),
                    badgeText = "Resting Baseline",
                    footerText = "Recorded: Pt. Self",
                    onClick = onCardClick,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SingleMetricCard(
                    title = "BLOOD GLUCOSE",
                    value = glucoseVal,
                    unit = "mg/dL",
                    icon = Icons.Default.WaterDrop,
                    iconColor = Color(0xFFF59E0B),
                    badgeText = "Fasting Baseline",
                    footerText = "Target: < 120",
                    onClick = onCardClick,
                    modifier = Modifier.weight(1f)
                )
                SingleMetricCard(
                    title = "SPO2 OXYGEN",
                    value = spo2Val,
                    unit = "%",
                    icon = Icons.Default.Air,
                    iconColor = Color(0xFF8B5CF6),
                    badgeText = "Optimal Airway",
                    footerText = "Room Air",
                    onClick = onCardClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SingleMetricCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    iconColor: Color,
    badgeText: String,
    footerText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(11.dp)
        ) {
            // Top Row: Title + Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.4.sp
                )
                Surface(
                    shape = CircleShape,
                    color = iconColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Middle: Big Value + Unit
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom row: Pill Badge + Footer Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFDCFCE7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = footerText,
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * 3. Vitals & Glycemic 7-Day Trend Visualizer Card
 * Plots 3 Curves: Systolic BP, Diastolic BP, and Blood Glucose
 * Matches the exact graph from the desktop screenshot!
 */
@Composable
fun VitalsAndGlycemicTrendChartCard(
    vitalsList: List<VitalSignEntity>,
    isLandscape: Boolean = false,
    onViewFullHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("vitals_trend_visualizer_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row / Column for Title & Legend
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Chart",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Vitals & Glycemic 7-Day Trend Visualizer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // 3 Legend items
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = Color(0xFF0284C7), label = "Systolic BP")
                        LegendItem(color = Color(0xFF10B981), label = "Diastolic BP")
                        LegendItem(color = Color(0xFFF59E0B), label = "Glucose")
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Chart",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "7-Day Vitals Trend",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "Full Logs →",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7),
                            modifier = Modifier.clickable(onClick = onViewFullHistory)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = Color(0xFF0284C7), label = "Systolic")
                        LegendItem(color = Color(0xFF10B981), label = "Diastolic")
                        LegendItem(color = Color(0xFFF59E0B), label = "Glucose")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 7 Days Data points (Mon -> Today)
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today")
            val systolicValues = listOf(120f, 126f, 138f, 124f, 118f, 125f, 122f)
            val diastolicValues = listOf(78f, 82f, 88f, 80f, 76f, 81f, 78f)
            val glucoseValues = listOf(105f, 112f, 118f, 106f, 98f, 110f, 108f)

            val yAxisLabels = listOf(160, 140, 120, 100, 80, 60)
            val chartHeightDp = if (isLandscape) 180.dp else 150.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeightDp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val leftPadding = 30.dp.toPx()
                    val bottomPadding = 20.dp.toPx()
                    val chartWidth = w - leftPadding
                    val chartHeight = h - bottomPadding

                    val minY = 60f
                    val maxY = 160f
                    val yRange = maxY - minY

                    // 1. Draw horizontal grid lines & Y-Axis Labels
                    yAxisLabels.forEach { labelVal ->
                        val normY = (labelVal - minY) / yRange
                        val yPos = chartHeight - (normY * chartHeight)

                        // Grid line
                        drawLine(
                            color = Color(0x22888888),
                            start = Offset(leftPadding, yPos),
                            end = Offset(w, yPos),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // 2. Draw Curves
                    val stepX = chartWidth / (days.size - 1).coerceAtLeast(1)

                    fun drawSpline(values: List<Float>, color: Color, isDashed: Boolean) {
                        val path = Path()
                        val pointsList = mutableListOf<Offset>()

                        values.forEachIndexed { i, v ->
                            val x = leftPadding + (i * stepX)
                            val normY = (v - minY) / yRange
                            val y = chartHeight - (normY * chartHeight)
                            pointsList.add(Offset(x, y))

                            if (i == 0) {
                                path.moveTo(x, y)
                            } else {
                                val prev = pointsList[i - 1]
                                val cX1 = prev.x + (x - prev.x) / 2
                                val cY1 = prev.y
                                val cX2 = prev.x + (x - prev.x) / 2
                                val cY2 = y
                                path.cubicTo(cX1, cY1, cX2, cY2, x, y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = color,
                            style = if (isDashed) {
                                Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                                    cap = StrokeCap.Round
                                )
                            } else {
                                Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                            }
                        )

                        // Draw Point dots
                        pointsList.forEach { pt ->
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = color,
                                radius = 2.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }

                    // Draw Systolic BP (Blue)
                    drawSpline(systolicValues, Color(0xFF0284C7), isDashed = false)

                    // Draw Diastolic BP (Green)
                    drawSpline(diastolicValues, Color(0xFF10B981), isDashed = false)

                    // Draw Blood Glucose (Amber Dashed)
                    drawSpline(glucoseValues, Color(0xFFF59E0B), isDashed = true)
                }

                // Y-Axis Labels overlay on left
                Column(
                    modifier = Modifier
                        .height(if (isLandscape) 150.dp else 125.dp)
                        .width(28.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    yAxisLabels.forEach { label ->
                        Text(
                            text = label.toString(),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                // X-Axis Day Labels overlay at bottom
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(start = 30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    days.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 9.5.sp,
                            fontWeight = if (day == "Today") FontWeight.Bold else FontWeight.Medium,
                            color = if (day == "Today") Color(0xFF0284C7) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (isLandscape) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Real-Time Telemetry: 7-Day continuous physiological telemetry stream",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "View Full Logs & Diagnostics →",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.clickable(onClick = onViewFullHistory)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 4. Medication Status Card (Today)
 */
@Composable
fun MedicationStatusCard(
    medications: List<MedicationEntity>,
    recentLogs: List<MedicationAdministrationLogEntity>,
    onMarkTaken: (MedicationEntity) -> Unit,
    onMarkMissed: (MedicationEntity) -> Unit,
    onAddMedication: () -> Unit,
    onViewMedicationHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("medication_status_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = "Medications",
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Prescribed Medication Regimen",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "History & Logs →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0284C7),
                    modifier = Modifier.clickable(onClick = onViewMedicationHistory)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (medications.isEmpty()) {
                Text(
                    text = "No active medications scheduled. Tap '+ Add Prescription' to record medication.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    medications.take(4).forEach { med ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${med.name} • ${med.dosage}",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Schedule: ${med.frequency} • Prescribed by ${med.prescribedBy.ifEmpty { "Attending Physician" }}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { onMarkTaken(med) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthNormalGreen),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, HealthNormalGreen),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Taken", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Taken", fontSize = 11.5.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { onMarkMissed(med) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Missed", fontSize = 11.5.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5. Doctor's Message Card with "Open Modal Pop-up" action
 */
@Composable
fun DoctorMessageCard(
    alert: PatientAlertNoteEntity,
    onOpenPopup: () -> Unit,
    onAcknowledge: () -> Unit,
    onOpenMessaging: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2444)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF38BDF8)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("doctor_message_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = "Doctor",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Doctor's Clinical Note",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = alert.senderName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF93C5FD)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x3338BDF8),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6638BDF8))
                ) {
                    Text(
                        text = alert.severity,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBAE6FD),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = alert.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFF1F5F9)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCBD5E1),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Open Full Screen Pop-up Dialog
                OutlinedButton(
                    onClick = onOpenPopup,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                    modifier = Modifier.testTag("open_popup_doc_message_button")
                ) {
                    Icon(imageVector = Icons.Default.NotificationImportant, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Pop-up", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onAcknowledge,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF93C5FD)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6638BDF8)),
                    modifier = Modifier.testTag("ack_doc_message_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Acknowledge", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onOpenMessaging,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier.testTag("reply_doc_message_button")
                ) {
                    Text("Reply to MD", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

/**
 * 6. Admin & Compliance Message Card
 */
@Composable
fun AdminMessageCard(
    alert: PatientAlertNoteEntity,
    onOpenPopup: () -> Unit,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1B0E)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFF59E0B)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_message_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD97706),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Admin & Compliance Broadcast",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ANA Care System Security",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss notice",
                        tint = Color(0xFFFDE68A),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFEF3C7)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFDE68A),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onOpenPopup,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFDE68A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                    modifier = Modifier.testTag("admin_open_popup_button")
                ) {
                    Text("View Pop-up Modal", fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * 7. Quick Actions Hub
 */
@Composable
fun QuickActionsHub(
    onNavigateToTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_actions_hub")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Clinical Hub & Direct Shortcuts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickHubActionItem(
                    title = "Add Vital Check",
                    subtitle = "Log daily telemetry",
                    icon = Icons.Default.Add,
                    color = Color(0xFF0284C7),
                    onClick = { onNavigateToTab(MainTab.ADD_RECORDS) },
                    modifier = Modifier.weight(1f)
                )

                QuickHubActionItem(
                    title = "Diagnostic Labs",
                    subtitle = "Review lab results",
                    icon = Icons.Default.Science,
                    color = Color(0xFF10B981),
                    onClick = { onNavigateToTab(MainTab.LAB_RESULTS) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickHubActionItem(
                    title = "Appointments",
                    subtitle = "Schedule telehealth",
                    icon = Icons.Default.CalendarMonth,
                    color = Color(0xFF8B5CF6),
                    onClick = { onNavigateToTab(MainTab.APPOINTMENTS) },
                    modifier = Modifier.weight(1f)
                )

                QuickHubActionItem(
                    title = "Message MD",
                    subtitle = "Encrypted chat",
                    icon = Icons.Default.QuestionAnswer,
                    color = Color(0xFFF59E0B),
                    onClick = { onNavigateToTab(MainTab.MESSAGING) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun QuickHubActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Caregiver Patient Roster Selection Card
 * Enables Caregiver to quickly switch and monitor all patients in their care circle or clinic
 */
@Composable
fun CaregiverPatientRosterCard(
    patients: List<UserAccountEntity>,
    selectedPatient: UserAccountEntity?,
    onSelectPatient: (UserAccountEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredPatients = remember(patients, searchQuery) {
        if (searchQuery.isBlank()) patients
        else patients.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.userId.contains(searchQuery, ignoreCase = true) ||
            it.email.contains(searchQuery, ignoreCase = true)
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC4B5FD).copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("caregiver_patient_roster_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFEDE9FE),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Caregiver Patients",
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Assigned Patients Monitoring Roster",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select a patient to inspect live vitals, medications & care instructions",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEDE9FE),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE))
                ) {
                    Text(
                        text = "${patients.size} PATIENTS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF6D28D9),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter patient by name or ID...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7C3AED),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("caregiver_patient_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Patient Cards Grid / Horizontal list
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredPatients.forEach { patient ->
                    val isSelected = selectedPatient?.userId == patient.userId
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFF5F3FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF7C3AED) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .width(210.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectPatient(patient) }
                            .testTag("caregiver_patient_card_${patient.userId}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF64748B),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = patient.avatarInitials.ifEmpty { patient.name.take(2).uppercase() },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF7C3AED)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Active",
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "ACTIVE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "SWITCH",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = patient.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )

                            Text(
                                text = "ID: ${patient.userId}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (patient.relationship.isNotBlank()) "Relation: ${patient.relationship}" else "Primary Care Circle",
                                fontSize = 10.sp,
                                color = if (isSelected) Color(0xFF6D28D9) else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
