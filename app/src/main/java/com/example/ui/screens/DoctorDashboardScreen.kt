package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DailyActivityEntity
import com.example.data.local.entities.LabResultEntity
import com.example.data.local.entities.MedicationEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.data.local.entities.VitalSignEntity
import com.example.ui.components.SendImportantNoteDialog
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
fun DoctorDashboardScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val allAccounts by viewModel.allAccounts.collectAsState()
    val allVitalsList by viewModel.allVitalsList.collectAsState()
    val activeAccount by viewModel.activeAccount.collectAsState()

    val doctorTargetPatient by viewModel.doctorTargetPatient.collectAsState()
    val showSendDoctorNoteDialog by viewModel.showSendDoctorNoteDialog.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val doctorPatientVitals by viewModel.doctorPatientVitals.collectAsState()
    val doctorPatientMedications by viewModel.doctorPatientMedications.collectAsState()
    val doctorPatientActivities by viewModel.doctorPatientActivities.collectAsState()
    val doctorPatientLabs by viewModel.doctorPatientLabs.collectAsState()

    var searchPatientInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview/Vitals, 1: Medications (Prescribe/Stop), 2: Daily Activities, 3: Lab Results

    // Dialog state for prescribing / suggesting medication
    var showSuggestMedDialog by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<MedicationEntity?>(null) }
    var medicationToStop by remember { mutableStateOf<MedicationEntity?>(null) }

    // Direct Message Dialog state
    var directMessageTargetPatient by remember { mutableStateOf<UserAccountEntity?>(null) }
    var directMessageText by remember { mutableStateOf("") }

    val allPatients = allAccounts.filter { it.role == "PATIENT" }
    val currentDoctorId = activeAccount?.userId?.takeIf { it.isNotEmpty() } ?: "1001"
    val myDoctorPatients = allAccounts.filter { 
        it.role == "PATIENT" && (it.assignedDoctorId == currentDoctorId || (it.assignedDoctorId.isEmpty() && currentDoctorId == "1001")) 
    }

    var selectedRosterFilter by remember { mutableStateOf("ALL_PATIENTS") } // ALL_PATIENTS, MY_PATIENTS, WITH_CAREGIVERS

    val displayedPatients = remember(allPatients, myDoctorPatients, selectedRosterFilter, searchPatientInput, activeAccount, allAccounts) {
        val baseList = when (selectedRosterFilter) {
            "MY_PATIENTS" -> if (myDoctorPatients.isNotEmpty()) myDoctorPatients else allPatients
            "WITH_CAREGIVERS" -> allPatients.filter { pat -> allAccounts.any { it.role == "CAREGIVER" && it.assignedPatientId == pat.userId } }
            else -> allPatients
        }
        if (searchPatientInput.isBlank()) {
            baseList
        } else {
            allPatients.filter { pat ->
                pat.name.contains(searchPatientInput, ignoreCase = true) ||
                pat.userId.contains(searchPatientInput, ignoreCase = true) ||
                pat.diagnosis.contains(searchPatientInput, ignoreCase = true) ||
                pat.bloodGroup.contains(searchPatientInput, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp)
            .testTag("doctor_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Attending Physician Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier.fillMaxWidth().testTag("doctor_header_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x2238BDF8),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4438BDF8))
                            ) {
                                Text(
                                    text = "ATTENDING PHYSICIAN CORE",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = activeAccount?.name ?: "Dr. Sarah Jenkins, MD",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Doctor ID: ${activeAccount?.userId ?: "1001"} • Specialization: ${activeAccount?.specialty.takeIf { !it.isNullOrBlank() } ?: "Internal Medicine"}",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Button(
                            onClick = { viewModel.setShowSendDoctorNoteDialog(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("broadcast_urgent_alert_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Broadcast Urgent Alert", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // 2. 4 Metric KPI Stat Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: LINKED PATIENTS
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("LINKED PATIENTS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Surface(shape = CircleShape, color = Color(0xFFF3E8FF), modifier = Modifier.size(26.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${myDoctorPatients.size} Under Care", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("• 100% telemetry synced", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF16A34A))
                    }
                }

                // Card 2: PENDING LAB AUDITS
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("PENDING LAB AUDITS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Surface(shape = CircleShape, color = Color(0xFFFEF3C7), modifier = Modifier.size(26.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Biotech, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("3 Reports", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Requires physician review", fontSize = 10.sp, color = Color(0xFFD97706))
                    }
                }

                // Card 3: TELEMETRY ALERTS
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TELEMETRY ALERTS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Surface(shape = CircleShape, color = Color(0xFFFEE2E2), modifier = Modifier.size(26.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("1 Active", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("BP fluctuation on Pt. Arthur (ID: 21001002)", fontSize = 9.sp, color = Color(0xFFDC2626), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                // Card 4: TODAY'S CONSULTATIONS
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TODAY'S CONSULTATIONS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Surface(shape = CircleShape, color = Color(0xFFE0F2FE), modifier = Modifier.size(26.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("4 Consults", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Next: Pt. Eleanor Vance (10:00 AM)", fontSize = 9.sp, color = Color(0xFF0284C7), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        // 3. Active Patient Directory & Vitals Status Section (Responsive Data Table)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth().testTag("doctor_patient_search_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title & Search Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Active Patient Directory & Vitals Status",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text(
                                text = "Patients linked to Attending Doctor: ${activeAccount?.name ?: "Dr. Sarah Jenkins"} (${activeAccount?.userId ?: "1001"})",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Search box
                        OutlinedTextField(
                            value = searchPatientInput,
                            onValueChange = { searchPatientInput = it },
                            placeholder = { Text("Search Pt. name or ID...", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = TealAccent, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                viewModel.searchAndSelectDoctorPatient(searchPatientInput)
                            }),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .width(280.dp)
                                .height(46.dp)
                                .testTag("doctor_patient_search_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter Tabs Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filterTabs = listOf(
                            "ALL_PATIENTS" to "All Clinic (${allPatients.size})",
                            "MY_PATIENTS" to "My Assigned (${myDoctorPatients.size})",
                            "WITH_CAREGIVERS" to "With Caregivers"
                        )
                        filterTabs.forEach { (key, title) ->
                            val isFilterActive = selectedRosterFilter == key
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isFilterActive) NavyPrimary else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .clickable { selectedRosterFilter = key }
                                    .testTag("roster_filter_$key")
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isFilterActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isFilterActive) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Desktop Table Container with horizontal scroll for responsiveness
                    val tableScrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tableScrollState)
                    ) {
                        // Table Header
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("PATIENT ID", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(110.dp))
                                Text("PATIENT NAME", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(190.dp))
                                Text("PRIMARY DIAGNOSIS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(210.dp))
                                Text("LATEST BP", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(130.dp))
                                Text("BLOOD SUGAR", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(120.dp))
                                Text("ASSIGNED CAREGIVER", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(200.dp))
                                Text("STATUS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(120.dp))
                                Text("ACTIONS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(240.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (displayedPatients.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No patients match the search or filter criteria.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        } else {
                            displayedPatients.forEachIndexed { index, pat ->
                                val isSelected = doctorTargetPatient?.userId == pat.userId
                                val patCaregivers = allAccounts.filter { it.role == "CAREGIVER" && it.assignedPatientId == pat.userId }
                                val cgText = when (patCaregivers.size) {
                                    0 -> "None assigned"
                                    1 -> patCaregivers.first().name.replace("CG. ", "")
                                    else -> patCaregivers.joinToString(", ") { it.name.replace("CG. ", "") }
                                }

                                // Lookup latest vitals for this patient
                                val latestVital = allVitalsList.find { it.patientId == pat.userId }
                                val bpText = if (latestVital != null) "${latestVital.systolicBp}/${latestVital.diastolicBp} mmHg" else "120/80 mmHg"
                                val glucoseText = if (latestVital != null) "${latestVital.bloodGlucose} mg/dL" else "100 mg/dL"
                                val statusText = when {
                                    latestVital?.status == "CRITICAL" -> "Critical"
                                    latestVital?.status == "ELEVATED" || pat.userId == "21001002" -> "Elevated BP"
                                    else -> "Stable"
                                }
                                val statusBg = when (statusText) {
                                    "Critical" -> Color(0xFFFEE2E2)
                                    "Elevated BP" -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFDCFCE7)
                                }
                                val statusFg = when (statusText) {
                                    "Critical" -> Color(0xFFB91C1C)
                                    "Elevated BP" -> Color(0xFFB45309)
                                    else -> Color(0xFF15803D)
                                }
                                val bpColor = when (statusText) {
                                    "Critical" -> Color(0xFFDC2626)
                                    "Elevated BP" -> Color(0xFFD97706)
                                    else -> Color(0xFF16A34A)
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) Color(0xFFF0FDF4) else if (index % 2 == 0) Color.White else Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF10B981) else Color(0xFFF1F5F9)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .testTag("patient_row_${pat.userId}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Patient ID
                                        Text(
                                            text = pat.userId,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0284C7),
                                            modifier = Modifier
                                                .width(110.dp)
                                                .clickable { viewModel.selectDoctorTargetPatient(pat) }
                                        )

                                        // Patient Name
                                        Text(
                                            text = "${pat.name} (Age ${pat.dateOfBirth.takeIf { it.isNotBlank() } ?: "68"})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary,
                                            modifier = Modifier.width(190.dp)
                                        )

                                        // Diagnosis
                                        Text(
                                            text = pat.diagnosis.ifEmpty { "Hypertension, Type-2 Diabetes" },
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF475569),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.width(210.dp)
                                        )

                                        // Latest BP
                                        Text(
                                            text = bpText,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = bpColor,
                                            modifier = Modifier.width(130.dp)
                                        )

                                        // Blood Sugar
                                        Text(
                                            text = glucoseText,
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF334155),
                                            modifier = Modifier.width(120.dp)
                                        )

                                        // Caregiver
                                        Text(
                                            text = cgText,
                                            fontSize = 11.sp,
                                            color = if (patCaregivers.isNotEmpty()) Color(0xFF7C3AED) else Color(0xFF94A3B8),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.width(200.dp)
                                        )

                                        // Status Pill
                                        Box(modifier = Modifier.width(120.dp)) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = statusBg
                                            ) {
                                                Text(
                                                    text = statusText,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = statusFg,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        // Action Buttons: Open Chart, Message, Send Alert
                                        Row(
                                            modifier = Modifier.width(240.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Open Chart Button
                                            Button(
                                                onClick = {
                                                    searchPatientInput = pat.userId
                                                    viewModel.selectDoctorTargetPatient(pat)
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isSelected) Color(0xFF10B981) else Color(0xFF0284C7)
                                                ),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier
                                                    .height(28.dp)
                                                    .testTag("open_chart_button_${pat.userId}")
                                            ) {
                                                Text(
                                                    text = if (isSelected) "Chart Active" else "Open Chart",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }

                                            // Message Button
                                            Button(
                                                onClick = {
                                                    directMessageTargetPatient = pat
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier
                                                    .height(28.dp)
                                                    .testTag("message_button_${pat.userId}")
                                            ) {
                                                Text("Message", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }

                                            // Send Alert Button
                                            Button(
                                                onClick = {
                                                    viewModel.selectDoctorTargetPatient(pat)
                                                    viewModel.setShowSendDoctorNoteDialog(true)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier
                                                    .height(28.dp)
                                                    .testTag("send_alert_button_${pat.userId}")
                                            ) {
                                                Text("Send Alert", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
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

        // 4. Active Patient Detail Clinical Chart Section
        item {
            val patient = doctorTargetPatient
            if (patient != null) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                    modifier = Modifier.fillMaxWidth().testTag("active_patient_banner")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = patient.avatarInitials.ifEmpty { "PT" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Active Clinical Chart: ${patient.name}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = "ID: ${patient.userId} • Gender: ${patient.gender} • Blood: ${patient.bloodGroup.ifEmpty { "O+" }} • Phone: ${patient.phone.ifEmpty { "+1 (555) 019-2834" }}",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        editingMedication = null
                                        showSuggestMedDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("doctor_prescribe_action_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Prescribe Medication", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                Button(
                                    onClick = { viewModel.setShowSendDoctorNoteDialog(true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = HealthCriticalRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("doctor_send_alert_action_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Broadcast Alert", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                Button(
                                    onClick = { viewModel.exportVitalsPdfReport() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Export PDF", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Clinical Navigation Tabs & Tab Content
        item {
            val patient = doctorTargetPatient
            if (patient != null) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("doctor_chart_tabs_card")
                ) {
                    Column {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = NavyPrimary,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = TealAccent,
                                    height = 3.dp
                                )
                            }
                        ) {
                            val tabs = listOf(
                                "Vitals History (${doctorPatientVitals.size})",
                                "Prescriptions (${doctorPatientMedications.count { it.status == "RUNNING" }})",
                                "Daily Activities (${doctorPatientActivities.size})",
                                "Diagnostic Lab Reports (${doctorPatientLabs.size})"
                            )
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            text = title,
                                            fontSize = 12.sp,
                                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedTab == index) NavyPrimary else Color(0xFF64748B)
                                        )
                                    },
                                    modifier = Modifier.testTag("doctor_tab_$index")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tab Content Display
        val patient = doctorTargetPatient
        if (patient != null) {
            when (selectedTab) {
                0 -> {
                    // Vitals History Tab
                    if (doctorPatientVitals.isEmpty()) {
                        item {
                            EmptySectionCard(
                                title = "No Vital Signs Logged",
                                description = "This patient has not logged any vital signs yet. You can advise them to use the Vital Capture module."
                            )
                        }
                    } else {
                        items(doctorPatientVitals) { vital ->
                            DoctorVitalCard(vital = vital)
                        }
                    }
                }
                1 -> {
                    // Prescriptions Tab
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Prescriptions & Regimen Titration",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Button(
                                onClick = {
                                    editingMedication = null
                                    showSuggestMedDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add New Rx", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (doctorPatientMedications.isEmpty()) {
                        item {
                            EmptySectionCard(
                                title = "No Active Prescriptions",
                                description = "There are no current running medications assigned for this patient."
                            )
                        }
                    } else {
                        items(doctorPatientMedications) { med ->
                            DoctorMedicationCard(
                                med = med,
                                onEdit = {
                                    editingMedication = med
                                    showSuggestMedDialog = true
                                },
                                onStop = {
                                    medicationToStop = med
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // Daily Activities Tab
                    if (doctorPatientActivities.isEmpty()) {
                        item {
                            EmptySectionCard(
                                title = "No Daily Activities Recorded",
                                description = "No physical therapy, walking steps, sleep, or nutrition logs found for this patient."
                            )
                        }
                    } else {
                        items(doctorPatientActivities) { act ->
                            DoctorActivityCard(activity = act)
                        }
                    }
                }
                3 -> {
                    // Lab Results Tab
                    if (doctorPatientLabs.isEmpty()) {
                        item {
                            EmptySectionCard(
                                title = "No Diagnostic Lab Results",
                                description = "No laboratory results (CMP, Lipid, CBC, HbA1c) have been recorded for this patient."
                            )
                        }
                    } else {
                        items(doctorPatientLabs) { lab ->
                            DoctorLabCard(lab = lab)
                        }
                    }
                }
            }
        }
    }

    // Direct Message Dialog
    val msgTarget = directMessageTargetPatient
    if (msgTarget != null) {
        AlertDialog(
            onDismissRequest = { directMessageTargetPatient = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Encrypted Message to ${msgTarget.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Recipient: ${msgTarget.name} (Patient ID: ${msgTarget.userId})",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    OutlinedTextField(
                        value = directMessageText,
                        onValueChange = { directMessageText = it },
                        label = { Text("Clinical Note / Guidance") },
                        placeholder = { Text("Enter encrypted medical note or instructions...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("doctor_direct_message_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (directMessageText.isNotBlank()) {
                            viewModel.sendMessage(
                                peerId = msgTarget.userId,
                                peerName = msgTarget.name,
                                messageText = directMessageText.trim()
                            )
                            directMessageText = ""
                            directMessageTargetPatient = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Send E2EE Message", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { directMessageTargetPatient = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Doctor Prescribe / Modify Dialog
    if (showSuggestMedDialog && doctorTargetPatient != null) {
        DoctorSuggestMedicationDialog(
            patient = doctorTargetPatient!!,
            existingMedication = editingMedication,
            doctorName = "${activeAccount?.name ?: "Dr. Sarah Jenkins"}, MD (ID: ${activeAccount?.userId ?: "1001"})",
            onDismiss = { showSuggestMedDialog = false },
            onSave = { newMed ->
                viewModel.saveMedication(newMed)
                showSuggestMedDialog = false
            }
        )
    }

    // Stop Medication Confirmation Dialog
    val medStop = medicationToStop
    if (medStop != null) {
        AlertDialog(
            onDismissRequest = { medicationToStop = null },
            title = {
                Text("Discontinue Prescription", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HealthCriticalRed)
            },
            text = {
                Text(
                    text = "Are you sure you want to stop/discontinue '${medStop.name}' (${medStop.dosage}) for patient ${doctorTargetPatient?.name}?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleMedicationRunningStatus(medStop.id, false)
                        medicationToStop = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthCriticalRed)
                ) {
                    Text("Confirm Discontinuation", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { medicationToStop = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Send Doctor Urgent Note / Pop-up Dialog
    if (showSendDoctorNoteDialog) {
        SendImportantNoteDialog(
            senderRole = "DOCTOR",
            senderName = activeAccount?.name ?: "Dr. Sarah Jenkins, MD",
            patientList = allPatients,
            preselectedPatientId = doctorTargetPatient?.userId ?: "ALL",
            onDismiss = { viewModel.setShowSendDoctorNoteDialog(false) },
            onSend = { targetPatientId, targetPatientName, title, message, severity, actionLink ->
                viewModel.sendImportantAlertNote(
                    targetPatientId = targetPatientId,
                    targetPatientName = targetPatientName,
                    title = title,
                    message = message,
                    severity = severity,
                    actionLink = actionLink
                )
            }
        )
    }
}

@Composable
private fun EmptySectionCard(title: String, description: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun DoctorVitalCard(vital: VitalSignEntity) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(vital.timestamp))
    val isCritical = vital.status == "CRITICAL" || vital.status == "ELEVATED"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCritical) Color(0xFFFFF7ED) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isCritical) Color(0xFFFDBA74) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = if (isCritical) HealthWarningAmber else HealthNormalGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Vital Measurement • $dateStr",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isCritical) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = vital.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCritical) Color(0xFFB45309) else Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VitalMetricItem(label = "BP", value = "${vital.systolicBp}/${vital.diastolicBp}", unit = "mmHg")
                VitalMetricItem(label = "Heart Rate", value = "${vital.heartRate}", unit = "bpm")
                VitalMetricItem(label = "Blood Sugar", value = "${vital.bloodGlucose}", unit = "mg/dL")
                VitalMetricItem(label = "SpO2", value = "${vital.oxygenSaturation}", unit = "%")
                VitalMetricItem(label = "Temp", value = "${vital.temperatureF}", unit = "°F")
            }

            if (vital.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${vital.notes} (Measured by: ${vital.measuredBy})",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VitalMetricItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
        Text(text = unit, fontSize = 9.sp, color = Color(0xFF94A3B8))
    }
}

@Composable
private fun DoctorMedicationCard(
    med: MedicationEntity,
    onEdit: () -> Unit,
    onStop: () -> Unit
) {
    val isRunning = med.status == "RUNNING"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isRunning) Color(0xFFE2E8F0) else Color(0xFFF1F5F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = if (isRunning) TealAccent else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${med.name} (${med.dosage})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRunning) NavyPrimary else Color(0xFF94A3B8)
                        )
                        Text(
                            text = "${med.frequency} • ${med.scheduledTime} • Route: ${med.route}",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isRunning) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = if (isRunning) "ACTIVE" else "STOPPED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning) Color(0xFF15803D) else Color(0xFF991B1B),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (med.instructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Instructions: ${med.instructions}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Prescribed by: ${med.prescribedBy} • Refills: ${med.refillsRemaining}",
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B)
                )

                if (isRunning) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Modify", fontSize = 11.sp, color = NavyPrimary)
                        }
                        Button(
                            onClick = onStop,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Discontinue", fontSize = 11.sp, color = HealthCriticalRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorActivityCard(activity: DailyActivityEntity) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(activity.timestamp))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            activity.activityType.contains("Walk", ignoreCase = true) -> Icons.Default.DirectionsWalk
                            activity.activityType.contains("Sleep", ignoreCase = true) -> Icons.Default.NightsStay
                            activity.activityType.contains("Therapy", ignoreCase = true) -> Icons.Default.FitnessCenter
                            else -> Icons.Default.DirectionsWalk
                        },
                        contentDescription = null,
                        tint = TealAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = activity.activityType,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }

                Text(dateStr, fontSize = 11.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Metric: ${activity.metricValue} (Duration: ${activity.durationMinutes} mins) • Mood: ${activity.mood} • Pain Score: ${activity.painScore}/10",
                fontSize = 12.sp,
                color = Color(0xFF334155)
            )

            if (activity.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${activity.notes} • Logged by: ${activity.loggedBy}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun DoctorLabCard(lab: LabResultEntity) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(lab.datePerformed))
    val isCritical = lab.status.contains("ELEVATED", ignoreCase = true) || lab.status.contains("CRITICAL", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCritical) Color(0xFFFECACA) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(lab.testName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text("Category: ${lab.category} • Date: $dateStr", fontSize = 11.5.sp, color = Color(0xFF64748B))
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isCritical) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = lab.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCritical) Color(0xFF991B1B) else Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Result: ${lab.summary.ifEmpty { lab.keyParameters }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Lab: ${lab.facility} • Physician: ${lab.orderedBy}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DoctorSuggestMedicationDialog(
    patient: UserAccountEntity,
    existingMedication: MedicationEntity?,
    doctorName: String,
    onDismiss: () -> Unit,
    onSave: (MedicationEntity) -> Unit
) {
    var name by remember { mutableStateOf(existingMedication?.name ?: "") }
    var dosage by remember { mutableStateOf(existingMedication?.dosage ?: "") }
    var frequency by remember { mutableStateOf(existingMedication?.frequency ?: "Once daily") }
    var route by remember { mutableStateOf(existingMedication?.route ?: "Oral") }
    var scheduledTime by remember { mutableStateOf(existingMedication?.scheduledTime ?: "08:00 AM") }
    var instructions by remember { mutableStateOf(existingMedication?.instructions ?: "") }
    var category by remember { mutableStateOf(existingMedication?.category ?: "Cardiovascular") }
    var refills by remember { mutableStateOf((existingMedication?.refillsRemaining ?: 3).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (existingMedication == null) "Suggest & Prescribe Medication" else "Modify Prescription",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Patient: ${patient.name} (${patient.userId})",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name (e.g. Amlodipine)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("suggest_med_name_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage (e.g. 5 mg)") },
                        modifier = Modifier.weight(1f).testTag("suggest_med_dosage_input")
                    )
                    OutlinedTextField(
                        value = route,
                        onValueChange = { route = it },
                        label = { Text("Route (Oral, SubQ)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = { frequency = it },
                        label = { Text("Frequency") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = scheduledTime,
                        onValueChange = { scheduledTime = it },
                        label = { Text("Scheduled Time") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Clinical Instructions for Patient") },
                    modifier = Modifier.fillMaxWidth().testTag("suggest_med_instructions_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = refills,
                        onValueChange = { refills = it },
                        label = { Text("Refills") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && dosage.isNotBlank()) {
                        val toSave = existingMedication?.copy(
                            name = name.trim(),
                            dosage = dosage.trim(),
                            frequency = frequency.trim(),
                            route = route.trim(),
                            scheduledTime = scheduledTime.trim(),
                            instructions = instructions.trim(),
                            category = category.trim(),
                            refillsRemaining = refills.toIntOrNull() ?: 2,
                            prescribedBy = doctorName,
                            status = "RUNNING"
                        ) ?: MedicationEntity(
                            patientId = patient.userId,
                            name = name.trim(),
                            dosage = dosage.trim(),
                            frequency = frequency.trim(),
                            route = route.trim(),
                            scheduledTime = scheduledTime.trim(),
                            instructions = instructions.trim(),
                            prescribedBy = doctorName,
                            startDate = System.currentTimeMillis(),
                            isTakenToday = false,
                            reminderEnabled = true,
                            category = category.trim(),
                            refillsRemaining = refills.toIntOrNull() ?: 2,
                            status = "RUNNING",
                            lastAction = "",
                            lastActionTimestamp = 0L,
                            lastActionDateFormatted = ""
                        )
                        onSave(toSave)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("doctor_confirm_prescribe_button")
            ) {
                Text("Confirm & Prescribe", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
