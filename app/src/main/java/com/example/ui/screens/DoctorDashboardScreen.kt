package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DailyActivityEntity
import com.example.data.local.entities.LabResultEntity
import com.example.data.local.entities.MedicationEntity
import com.example.data.local.entities.PatientAlertNoteEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.data.local.entities.VitalSignEntity
import com.example.ui.components.DoctorPatientStatusBarChartCard
import com.example.ui.components.SendImportantNoteDialog
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyPrimary
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
    val allAlertNotes by viewModel.allAlertNotes.collectAsState()
    val activeAccount by viewModel.activeAccount.collectAsState()

    val doctorTargetPatient by viewModel.doctorTargetPatient.collectAsState()
    val showSendDoctorNoteDialog by viewModel.showSendDoctorNoteDialog.collectAsState()
    val doctorPatientVitals by viewModel.doctorPatientVitals.collectAsState()
    val doctorPatientMedications by viewModel.doctorPatientMedications.collectAsState()
    val doctorPatientActivities by viewModel.doctorPatientActivities.collectAsState()
    val doctorPatientLabs by viewModel.doctorPatientLabs.collectAsState()

    var searchPatientInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Vitals, 1: Medications, 2: Activities & Notes, 3: Lab Reports, 4: Caregiver Directives

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Dialogs state
    var showAddVitalsDialog by remember { mutableStateOf(false) }
    var showSuggestMedDialog by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<MedicationEntity?>(null) }
    var medicationToStop by remember { mutableStateOf<MedicationEntity?>(null) }

    var showAddActivityDialog by remember { mutableStateOf(false) }
    var showAddLabDialog by remember { mutableStateOf(false) }
    var editingLabResult by remember { mutableStateOf<LabResultEntity?>(null) }
    var labToDelete by remember { mutableStateOf<LabResultEntity?>(null) }

    var showCaregiverInstructionDialog by remember { mutableStateOf(false) }
    var caregiverInstructionTargetPatient by remember { mutableStateOf<UserAccountEntity?>(null) }

    // Direct Message Dialog state
    var directMessageTargetAccount by remember { mutableStateOf<UserAccountEntity?>(null) }
    var directMessageText by remember { mutableStateOf("") }

    val allPatients = allAccounts.filter { it.role == "PATIENT" }
    val currentDoctorId = activeAccount?.userId?.takeIf { it.isNotEmpty() } ?: "1001"
    
    // Connected patients for this doctor
    val myDoctorPatients = allAccounts.filter { 
        it.role == "PATIENT" && (it.assignedDoctorId == currentDoctorId || (it.assignedDoctorId.isEmpty() && currentDoctorId == "1001")) 
    }
    
    // Relevant caregivers connected to this doctor's patients
    val connectedPatientIds = myDoctorPatients.map { it.userId }.toSet()
    val relevantCaregivers = allAccounts.filter { 
        it.role == "CAREGIVER" && (connectedPatientIds.contains(it.assignedPatientId) || connectedPatientIds.isEmpty())
    }

    var selectedRosterFilter by remember { mutableStateOf("MY_PATIENTS") } // MY_PATIENTS, RELEVANT_CAREGIVERS, ALL_PATIENTS
    var graphScopeFilter by remember { mutableStateOf("MY_PATIENTS") } // MY_PATIENTS or ALL_PATIENTS
    var selectedBarStatusFilter by remember { mutableStateOf<String?>(null) } // null for ALL, "NORMAL", "CRITICAL"

    // Evaluate target patients for the Bar Graph based on the selected scope
    val targetGraphPatients = remember(graphScopeFilter, myDoctorPatients, allPatients) {
        if (graphScopeFilter == "MY_PATIENTS") {
            if (myDoctorPatients.isNotEmpty()) myDoctorPatients else allPatients
        } else {
            allPatients
        }
    }

    // Triage patients into Normal and Critical cohorts
    val (normalPatientsList, criticalPatientsList) = remember(targetGraphPatients, allVitalsList) {
        val normal = mutableListOf<UserAccountEntity>()
        val critical = mutableListOf<UserAccountEntity>()
        targetGraphPatients.forEach { pt ->
            val latestVital = allVitalsList.find { it.patientId == pt.userId }
            val isCritical = latestVital?.status == "CRITICAL" ||
                    latestVital?.status == "ELEVATED" ||
                    pt.userId == "21001002" ||
                    (latestVital != null && (latestVital.systolicBp >= 140 || latestVital.diastolicBp >= 90 || latestVital.bloodGlucose >= 180 || latestVital.oxygenSaturation < 92))
            if (isCritical) critical.add(pt) else normal.add(pt)
        }
        Pair(normal, critical)
    }

    val totalPatientsCount = targetGraphPatients.size
    val normalPatientsCount = normalPatientsList.size
    val criticalPatientsCount = criticalPatientsList.size

    val displayedPatients = remember(allPatients, myDoctorPatients, selectedRosterFilter, selectedBarStatusFilter, searchPatientInput, allVitalsList) {
        val baseList = when (selectedRosterFilter) {
            "MY_PATIENTS" -> if (myDoctorPatients.isNotEmpty()) myDoctorPatients else allPatients
            "ALL_PATIENTS" -> allPatients
            else -> myDoctorPatients
        }

        val filteredByBar = when (selectedBarStatusFilter) {
            "NORMAL" -> baseList.filter { pt ->
                val latestVital = allVitalsList.find { it.patientId == pt.userId }
                val isCrit = latestVital?.status == "CRITICAL" || latestVital?.status == "ELEVATED" || pt.userId == "21001002" || (latestVital != null && (latestVital.systolicBp >= 140 || latestVital.diastolicBp >= 90 || latestVital.bloodGlucose >= 180 || latestVital.oxygenSaturation < 92))
                !isCrit
            }
            "CRITICAL" -> baseList.filter { pt ->
                val latestVital = allVitalsList.find { it.patientId == pt.userId }
                val isCrit = latestVital?.status == "CRITICAL" || latestVital?.status == "ELEVATED" || pt.userId == "21001002" || (latestVital != null && (latestVital.systolicBp >= 140 || latestVital.diastolicBp >= 90 || latestVital.bloodGlucose >= 180 || latestVital.oxygenSaturation < 92))
                isCrit
            }
            else -> baseList
        }

        if (searchPatientInput.isBlank()) {
            filteredByBar
        } else {
            filteredByBar.filter { pat ->
                pat.name.contains(searchPatientInput, ignoreCase = true) ||
                pat.userId.contains(searchPatientInput, ignoreCase = true) ||
                pat.diagnosis.contains(searchPatientInput, ignoreCase = true) ||
                pat.bloodGroup.contains(searchPatientInput, ignoreCase = true)
            }
        }
    }

    val displayedCaregivers = remember(relevantCaregivers, allAccounts, searchPatientInput) {
        if (searchPatientInput.isBlank()) {
            relevantCaregivers
        } else {
            relevantCaregivers.filter { cg ->
                cg.name.contains(searchPatientInput, ignoreCase = true) ||
                cg.userId.contains(searchPatientInput, ignoreCase = true) ||
                cg.assignedPatientId.contains(searchPatientInput, ignoreCase = true)
            }
        }
    }

    // Directives sent to active target patient's caregiver
    val activePatientDirectives = remember(doctorTargetPatient, allAlertNotes) {
        val pid = doctorTargetPatient?.userId ?: ""
        allAlertNotes.filter { it.targetPatientId == pid || it.targetPatientId == "ALL" }
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
                // Card 1: CONNECTED PATIENTS
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
                            Text("MY PATIENTS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Surface(shape = CircleShape, color = Color(0xFFF3E8FF), modifier = Modifier.size(26.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${myDoctorPatients.size} Connected", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("• Active EMR Link", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF16A34A))
                    }
                }

                // Card 2: RELEVANT CAREGIVERS
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
                            Text("CAREGIVERS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Surface(shape = CircleShape, color = Color(0xFFE0E7FF), modifier = Modifier.size(26.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.SupervisedUserCircle, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${relevantCaregivers.size} Assigned", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Directives Active", fontSize = 10.sp, color = Color(0xFF4F46E5))
                    }
                }

                // Card 3: PENDING LAB AUDITS
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
                            Text("PENDING LABS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Surface(shape = CircleShape, color = Color(0xFFFEF3C7), modifier = Modifier.size(26.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Biotech, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("3 Reviews", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Action Required", fontSize = 10.sp, color = Color(0xFFD97706))
                    }
                }

                // Card 4: TELEMETRY ALERTS
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
                            Text("TELEMETRY", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Surface(shape = CircleShape, color = Color(0xFFFEE2E2), modifier = Modifier.size(26.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$criticalPatientsCount Critical", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(if (criticalPatientsCount > 0) "Alert on ${criticalPatientsList.firstOrNull()?.name ?: "Pt. 21001002"}" else "All Biometrics Stable", fontSize = 9.sp, color = if (criticalPatientsCount > 0) Color(0xFFDC2626) else Color(0xFF16A34A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        // 3. High-Precision Patient Health Triage Bar Graph Card (Total, Normal, Critical Patients)
        item {
            DoctorPatientStatusBarChartCard(
                totalPatientsCount = totalPatientsCount,
                normalPatientsCount = normalPatientsCount,
                criticalPatientsCount = criticalPatientsCount,
                selectedScope = graphScopeFilter,
                onScopeChange = { graphScopeFilter = it },
                selectedBarFilter = selectedBarStatusFilter,
                onSelectBarFilter = { selectedBarStatusFilter = it },
                criticalPatients = criticalPatientsList,
                onSelectCriticalPatient = { critPatient ->
                    viewModel.selectDoctorTargetPatient(critPatient)
                },
                onSendCaregiverInstruction = { critPatient ->
                    viewModel.selectDoctorTargetPatient(critPatient)
                    viewModel.setShowSendDoctorNoteDialog(true)
                }
            )
        }

        // 4. Connected Patients & Relevant Caregivers Roster Card
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
                                text = "Clinical Roster & Care Network",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text(
                                text = "Connected to ${activeAccount?.name ?: "Dr. Sarah Jenkins"} (ID: ${activeAccount?.userId ?: "1001"})",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Search box
                        OutlinedTextField(
                            value = searchPatientInput,
                            onValueChange = { searchPatientInput = it },
                            placeholder = { Text("Search name, ID or diagnosis...", fontSize = 12.sp) },
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
                                .width(290.dp)
                                .height(46.dp)
                                .testTag("doctor_patient_search_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Role-based Filter Tabs Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filterTabs = listOf(
                            "MY_PATIENTS" to "🩺 Connected (${myDoctorPatients.size})",
                            "CRITICAL_PATIENTS" to "⚠️ Critical (${criticalPatientsList.size})",
                            "NORMAL_PATIENTS" to "✅ Normal (${normalPatientsList.size})",
                            "RELEVANT_CAREGIVERS" to "👥 Caregivers (${relevantCaregivers.size})",
                            "ALL_PATIENTS" to "🏥 All Patients (${allPatients.size})"
                        )
                        filterTabs.forEach { (key, title) ->
                            val isFilterActive = selectedRosterFilter == key
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isFilterActive) {
                                    when (key) {
                                        "CRITICAL_PATIENTS" -> Color(0xFFDC2626)
                                        "NORMAL_PATIENTS" -> Color(0xFF16A34A)
                                        else -> NavyPrimary
                                    }
                                } else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .clickable {
                                        selectedRosterFilter = key
                                        if (key == "CRITICAL_PATIENTS") {
                                            selectedBarStatusFilter = "CRITICAL"
                                        } else if (key == "NORMAL_PATIENTS") {
                                            selectedBarStatusFilter = "NORMAL"
                                        } else {
                                            selectedBarStatusFilter = null
                                        }
                                    }
                                    .testTag("roster_filter_$key")
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isFilterActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isFilterActive) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (selectedRosterFilter == "RELEVANT_CAREGIVERS") {
                        // Caregiver Directory View
                        val cgScrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(cgScrollState)
                        ) {
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
                                    Text("CAREGIVER ID", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(110.dp))
                                    Text("CAREGIVER NAME", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(180.dp))
                                    Text("RELATIONSHIP", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(150.dp))
                                    Text("ASSIGNED PATIENT", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(220.dp))
                                    Text("CONTACT PHONE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(140.dp))
                                    Text("PERMISSIONS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(130.dp))
                                    Text("PHYSICIAN ACTIONS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(320.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            if (displayedCaregivers.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No relevant caregivers found for connected patients.", fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                            } else {
                                displayedCaregivers.forEachIndexed { index, cg ->
                                    val assignedPt = allPatients.find { it.userId == cg.assignedPatientId }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (index % 2 == 0) Color.White else Color(0xFFF8FAFC),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .testTag("caregiver_row_${cg.userId}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(cg.userId, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5), modifier = Modifier.width(110.dp))
                                            Text(cg.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary, modifier = Modifier.width(180.dp))
                                            Text(cg.relationship.ifEmpty { "Primary Caregiver" }, fontSize = 11.5.sp, color = Color(0xFF475569), modifier = Modifier.width(150.dp))
                                            
                                            Column(modifier = Modifier.width(220.dp)) {
                                                Text(assignedPt?.name ?: "Pt. ID: ${cg.assignedPatientId}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                                Text("ID: ${cg.assignedPatientId}", fontSize = 10.5.sp, color = Color(0xFF0284C7))
                                            }

                                            Text(cg.phone.ifEmpty { "+1 (555) 012-4491" }, fontSize = 11.5.sp, color = Color(0xFF334155), modifier = Modifier.width(140.dp))
                                            
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color(0xFFEDE9FE),
                                                modifier = Modifier.width(130.dp)
                                            ) {
                                                Text(
                                                    text = cg.caregiverPermissions.ifEmpty { "FULL_ACCESS" },
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF6D28D9),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.width(320.dp),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Button(
                                                    onClick = {
                                                        caregiverInstructionTargetPatient = assignedPt
                                                        showCaregiverInstructionDialog = true
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.height(28.dp).testTag("instruction_button_${cg.userId}")
                                                ) {
                                                    Text("Special Instructions", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }

                                                Button(
                                                    onClick = { directMessageTargetAccount = cg },
                                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.height(28.dp).testTag("message_cg_button_${cg.userId}")
                                                ) {
                                                    Text("Message", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }

                                                if (assignedPt != null) {
                                                    Button(
                                                        onClick = {
                                                            viewModel.selectDoctorTargetPatient(assignedPt)
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.height(28.dp).testTag("view_pt_button_${assignedPt.userId}")
                                                    ) {
                                                        Text("View Patient", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
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
                                    Text("ACTIONS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(280.dp))
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
                                            .clickable { viewModel.selectDoctorTargetPatient(pat) }
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
                                                modifier = Modifier.width(110.dp)
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

                                            // Action Buttons: Open Chart, Message, Directives
                                            Row(
                                                modifier = Modifier.width(280.dp),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Open Chart Button
                                                Button(
                                                    onClick = {
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
                                                        text = if (isSelected) "Active Chart" else "Click to View",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }

                                                // Message Button
                                                Button(
                                                    onClick = {
                                                        directMessageTargetAccount = pat
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier
                                                        .height(28.dp)
                                                        .testTag("message_button_${pat.userId}")
                                                ) {
                                                    Text("Message", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }

                                                // Special Instructions to Caregiver
                                                Button(
                                                    onClick = {
                                                        caregiverInstructionTargetPatient = pat
                                                        showCaregiverInstructionDialog = true
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier
                                                        .height(28.dp)
                                                        .testTag("caregiver_directives_button_${pat.userId}")
                                                ) {
                                                    Text("Directives", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
        }

        // 4. Active Patient Detail Clinical Chart Header
        item {
            val patient = doctorTargetPatient
            if (patient != null) {
                val patCaregivers = allAccounts.filter { it.role == "CAREGIVER" && it.assignedPatientId == patient.userId }
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
                    modifier = Modifier.fillMaxWidth().testTag("active_patient_banner")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = patient.avatarInitials.ifEmpty { "PT" },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Active Clinical Chart: ${patient.name}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = "MRN/ID: ${patient.userId} • Gender: ${patient.gender} • Blood: ${patient.bloodGroup.ifEmpty { "O+" }} • Phone: ${patient.phone.ifEmpty { "+1 (555) 019-2834" }}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                    Text(
                                        text = "Diagnosis: ${patient.diagnosis.ifEmpty { "Primary Essential Hypertension" }} • Allergies: ${patient.allergies.ifEmpty { "Penicillin, Sulfa" }}",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF475569)
                                    )
                                    if (patCaregivers.isNotEmpty()) {
                                        Text(
                                            text = "Assigned Caregiver(s): ${patCaregivers.joinToString { "${it.name} (${it.relationship.ifEmpty { "Primary" }})" }}",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF7C3AED)
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        caregiverInstructionTargetPatient = patient
                                        showCaregiverInstructionDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("doctor_header_cg_directive_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.SupervisedUserCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Caregiver Directives", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
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

        // 5. 5 Dedicated Clinical Tabs: Vitals, Medications, Activities & Notes, Labs, Caregiver Directives
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
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = NavyPrimary,
                            edgePadding = 8.dp,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = TealAccent,
                                    height = 3.dp
                                )
                            }
                        ) {
                            val tabs = listOf(
                                Triple("Vital Signs (${doctorPatientVitals.size})", Icons.Default.Favorite, "Vitals"),
                                Triple("Medications (${doctorPatientMedications.count { it.status == "RUNNING" }})", Icons.Default.Medication, "Medications"),
                                Triple("Daily Activities & Notes (${doctorPatientActivities.size})", Icons.Default.FitnessCenter, "Activities"),
                                Triple("Diagnostic Labs (${doctorPatientLabs.size})", Icons.Default.Biotech, "Labs"),
                                Triple("Caregiver Directives (${activePatientDirectives.size})", Icons.Default.SupervisedUserCircle, "Directives")
                            )
                            tabs.forEachIndexed { index, (title, icon, contentDesc) ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = contentDesc,
                                            tint = if (selectedTab == index) NavyPrimary else Color(0xFF64748B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    text = if (isLandscape) {
                                        {
                                            Text(
                                                text = title,
                                                fontSize = 12.sp,
                                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selectedTab == index) NavyPrimary else Color(0xFF64748B)
                                            )
                                        }
                                    } else null,
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
                    // TAB 0: VITAL SIGNS (View & Add Vitals)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Vital Signs & Biometric Telemetry",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text(
                                    text = "Blood pressure, heart rate, blood glucose, SpO2, and temperature readings",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Button(
                                onClick = { showAddVitalsDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("doctor_add_vitals_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Add Vitals", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    if (doctorPatientVitals.isEmpty()) {
                        item {
                            EmptySectionCard(
                                title = "No Vital Signs Logged",
                                description = "Click '+ Add Vitals' above to record biometric vitals for ${patient.name}."
                            )
                        }
                    } else {
                        items(doctorPatientVitals) { vital ->
                            DoctorVitalCard(vital = vital)
                        }
                    }
                }

                1 -> {
                    // TAB 1: MEDICATIONS (View, Add & Edit Rx, Stop/Resume)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Active Prescriptions & Regimen Titration",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text(
                                    text = "Manage pharmacotherapy, dosing instructions, and refills",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Button(
                                onClick = {
                                    editingMedication = null
                                    showSuggestMedDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("doctor_add_rx_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Add New Rx", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (doctorPatientMedications.isEmpty()) {
                        item {
                            EmptySectionCard(
                                title = "No Active Prescriptions",
                                description = "There are no current running medications assigned for this patient. Click '+ Add New Rx' to prescribe."
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
                                },
                                onToggleStatus = {
                                    viewModel.toggleMedicationRunningStatus(med.id, med.status != "RUNNING")
                                }
                            )
                        }
                    }
                }

                2 -> {
                    // TAB 2: DAILY ACTIVITIES & NOTES (View & Add Activity/Clinical Note)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Daily Activities, Physical Therapy & Clinical Progress Notes",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text(
                                    text = "Track exercise, physical therapy, nutrition, pain scores, and physician progress notes",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Button(
                                onClick = { showAddActivityDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("doctor_add_activity_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Add Activity / Note", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    if (doctorPatientActivities.isEmpty()) {
                        item {
                            EmptySectionCard(
                                title = "No Daily Activities or Progress Notes",
                                description = "Click '+ Add Activity / Note' above to record physical therapy, activity, or clinical notes."
                            )
                        }
                    } else {
                        items(doctorPatientActivities) { act ->
                            DoctorActivityCard(activity = act)
                        }
                    }
                }

                3 -> {
                    // TAB 3: DIAGNOSTIC LAB REPORTS (View, Add & Edit Labs)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Diagnostic Laboratory Reports",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text(
                                    text = "Review CMP, Lipid Panels, CBC, HbA1c, and add new diagnostic reports",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Button(
                                onClick = {
                                    editingLabResult = null
                                    showAddLabDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("doctor_add_lab_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Add Lab Report", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    if (doctorPatientLabs.isEmpty()) {
                        item {
                            EmptySectionCard(
                                title = "No Diagnostic Lab Results",
                                description = "Click '+ Add Lab Report' to log new blood tests or laboratory findings."
                            )
                        }
                    } else {
                        items(doctorPatientLabs) { lab ->
                            DoctorLabCard(
                                lab = lab,
                                onEdit = {
                                    editingLabResult = lab
                                    showAddLabDialog = true
                                },
                                onDelete = {
                                    labToDelete = lab
                                }
                            )
                        }
                    }
                }

                4 -> {
                    // TAB 4: SPECIAL INSTRUCTIONS TO CAREGIVER
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Special Instructions & Directives to Caregivers",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text(
                                    text = "Clinical directives delivered directly to the caregiver's dashboard and alerts",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Button(
                                onClick = {
                                    caregiverInstructionTargetPatient = patient
                                    showCaregiverInstructionDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("doctor_issue_instruction_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Issue Instruction to Caregiver", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    if (activePatientDirectives.isEmpty()) {
                        item {
                            EmptySectionCard(
                                title = "No Directives Issued Yet",
                                description = "Click '+ Issue Instruction to Caregiver' above to send strict protocols (e.g. fluid restriction, medication timing, PT support) to this patient's caregiver."
                            )
                        }
                    } else {
                        items(activePatientDirectives) { directive ->
                            DoctorCaregiverDirectiveCard(
                                alert = directive,
                                onAcknowledge = { viewModel.acknowledgeAlertNote(directive.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // ----------------------------------------------------
    // DIALOGS & MODALS
    // ----------------------------------------------------

    // 1. ADD VITALS DIALOG
    if (showAddVitalsDialog && doctorTargetPatient != null) {
        AddDoctorVitalsDialog(
            patient = doctorTargetPatient!!,
            onDismiss = { showAddVitalsDialog = false },
            onSave = { systolic, diastolic, hr, spo2, tempF, glucose, respRate, weightLbs, notes ->
                viewModel.recordVitalsForPatient(
                    patientId = doctorTargetPatient!!.userId,
                    systolic = systolic,
                    diastolic = diastolic,
                    heartRate = hr,
                    spo2 = spo2,
                    tempF = tempF,
                    glucose = glucose,
                    respRate = respRate,
                    weightLbs = weightLbs,
                    notes = notes
                )
                showAddVitalsDialog = false
            }
        )
    }

    // 2. ADD/EDIT MEDICATION DIALOG
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

    // 3. ADD ACTIVITY & CLINICAL NOTE DIALOG
    if (showAddActivityDialog && doctorTargetPatient != null) {
        AddDoctorActivityDialog(
            patient = doctorTargetPatient!!,
            onDismiss = { showAddActivityDialog = false },
            onSave = { activityType, durationMinutes, metricValue, painScore, mood, notes ->
                viewModel.recordDailyActivityForPatient(
                    patientId = doctorTargetPatient!!.userId,
                    activityType = activityType,
                    durationMinutes = durationMinutes,
                    metricValue = metricValue,
                    painScore = painScore,
                    mood = mood,
                    notes = notes
                )
                showAddActivityDialog = false
            }
        )
    }

    // 4. ADD/EDIT LAB REPORT DIALOG
    if (showAddLabDialog && doctorTargetPatient != null) {
        AddDoctorLabDialog(
            patient = doctorTargetPatient!!,
            existingLab = editingLabResult,
            doctorName = "${activeAccount?.name ?: "Dr. Sarah Jenkins"}, MD",
            onDismiss = { showAddLabDialog = false },
            onSave = { labEntity ->
                viewModel.saveLabResult(labEntity)
                showAddLabDialog = false
            }
        )
    }

    // 5. SPECIAL INSTRUCTIONS TO CAREGIVER DIALOG
    if (showCaregiverInstructionDialog && (caregiverInstructionTargetPatient != null || doctorTargetPatient != null)) {
        val targetPt = caregiverInstructionTargetPatient ?: doctorTargetPatient!!
        SendCaregiverSpecialInstructionDialog(
            patient = targetPt,
            caregivers = allAccounts.filter { it.role == "CAREGIVER" && it.assignedPatientId == targetPt.userId },
            onDismiss = { showCaregiverInstructionDialog = false },
            onSend = { title, message, severity, actionLink ->
                viewModel.sendCaregiverSpecialInstruction(
                    targetPatientId = targetPt.userId,
                    targetPatientName = targetPt.name,
                    title = title,
                    message = message,
                    severity = severity,
                    actionLink = actionLink
                )
                showCaregiverInstructionDialog = false
            }
        )
    }

    // 6. DIRECT MESSAGE DIALOG (E2EE)
    val msgTarget = directMessageTargetAccount
    if (msgTarget != null) {
        AlertDialog(
            onDismissRequest = { directMessageTargetAccount = null },
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
                        text = "Recipient: ${msgTarget.name} (${msgTarget.role} • ID: ${msgTarget.userId})",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    OutlinedTextField(
                        value = directMessageText,
                        onValueChange = { directMessageText = it },
                        label = { Text("Clinical Note / Guidance") },
                        placeholder = { Text("Enter encrypted guidance or consultation note...") },
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
                            directMessageTargetAccount = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Send Message", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { directMessageTargetAccount = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 7. STOP MEDICATION CONFIRMATION DIALOG
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

    // 8. DELETE LAB CONFIRMATION DIALOG
    val labDel = labToDelete
    if (labDel != null) {
        AlertDialog(
            onDismissRequest = { labToDelete = null },
            title = {
                Text("Delete Lab Report", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HealthCriticalRed)
            },
            text = {
                Text("Are you sure you want to delete '${labDel.testName}' from ${doctorTargetPatient?.name}'s records?", fontSize = 13.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLabResult(labDel.id)
                        labToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthCriticalRed)
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { labToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 9. BROADCAST DOCTOR URGENT NOTE DIALOG
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

// ----------------------------------------------------
// COMPONENT CARDS
// ----------------------------------------------------

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
    onStop: () -> Unit,
    onToggleStatus: () -> Unit
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

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Edit Rx", fontSize = 11.sp, color = NavyPrimary)
                    }

                    if (isRunning) {
                        Button(
                            onClick = onStop,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Discontinue", fontSize = 11.sp, color = HealthCriticalRed, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onToggleStatus,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDCFCE7)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Resume", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
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
                            activity.activityType.contains("Progress", ignoreCase = true) -> Icons.Default.MedicalInformation
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes / Clinical Interpretation: ${activity.notes}\nLogged by: ${activity.loggedBy}",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DoctorLabCard(
    lab: LabResultEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                    Text("Category: ${lab.category} • Performed: $dateStr", fontSize = 11.5.sp, color = Color(0xFF64748B))
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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

                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Lab", tint = NavyPrimary, modifier = Modifier.size(16.dp))
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Lab", tint = HealthCriticalRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Summary: ${lab.summary.ifEmpty { "Diagnostic evaluation completed" }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (lab.keyParameters.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Key Parameters: ${lab.keyParameters}",
                            fontSize = 11.5.sp,
                            color = Color(0xFF334155)
                        )
                    }
                    if (lab.doctorNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Doctor Interpretation: ${lab.doctorNotes}",
                            fontSize = 11.5.sp,
                            color = Color(0xFF0369A1)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lab Facility: ${lab.facility} • Ordering Physician: ${lab.orderedBy}",
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
private fun DoctorCaregiverDirectiveCard(
    alert: PatientAlertNoteEntity,
    onAcknowledge: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(alert.timestamp))
    val isUrgent = alert.severity == "URGENT" || alert.severity == "CRITICAL"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isUrgent) Color(0xFFFFFBEB) else Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isUrgent) Color(0xFFFDE68A) else Color(0xFFE2E8F0)),
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
                        imageVector = if (isUrgent) Icons.Default.NotificationImportant else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isUrgent) Color(0xFFD97706) else Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = alert.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Issued by ${alert.senderName} • $dateStr",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isUrgent) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = if (alert.isAcknowledged) "ACKNOWLEDGED BY CG" else alert.severity,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (alert.isAcknowledged) Color(0xFF15803D) else if (isUrgent) Color(0xFFB45309) else Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = RoundedCornerShape(6.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = alert.message,
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------
// DIALOG IMPLEMENTATIONS
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDoctorVitalsDialog(
    patient: UserAccountEntity,
    onDismiss: () -> Unit,
    onSave: (systolic: Int, diastolic: Int, hr: Int, spo2: Int, tempF: Float, glucose: Int, respRate: Int, weightLbs: Float, notes: String) -> Unit
) {
    var systolic by remember { mutableStateOf("120") }
    var diastolic by remember { mutableStateOf("80") }
    var hr by remember { mutableStateOf("72") }
    var spo2 by remember { mutableStateOf("98") }
    var tempF by remember { mutableStateOf("98.6") }
    var glucose by remember { mutableStateOf("105") }
    var respRate by remember { mutableStateOf("16") }
    var weightLbs by remember { mutableStateOf("165.0") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Record Biometric Vitals", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Patient: ${patient.name} (ID: ${patient.userId})", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = systolic,
                        onValueChange = { systolic = it },
                        label = { Text("Systolic BP (mmHg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("vitals_input_systolic")
                    )
                    OutlinedTextField(
                        value = diastolic,
                        onValueChange = { diastolic = it },
                        label = { Text("Diastolic BP (mmHg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("vitals_input_diastolic")
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = hr,
                        onValueChange = { hr = it },
                        label = { Text("Heart Rate (bpm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("vitals_input_hr")
                    )
                    OutlinedTextField(
                        value = spo2,
                        onValueChange = { spo2 = it },
                        label = { Text("SpO2 Oxygen (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("vitals_input_spo2")
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = glucose,
                        onValueChange = { glucose = it },
                        label = { Text("Blood Glucose (mg/dL)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("vitals_input_glucose")
                    )
                    OutlinedTextField(
                        value = tempF,
                        onValueChange = { tempF = it },
                        label = { Text("Temperature (°F)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("vitals_input_temp")
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = respRate,
                        onValueChange = { respRate = it },
                        label = { Text("Resp Rate (breaths/m)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightLbs,
                        onValueChange = { weightLbs = it },
                        label = { Text("Weight (lbs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Physician Clinical Notes & Observations") },
                    placeholder = { Text("e.g. Resting BP measured after 5 minutes sitting...") },
                    modifier = Modifier.fillMaxWidth().testTag("vitals_input_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sysInt = systolic.toIntOrNull() ?: 120
                    val diaInt = diastolic.toIntOrNull() ?: 80
                    val hrInt = hr.toIntOrNull() ?: 72
                    val spo2Int = spo2.toIntOrNull() ?: 98
                    val tempFloat = tempF.toFloatOrNull() ?: 98.6f
                    val gluInt = glucose.toIntOrNull() ?: 105
                    val respInt = respRate.toIntOrNull() ?: 16
                    val wtFloat = weightLbs.toFloatOrNull() ?: 165f
                    onSave(sysInt, diaInt, hrInt, spo2Int, tempFloat, gluInt, respInt, wtFloat, notes.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("save_doctor_vitals_confirm_button")
            ) {
                Text("Save Vitals", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
                    text = if (existingMedication == null) "Prescribe Medication" else "Edit Prescription",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
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
                    label = { Text("Medication Name (e.g. Lisinopril)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("suggest_med_name_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage (e.g. 10 mg)") },
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
                    label = { Text("Clinical Instructions for Patient & Caregiver") },
                    placeholder = { Text("Take in morning with food. Avoid grapefruit.") },
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                Text(if (existingMedication == null) "Prescribe" else "Update Rx", color = Color.White, fontWeight = FontWeight.Bold)
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
private fun AddDoctorActivityDialog(
    patient: UserAccountEntity,
    onDismiss: () -> Unit,
    onSave: (activityType: String, durationMinutes: Int, metricValue: String, painScore: Int, mood: String, notes: String) -> Unit
) {
    var activityType by remember { mutableStateOf("Physical Therapy Session") }
    var durationMinutes by remember { mutableStateOf("30") }
    var metricValue by remember { mutableStateOf("Completed lower-extremity gait protocol") }
    var painScore by remember { mutableFloatStateOf(2f) }
    var mood by remember { mutableStateOf("Good") }
    var notes by remember { mutableStateOf("") }

    val activityOptions = listOf(
        "Physical Therapy Session",
        "Walking & Mobility",
        "Cardiac Rehab Exercise",
        "Clinical Progress Note",
        "Sleep & Rest Pattern",
        "Nutrition & Diet Adherence"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Log Activity & Clinical Note", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Patient: ${patient.name} (ID: ${patient.userId})", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Select Activity / Note Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    activityOptions.forEach { opt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (activityType == opt) NavyPrimary else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable { activityType = opt }
                        ) {
                            Text(
                                text = opt,
                                fontSize = 11.sp,
                                fontWeight = if (activityType == opt) FontWeight.Bold else FontWeight.Normal,
                                color = if (activityType == opt) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = durationMinutes,
                        onValueChange = { durationMinutes = it },
                        label = { Text("Duration (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("activity_duration_input")
                    )
                    OutlinedTextField(
                        value = mood,
                        onValueChange = { mood = it },
                        label = { Text("Patient Mood") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = metricValue,
                    onValueChange = { metricValue = it },
                    label = { Text("Metric / Goal Outcome") },
                    placeholder = { Text("e.g. 4,500 steps or Full ROM achieved") },
                    modifier = Modifier.fillMaxWidth().testTag("activity_metric_input")
                )

                Column {
                    Text("Patient Pain Score: ${painScore.toInt()} / 10", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                    Slider(
                        value = painScore,
                        onValueChange = { painScore = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Clinical Interpretation & Progress Notes") },
                    placeholder = { Text("Document patient response, caregiver assistance, or medical plan...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("activity_notes_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val durInt = durationMinutes.toIntOrNull() ?: 30
                    onSave(activityType, durInt, metricValue.trim(), painScore.toInt(), mood.trim(), notes.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("save_doctor_activity_button")
            ) {
                Text("Save Activity & Notes", color = Color.White, fontWeight = FontWeight.Bold)
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
private fun AddDoctorLabDialog(
    patient: UserAccountEntity,
    existingLab: LabResultEntity?,
    doctorName: String,
    onDismiss: () -> Unit,
    onSave: (LabResultEntity) -> Unit
) {
    var testName by remember { mutableStateOf(existingLab?.testName ?: "Comprehensive Metabolic Panel (CMP)") }
    var category by remember { mutableStateOf(existingLab?.category ?: "Metabolic") }
    var status by remember { mutableStateOf(existingLab?.status ?: "NORMAL") }
    var summary by remember { mutableStateOf(existingLab?.summary ?: "Electrolytes, BUN, Creatinine within reference intervals") }
    var keyParameters by remember { mutableStateOf(existingLab?.keyParameters ?: "eGFR: 88 mL/min, Creatinine: 0.9 mg/dL, K+: 4.2 mEq/L") }
    var doctorNotes by remember { mutableStateOf(existingLab?.doctorNotes ?: "Normal renal and liver panel.") }
    var facility by remember { mutableStateOf(existingLab?.facility ?: "ANA Central Clinical Laboratory") }

    val presetTests = listOf(
        "Comprehensive Metabolic Panel (CMP)" to "Metabolic",
        "Lipid Panel (Cholesterol / Triglycerides)" to "Cardiovascular",
        "Complete Blood Count (CBC) with Diff" to "Hematology",
        "HbA1c Glycated Hemoglobin" to "Endocrine",
        "Renal Function Panel" to "Nephrology",
        "Cardiac Troponin I Panel" to "Cardiovascular"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(if (existingLab == null) "Add Diagnostic Lab Report" else "Edit Diagnostic Lab Report", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Patient: ${patient.name} (ID: ${patient.userId})", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Quick Select Standard Lab:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetTests.forEach { (tName, cat) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (testName == tName) NavyPrimary else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable {
                                testName = tName
                                category = cat
                            }
                        ) {
                            Text(
                                text = tName.take(24) + "...",
                                fontSize = 10.5.sp,
                                color = if (testName == tName) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = testName,
                    onValueChange = { testName = it },
                    label = { Text("Test Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("lab_test_name_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status (NORMAL / ELEVATED)") },
                        modifier = Modifier.weight(1f).testTag("lab_status_input")
                    )
                }

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Clinical Summary & Findings") },
                    modifier = Modifier.fillMaxWidth().testTag("lab_summary_input")
                )

                OutlinedTextField(
                    value = keyParameters,
                    onValueChange = { keyParameters = it },
                    label = { Text("Key Parameters & Biomarkers") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = doctorNotes,
                    onValueChange = { doctorNotes = it },
                    label = { Text("Physician Interpretation Notes") },
                    modifier = Modifier.fillMaxWidth().testTag("lab_doctor_notes_input")
                )

                OutlinedTextField(
                    value = facility,
                    onValueChange = { facility = it },
                    label = { Text("Testing Facility") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (testName.isNotBlank()) {
                        val labEntity = existingLab?.copy(
                            testName = testName.trim(),
                            category = category.trim(),
                            status = status.trim(),
                            summary = summary.trim(),
                            keyParameters = keyParameters.trim(),
                            doctorNotes = doctorNotes.trim(),
                            facility = facility.trim(),
                            orderedBy = doctorName
                        ) ?: LabResultEntity(
                            patientId = patient.userId,
                            testName = testName.trim(),
                            category = category.trim(),
                            datePerformed = System.currentTimeMillis(),
                            status = status.trim(),
                            summary = summary.trim(),
                            keyParameters = keyParameters.trim(),
                            doctorNotes = doctorNotes.trim(),
                            orderedBy = doctorName,
                            facility = facility.trim()
                        )
                        onSave(labEntity)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("save_doctor_lab_button")
            ) {
                Text(if (existingLab == null) "Save Lab Report" else "Update Lab", color = Color.White, fontWeight = FontWeight.Bold)
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
private fun SendCaregiverSpecialInstructionDialog(
    patient: UserAccountEntity,
    caregivers: List<UserAccountEntity>,
    onDismiss: () -> Unit,
    onSend: (title: String, message: String, severity: String, actionLink: String) -> Unit
) {
    var title by remember { mutableStateOf("Strict Dietary & Fluid Protocol Directive") }
    var message by remember { mutableStateOf("Please ensure patient adheres to low-sodium diet (<1500mg/day) and log morning blood pressure before 9:00 AM daily.") }
    var severity by remember { mutableStateOf("URGENT") }
    var actionLink by remember { mutableStateOf("VITALS_LOG") }

    val presetInstructions = listOf(
        "Strict Dietary & Fluid Protocol" to "Please ensure patient adheres to low-sodium diet (<1500mg/day) and log morning blood pressure before 9:00 AM daily.",
        "Medication Adherence Directive" to "Please administer the morning ACE inhibitor with meals and verify heart rate is above 60 bpm.",
        "Physical Therapy Assistance" to "Assist patient with 20-minute guided walking exercise twice daily and monitor for shortness of breath.",
        "Urgent Blood Pressure Alert" to "Patient showed elevated systolic reading. Please re-check BP in 1 hour and contact clinic if above 150 mmHg."
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Special Instructions to Caregiver", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                Text("Patient: ${patient.name} (ID: ${patient.userId})", fontSize = 12.sp, color = Color(0xFF64748B))
                if (caregivers.isNotEmpty()) {
                    Text("Target Caregiver(s): ${caregivers.joinToString { it.name }}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF7C3AED))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Quick Protocol Templates:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    presetInstructions.forEach { (t, msg) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (title == t) Color(0xFFFEF3C7) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (title == t) Color(0xFFF59E0B) else Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    title = t
                                    message = msg
                                }
                        ) {
                            Text(
                                text = "• $t",
                                fontSize = 11.sp,
                                fontWeight = if (title == t) FontWeight.Bold else FontWeight.Normal,
                                color = if (title == t) Color(0xFF92400E) else Color(0xFF334155),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Directive Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cg_instruction_title_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Priority Severity Selection
                    listOf("ROUTINE", "URGENT", "CRITICAL").forEach { sev ->
                        val isSel = severity == sev
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) when (sev) {
                                "CRITICAL" -> Color(0xFFDC2626)
                                "URGENT" -> Color(0xFFD97706)
                                else -> Color(0xFF0284C7)
                            } else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { severity = sev }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = sev,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Special Instruction / Clinical Directive for Caregiver") },
                    placeholder = { Text("Enter detailed steps and parameters for the caregiver...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("cg_instruction_message_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSend(title.trim(), message.trim(), severity, actionLink)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                modifier = Modifier.testTag("send_cg_instruction_confirm_button")
            ) {
                Text("Issue Directive", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
