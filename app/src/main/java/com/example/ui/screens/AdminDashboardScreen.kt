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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AppConfigEntity
import com.example.data.local.entities.PatientAlertNoteEntity
import com.example.data.local.entities.UserAccountEntity
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
fun AdminDashboardScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val allAccounts by viewModel.allAccounts.collectAsState()
    val activeAccount by viewModel.activeAccount.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val allAlertNotes by viewModel.allAlertNotes.collectAsState()
    val showSendAdminNoteDialog by viewModel.showSendAdminNoteDialog.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) } // 0: User Directory, 1: Live Layout Studio, 2: Important Pop-ups, 3: OTA App Updates
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("ALL") }
    var userToDelete by remember { mutableStateOf<UserAccountEntity?>(null) }
    var expandedUserId by remember { mutableStateOf<String?>(null) }

    // Patient layout controls editable state initialized from appConfig
    var editShowVitals by remember(appConfig) { mutableStateOf(appConfig.showVitalsSummary) }
    var editShowMeds by remember(appConfig) { mutableStateOf(appConfig.showMedicationSection) }
    var editShowActivities by remember(appConfig) { mutableStateOf(appConfig.showDailyActivities) }
    var editShowLabs by remember(appConfig) { mutableStateOf(appConfig.showLabResults) }
    var editShowEmergency by remember(appConfig) { mutableStateOf(appConfig.showEmergencyBanner) }
    var editDensity by remember(appConfig) { mutableStateOf(appConfig.patientLayoutDensity) }
    var editGreeting by remember(appConfig) { mutableStateOf(appConfig.patientGreetingMessage) }

    // Doctor layout controls editable state
    var editDoctorCompact by remember(appConfig) { mutableStateOf(appConfig.doctorCompactMode) }
    var editDoctorExpandCriticals by remember(appConfig) { mutableStateOf(appConfig.doctorAutoExpandCriticals) }
    var editDoctorQuickBar by remember(appConfig) { mutableStateOf(appConfig.doctorPrescriptionQuickBar) }
    var editDoctorHighlightCriticals by remember(appConfig) { mutableStateOf(appConfig.doctorHighlightCriticalVitals) }

    // OTA Update form state
    var newVersionName by remember(appConfig) { mutableStateOf(appConfig.appVersionName) }
    var newBuildNumberStr by remember(appConfig) { mutableStateOf(appConfig.appBuildNumber.toString()) }
    var newReleaseNotes by remember(appConfig) { mutableStateOf(appConfig.updateReleaseNotes) }
    var editMaintenanceMode by remember(appConfig) { mutableStateOf(appConfig.isMaintenanceMode) }
    var editMaintenanceMsg by remember(appConfig) { mutableStateOf(appConfig.maintenanceAnnouncement) }
    var selectedThemeAccent by remember(appConfig) { mutableStateOf(appConfig.systemThemeAccent) }

    // Stats
    val totalUsers = allAccounts.size
    val totalPatients = allAccounts.count { it.role == "PATIENT" }
    val totalCaregivers = allAccounts.count { it.role == "CAREGIVER" }
    val totalDoctors = allAccounts.count { it.role == "MEDICAL_PROFESSIONAL" }

    val filteredAccounts = allAccounts.filter { account ->
        val matchesRole = when (selectedRoleFilter) {
            "PATIENT" -> account.role == "PATIENT"
            "CAREGIVER" -> account.role == "CAREGIVER"
            "MEDICAL_PROFESSIONAL" -> account.role == "MEDICAL_PROFESSIONAL"
            "ADMIN" -> account.role == "ADMIN"
            else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            account.name.contains(searchQuery, ignoreCase = true) ||
            account.email.contains(searchQuery, ignoreCase = true) ||
            account.userId.contains(searchQuery, ignoreCase = true) ||
            account.phone.contains(searchQuery, ignoreCase = true) ||
            account.relationship.contains(searchQuery, ignoreCase = true)
        }
        matchesRole && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp)
            .testTag("admin_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Banner Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                modifier = Modifier.fillMaxWidth().testTag("admin_banner_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x3338BDF8),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = "Admin Console",
                                        tint = SkyLight,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "System Administration Console",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Full Live Governance & App Layout Engine",
                                    fontSize = 12.sp,
                                    color = SkyLight
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x3310B981)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(HealthNormalGreen)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "LIVE CONFIG",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6EE7B7)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBadge(label = "Total Users", count = totalUsers.toString(), color = Color(0xFF38BDF8), modifier = Modifier.weight(1f))
                        StatBadge(label = "Patients", count = totalPatients.toString(), color = Color(0xFF34D399), modifier = Modifier.weight(1f))
                        StatBadge(label = "Caregivers", count = totalCaregivers.toString(), color = Color(0xFFA78BFA), modifier = Modifier.weight(1f))
                        StatBadge(label = "Doctors", count = totalDoctors.toString(), color = Color(0xFFFBBF24), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Sub-tabs navigation for Admin console
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = NavyPrimary,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedAdminTab]),
                        color = NavyPrimary,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    text = { Text("👥 Users & Hierarchy", fontSize = 12.sp, fontWeight = if (selectedAdminTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    text = { Text("🎨 Layout Studio", fontSize = 12.sp, fontWeight = if (selectedAdminTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedAdminTab == 2,
                    onClick = { selectedAdminTab = 2 },
                    text = { Text("📢 Urgent Pop-ups (${allAlertNotes.size})", fontSize = 12.sp, fontWeight = if (selectedAdminTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedAdminTab == 3,
                    onClick = { selectedAdminTab = 3 },
                    text = { Text("🚀 App OTA & Updates", fontSize = 12.sp, fontWeight = if (selectedAdminTab == 3) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // ==========================================
        // TAB 0: User Directory & Access Hierarchy
        // ==========================================
        if (selectedAdminTab == 0) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by name, User ID, email, or role...", fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF64748B)) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_user_search_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "ALL" to "All (${allAccounts.size})",
                                "PATIENT" to "Patients ($totalPatients)",
                                "CAREGIVER" to "Caregivers ($totalCaregivers)",
                                "MEDICAL_PROFESSIONAL" to "Doctors ($totalDoctors)",
                                "ADMIN" to "Admins (${allAccounts.count { it.role == "ADMIN" }})"
                            ).forEach { (roleKey, label) ->
                                FilterChip(
                                    selected = selectedRoleFilter == roleKey,
                                    onClick = { selectedRoleFilter = roleKey },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NavyPrimary.copy(alpha = 0.15f),
                                        selectedLabelColor = NavyPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            items(filteredAccounts, key = { it.userId }) { account ->
                AdminUserCard(
                    account = account,
                    allAccounts = allAccounts,
                    isExpanded = expandedUserId == account.userId,
                    onToggleExpand = {
                        expandedUserId = if (expandedUserId == account.userId) null else account.userId
                    },
                    onDeleteClick = { userToDelete = account }
                )
            }
        }

        // ==========================================
        // TAB 1: Live Layout Studio (Patient & Doctor controls)
        // ==========================================
        if (selectedAdminTab == 1) {
            // Patient Layout Controls
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DashboardCustomize, contentDescription = "Layout", tint = NavyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Patient Dashboard Layout Controls", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        }
                        Text("Customize modules and visual density for all patient screens live.", fontSize = 12.sp, color = Color(0xFF64748B))

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom Greeting text
                        OutlinedTextField(
                            value = editGreeting,
                            onValueChange = { editGreeting = it },
                            label = { Text("Patient Greeting Headline") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Density Picker
                        Text("LAYOUT DENSITY / STYLING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "CARD_EXPANDED" to "Standard Cards",
                                "COMPACT_GRID" to "Compact Grid",
                                "HIGH_CONTRAST_SENIOR" to "Senior High Legibility"
                            ).forEach { (dens, label) ->
                                FilterChip(
                                    selected = editDensity == dens,
                                    onClick = { editDensity = dens },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NavyPrimary.copy(alpha = 0.15f),
                                        selectedLabelColor = NavyPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Module Toggles
                        LayoutToggleRow("Show Vitals & Health Stream Card", editShowVitals) { editShowVitals = it }
                        LayoutToggleRow("Show Running Medications Section", editShowMeds) { editShowMeds = it }
                        LayoutToggleRow("Show Daily Activities & Nutrition", editShowActivities) { editShowActivities = it }
                        LayoutToggleRow("Show Diagnostic Lab Results", editShowLabs) { editShowLabs = it }
                        LayoutToggleRow("Show 24/7 Emergency Dispatch Banner", editShowEmergency) { editShowEmergency = it }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.updatePatientLayoutSettings(
                                    showVitals = editShowVitals,
                                    showMeds = editShowMeds,
                                    showActivities = editShowActivities,
                                    showLabs = editShowLabs,
                                    showEmergency = editShowEmergency,
                                    density = editDensity,
                                    greeting = editGreeting
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Save Patient Dashboard Layout Live", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Doctor Layout Controls
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = "Doctor Controls", tint = Color(0xFF0284C7))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Doctor Clinical Workstation Layout", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        }
                        Text("Configure doctor chart presentation and clinical workflows.", fontSize = 12.sp, color = Color(0xFF64748B))

                        Spacer(modifier = Modifier.height(14.dp))

                        LayoutToggleRow("Compact Clinical Chart Mode", editDoctorCompact) { editDoctorCompact = it }
                        LayoutToggleRow("Auto-Expand Critical Patient Alerts", editDoctorExpandCriticals) { editDoctorExpandCriticals = it }
                        LayoutToggleRow("Show Quick Prescription Bar", editDoctorQuickBar) { editDoctorQuickBar = it }
                        LayoutToggleRow("Highlight Critical Vitals in Red", editDoctorHighlightCriticals) { editDoctorHighlightCriticals = it }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.updateDoctorLayoutSettings(
                                    compactMode = editDoctorCompact,
                                    autoExpandCriticals = editDoctorExpandCriticals,
                                    prescriptionQuickBar = editDoctorQuickBar,
                                    highlightCriticalVitals = editDoctorHighlightCriticals
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Save Doctor Workstation Layout Live", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 2: Important Note Pop-ups & Audit Tracker
        // ==========================================
        if (selectedAdminTab == 2) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationImportant, contentDescription = "Alerts", tint = Color(0xFFD97706))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Urgent Clinical Pop-up Notes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                    Text("Sends immediate screen modal alerts to patients", fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.setShowSendAdminNoteDialog(true) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("admin_send_popup_note_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compose & Dispatch Important Note Pop-up", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "DISPATCHED POP-UP ALERTS AUDIT LOG",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    letterSpacing = 0.5.sp
                )
            }

            if (allAlertNotes.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "No clinical pop-up notes have been dispatched yet.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(allAlertNotes, key = { it.id }) { alert ->
                    AdminAlertNoteCard(
                        alert = alert,
                        onDelete = { viewModel.deleteAlertNote(alert.id) }
                    )
                }
            }
        }

        // ==========================================
        // TAB 3: OTA App Updates & System Maintenance
        // ==========================================
        if (selectedAdminTab == 3) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = "OTA Update", tint = NavyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Live App Release & OTA Patch Engine", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                Text("Deploy instant update notifications and version changes", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newVersionName,
                                onValueChange = { newVersionName = it },
                                label = { Text("App Version") },
                                placeholder = { Text("e.g. 2.6.0-PROD") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = newBuildNumberStr,
                                onValueChange = { newBuildNumberStr = it },
                                label = { Text("Build #") },
                                placeholder = { Text("260") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(0.6f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = newReleaseNotes,
                            onValueChange = { newReleaseNotes = it },
                            label = { Text("Release Notes & Patch Highlights") },
                            placeholder = { Text("e.g. Added real-time vital telemetry pop-ups...") },
                            minLines = 2,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val buildNum = newBuildNumberStr.toIntOrNull() ?: 250
                                viewModel.deployLiveAppUpdate(
                                    versionName = newVersionName.trim(),
                                    buildNumber = buildNum,
                                    releaseNotes = newReleaseNotes.trim()
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Deploy Live Application Update (OTA)", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Maintenance Mode & Theme Accent
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = "Theme", tint = TealAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("System Theme Accent & Maintenance Mode", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("SYSTEM ACCENT THEME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "TEAL_EMERALD" to "Teal Emerald",
                                "ROYAL_NAVY" to "Royal Navy",
                                "CRIMSON_VITAL" to "Crimson Vital",
                                "INDIGO_MODERN" to "Indigo Modern"
                            ).forEach { (acc, label) ->
                                FilterChip(
                                    selected = selectedThemeAccent == acc,
                                    onClick = {
                                        selectedThemeAccent = acc
                                        viewModel.setSystemThemeAccent(acc)
                                    },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("System Maintenance Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                Text("Displays top banner on all dashboards", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                            Switch(
                                checked = editMaintenanceMode,
                                onCheckedChange = {
                                    editMaintenanceMode = it
                                    viewModel.setMaintenanceMode(it, editMaintenanceMsg)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HealthCriticalRed)
                            )
                        }

                        if (editMaintenanceMode) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = editMaintenanceMsg,
                                onValueChange = {
                                    editMaintenanceMsg = it
                                    viewModel.setMaintenanceMode(true, it)
                                },
                                label = { Text("Maintenance Announcement Message") },
                                placeholder = { Text("Scheduled backend sync in progress...") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal to Send Admin Pop-up Note
    if (showSendAdminNoteDialog) {
        SendImportantNoteDialog(
            senderRole = "ADMIN",
            senderName = activeAccount?.name ?: "System Administrator",
            patientList = allAccounts,
            onSend = { targetId, targetName, title, message, severity, actionLink ->
                viewModel.sendImportantAlertNote(targetId, targetName, title, message, severity, actionLink)
            },
            onDismiss = { viewModel.setShowSendAdminNoteDialog(false) }
        )
    }

    // Deletion Confirmation Dialog
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Confirm Account Removal", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove user '${user.name}' (${user.userId}) from the ANA Care portal registry? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUserAccount(user.userId)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthCriticalRed)
                ) {
                    Text("Delete Account", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun LayoutToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 13.sp, color = NavyDark, fontWeight = FontWeight.Medium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NavyPrimary
            )
        )
    }
}

@Composable
private fun AdminAlertNoteCard(
    alert: PatientAlertNoteEntity,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(alert.timestamp))

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
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (alert.severity) {
                            "URGENT" -> Color(0xFFFEE2E2)
                            "MEDICATION_ALERT" -> Color(0xFFFEF3C7)
                            "WARNING" -> Color(0xFFFFEDD5)
                            else -> Color(0xFFE0F2FE)
                        }
                    ) {
                        Text(
                            text = alert.severity,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (alert.severity) {
                                "URGENT" -> Color(0xFF991B1B)
                                "MEDICATION_ALERT" -> Color(0xFF92400E)
                                "WARNING" -> Color(0xFF9A3412)
                                else -> Color(0xFF0369A1)
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "To: ${alert.targetPatientName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (alert.isAcknowledged) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = if (alert.isAcknowledged) "ACKNOWLEDGED" else "PENDING POPUP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (alert.isAcknowledged) Color(0xFF15803D) else Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = alert.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = alert.message, fontSize = 12.sp, color = Color(0xFF475569), lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Sent by: ${alert.senderName} (${alert.senderRole})", fontSize = 11.sp, color = Color(0xFF64748B))
                Text(text = dateStr, fontSize = 11.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
private fun StatBadge(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun AdminUserCard(
    account: UserAccountEntity,
    allAccounts: List<UserAccountEntity>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val roleColor = when (account.role) {
        "MEDICAL_PROFESSIONAL" -> Color(0xFF0284C7)
        "CAREGIVER" -> Color(0xFF8B5CF6)
        "ADMIN" -> Color(0xFFD97706)
        else -> Color(0xFF10B981)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("admin_user_card_${account.userId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = roleColor.copy(alpha = 0.15f), modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = account.avatarInitials, fontWeight = FontWeight.Bold, color = roleColor, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = account.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = account.userId, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF64748B))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(6.dp), color = roleColor.copy(alpha = 0.12f)) {
                        Text(
                            text = when (account.role) {
                                "MEDICAL_PROFESSIONAL" -> "Doctor"
                                "CAREGIVER" -> "Caregiver"
                                "ADMIN" -> "Admin"
                                else -> "Patient"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = roleColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "✉ ${account.email}", fontSize = 12.sp, color = Color(0xFF475569))
                Text(text = "📞 ${account.phone}", fontSize = 12.sp, color = Color(0xFF475569))
            }
        }
    }
}
