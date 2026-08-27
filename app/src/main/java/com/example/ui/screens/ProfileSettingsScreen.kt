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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.MedicationEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.PortalViewModel

@Composable
fun ProfileSettingsScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val activeAccount by viewModel.activeAccount.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val allMedications by viewModel.allMedicationsList.collectAsState()

    val account = activeAccount ?: return

    // Main Profile Tabs: 0: Personal Info, 1: Caregiver Circle, 2: Prescriptions, 3: Security & Access
    var selectedMainTab by remember { mutableIntStateOf(0) }
    // Sub-Tabs for each Main Tab
    var selectedCaregiverSubTab by remember { mutableIntStateOf(0) }
    var selectedPrescriptionSubTab by remember { mutableIntStateOf(0) }
    var selectedSecuritySubTab by remember { mutableIntStateOf(0) }

    // Medication Dialog State (Add or Edit)
    var showMedicationDialog by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<MedicationEntity?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_settings_screen")
    ) {
        // User Profile Summary Mini Header
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                when (account.role) {
                                    "MEDICAL_PROFESSIONAL" -> Color(0xFF0284C7)
                                    "CAREGIVER" -> Color(0xFF8B5CF6)
                                    "ADMIN" -> Color(0xFFD97706)
                                    else -> Color(0xFF10B981)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = account.avatarInitials,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = account.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0x3338BDF8)
                            ) {
                                Text(
                                    text = when (account.role) {
                                        "MEDICAL_PROFESSIONAL" -> "Physician"
                                        "CAREGIVER" -> "Caregiver"
                                        "ADMIN" -> "Admin"
                                        else -> "Patient"
                                    },
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyLight,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = when (account.role) {
                                "PATIENT" -> "Patient ID: ${account.userId} • Doctor Ref: ${account.assignedDoctorId.ifEmpty { "1001" }}"
                                "MEDICAL_PROFESSIONAL" -> "Doctor ID: ${account.userId} • Licensed Physician"
                                "CAREGIVER" -> "Caregiver ID: ${account.userId} • Assigned Patient: ${account.assignedPatientId.ifEmpty { "21001001" }}"
                                else -> "${account.email} • ID: ${account.userId}"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFF93C5FD)
                        )
                    }
                }
            }
        }

        // Main Tab Row
        TabRow(
            selectedTabIndex = selectedMainTab,
            containerColor = NavySecondary,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                    color = SkyLight,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedMainTab == 0,
                onClick = { selectedMainTab = 0 },
                text = { Text("Personal", fontSize = 12.sp, fontWeight = if (selectedMainTab == 0) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) },
                selectedContentColor = SkyLight,
                unselectedContentColor = Color(0xFF94A3B8),
                modifier = Modifier.testTag("profile_tab_personal")
            )
            Tab(
                selected = selectedMainTab == 1,
                onClick = { selectedMainTab = 1 },
                text = { Text("Caregivers", fontSize = 12.sp, fontWeight = if (selectedMainTab == 1) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null, modifier = Modifier.size(16.dp)) },
                selectedContentColor = SkyLight,
                unselectedContentColor = Color(0xFF94A3B8),
                modifier = Modifier.testTag("profile_tab_caregivers")
            )
            Tab(
                selected = selectedMainTab == 2,
                onClick = { selectedMainTab = 2 },
                text = { Text("Prescriptions", fontSize = 12.sp, fontWeight = if (selectedMainTab == 2) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(16.dp)) },
                selectedContentColor = SkyLight,
                unselectedContentColor = Color(0xFF94A3B8),
                modifier = Modifier.testTag("profile_tab_prescriptions")
            )
            Tab(
                selected = selectedMainTab == 3,
                onClick = { selectedMainTab = 3 },
                text = { Text("Security", fontSize = 12.sp, fontWeight = if (selectedMainTab == 3) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp)) },
                selectedContentColor = SkyLight,
                unselectedContentColor = Color(0xFF94A3B8),
                modifier = Modifier.testTag("profile_tab_security")
            )
        }

        // Tab Content with Sub-Tabs
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(14.dp)
        ) {
            when (selectedMainTab) {
                0 -> PersonalInfoTabContent(
                    account = account,
                    onSave = { updated -> viewModel.updateProfileSettings(updated) },
                    onLinkDoctor = { docId -> viewModel.linkDoctorById(docId) }
                )
                1 -> CaregiverCircleTabContent(
                    activeAccount = account,
                    allAccounts = allAccounts,
                    selectedSubTab = selectedCaregiverSubTab,
                    onSubTabChange = { selectedCaregiverSubTab = it },
                    onSwitchAccount = { viewModel.switchAccount(it) },
                    onAddUserClick = { viewModel.setShowAddMultiUserDialog(true) }
                )
                2 -> PrescriptionsTabContent(
                    allMedications = allMedications,
                    selectedSubTab = selectedPrescriptionSubTab,
                    onSubTabChange = { selectedPrescriptionSubTab = it },
                    onAddMedClick = {
                        editingMedication = null
                        showMedicationDialog = true
                    },
                    onEditMedClick = { med ->
                        editingMedication = med
                        showMedicationDialog = true
                    },
                    onToggleStatus = { medId, willRun ->
                        viewModel.toggleMedicationRunningStatus(medId, willRun)
                    },
                    onDeleteMed = { viewModel.deleteMedication(it) }
                )
                3 -> SecurityAccessTabContent(
                    account = account,
                    selectedSubTab = selectedSecuritySubTab,
                    onSubTabChange = { selectedSecuritySubTab = it },
                    onToggleBiometric = { viewModel.toggleBiometricSetting(it) },
                    onToggleMfa = { viewModel.toggleMfaSetting(it) },
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }

    // Add / Edit Medication Modal Dialog
    if (showMedicationDialog) {
        MedicationEditDialog(
            initialMedication = editingMedication,
            patientId = account.userId,
            onDismiss = {
                showMedicationDialog = false
                editingMedication = null
            },
            onSave = { updatedMed ->
                viewModel.saveMedication(updatedMed)
                showMedicationDialog = false
                editingMedication = null
            }
        )
    }
}

// ----------------------------------------------------
// TAB 1: Personal Info (Direct Display: Name, Age, Contact Number, Diagnosis, Blood Group, Medical History)
// ----------------------------------------------------
@Composable
private fun PersonalInfoTabContent(
    account: UserAccountEntity,
    onSave: (UserAccountEntity) -> Unit,
    onLinkDoctor: (String) -> Unit
) {
    var name by remember(account) { mutableStateOf(account.name) }
    var age by remember(account) { mutableStateOf(account.age) }
    var phone by remember(account) { mutableStateOf(account.phone) }
    var diagnosis by remember(account) { mutableStateOf(account.diagnosis) }
    var bloodGroup by remember(account) { mutableStateOf(account.bloodGroup) }
    var medicalHistory by remember(account) { mutableStateOf(account.medicalHistory) }
    var doctorIdInput by remember(account) { mutableStateOf(account.assignedDoctorId.ifEmpty { "1001" }) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // --- System Assigned ID Card ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth().testTag("profile_assigned_id_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = SkyLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Assigned Health System ID",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x3338BDF8)
                    ) {
                        Text(
                            text = when (account.role) {
                                "MEDICAL_PROFESSIONAL" -> "Doctor ID"
                                "CAREGIVER" -> "Caregiver ID"
                                "ADMIN" -> "Admin ID"
                                else -> "Patient ID"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkyLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Prominent ID Display Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "OFFICIAL RECORD ID",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = account.userId,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SkyLight,
                                letterSpacing = 2.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x2210B981)
                        ) {
                            Text(
                                text = "VERIFIED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Structural Explanation
                when (account.role) {
                    "PATIENT" -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x1A38BDF8),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "ID Structure Breakdown:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyLight
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "• Digit 1: '2' (Patient indicator)\n• Digits 2-5: '${account.assignedDoctorId.ifEmpty { "1001" }}' (Doctor Reference ID)\n• Digits 6-8: '${account.userId.takeLast(3)}' (Patient sequence index)",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        // Doctor Link Section for Patient
                        Divider(color = Color(0x33475569))
                        Text(
                            text = "Attending Doctor Connection",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = doctorIdInput,
                                onValueChange = { if (it.length <= 6) doctorIdInput = it },
                                label = { Text("Doctor 4-Digit ID", color = Color(0xFFCBD5E1)) },
                                placeholder = { Text("e.g. 1001", color = Color(0xFF64748B)) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("patient_doctor_id_input"),
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = SkyLight,
                                    unfocusedBorderColor = Color(0xFF475569)
                                )
                            )

                            Button(
                                onClick = { onLinkDoctor(doctorIdInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = SkyLight),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("patient_connect_doctor_button")
                            ) {
                                Text("Link Doctor", color = NavyDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                    "MEDICAL_PROFESSIONAL" -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x1A38BDF8),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Doctor ID Format: 1001",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyLight
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "• Starts with '1' (Doctor indicator) followed by 3 ascending digits (e.g. 001).\n• Share your 4-digit ID with patients so their 8-digit chart ID links to your portal.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                    "CAREGIVER" -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x1A8B5CF6),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Caregiver ID: ${account.userId} • Assigned Patient: ${account.assignedPatientId.ifEmpty { "21001001" }}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC4B5FD)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Caregivers are assigned IDs starting with '3'. You are linked to Patient chart ID ${account.assignedPatientId.ifEmpty { "21001001" }}.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Personal Details Form ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("personal_info_subtab_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Personal Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                // 1. Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                )

                // 2. Age & 3. Contact Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        placeholder = { Text("e.g. 68") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("profile_age_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Number") },
                        placeholder = { Text("e.g. +1 (555) 234-5678") },
                        singleLine = true,
                        modifier = Modifier.weight(1.8f).testTag("profile_contact_input")
                    )
                }

                // 4. Blood Group
                OutlinedTextField(
                    value = bloodGroup,
                    onValueChange = { bloodGroup = it },
                    label = { Text("Blood Group") },
                    placeholder = { Text("e.g. O+, A+, B+, AB-") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_blood_group_input")
                )

                // 5. Diagnosis
                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("Diagnosis") },
                    placeholder = { Text("e.g. Hypertension, Type-2 Diabetes Mellitus") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("profile_diagnosis_input")
                )

                // 6. Medical History
                OutlinedTextField(
                    value = medicalHistory,
                    onValueChange = { medicalHistory = it },
                    label = { Text("Medical History") },
                    placeholder = { Text("e.g. Coronary artery disease (2021), mild asthma, penicillin allergy") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("profile_medical_history_input")
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val updated = account.copy(
                            name = name,
                            age = age,
                            phone = phone,
                            diagnosis = diagnosis,
                            bloodGroup = bloodGroup,
                            medicalHistory = medicalHistory
                        )
                        onSave(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("save_personal_info_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 2: Caregiver Circle (Sub-tabs: 1. Linked Accounts, 2. Add New Member, 3. Permissions Matrix)
// ----------------------------------------------------
@Composable
private fun CaregiverCircleTabContent(
    activeAccount: UserAccountEntity,
    allAccounts: List<UserAccountEntity>,
    selectedSubTab: Int,
    onSubTabChange: (Int) -> Unit,
    onSwitchAccount: (String) -> Unit,
    onAddUserClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Sub-Tab Row Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedSubTab == 0,
                onClick = { onSubTabChange(0) },
                label = { Text("Linked Accounts", fontSize = 11.5.sp) },
                leadingIcon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null, modifier = Modifier.size(14.dp)) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = selectedSubTab == 1,
                onClick = { onSubTabChange(1) },
                label = { Text("Access Matrix", fontSize = 11.5.sp) },
                leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(14.dp)) },
                modifier = Modifier.weight(1f)
            )
        }

        if (selectedSubTab == 0) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("caregiver_accounts_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Family & Caregiver Circle", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Text("Active profiles with portal access", fontSize = 11.sp, color = Color(0xFF64748B))
                        }

                        Button(
                            onClick = onAddUserClick,
                            colors = ButtonDefaults.buttonColors(containerColor = SkyLight, contentColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_user_subtab_button")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Add Member", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    allAccounts.forEach { acc ->
                        val isCurrent = acc.userId == activeAccount.userId
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCurrent) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) TealAccent else Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { if (!isCurrent) onSwitchAccount(acc.userId) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (acc.role) {
                                                    "MEDICAL_PROFESSIONAL" -> Color(0xFF0284C7)
                                                    "CAREGIVER" -> Color(0xFF8B5CF6)
                                                    "ADMIN" -> Color(0xFFD97706)
                                                    else -> Color(0xFF10B981)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(acc.avatarInitials, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(acc.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        Text(
                                            text = "${acc.relationship.ifEmpty { acc.role }} • Member ID: ${acc.userId}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                if (isCurrent) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFD1FAE5)) {
                                        Text("Active", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                } else {
                                    Text("Switch", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealAccent)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Permissions Matrix
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Role-Based Security & Permissions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    PermissionRowItem(role = "Primary Patient", permission = "Full Read & Write Access across all Vitals, Medications, Labs & Chat.")
                    PermissionRowItem(role = "Caregiver", permission = "Read Telemetry & Log Medication Doses with time-stamped signature.")
                    PermissionRowItem(role = "Attending Doctor", permission = "Clinical Orders, Prescriptions, Lab Interpretations & Direct Secure Alerts.")
                    PermissionRowItem(role = "Administrator", permission = "System audit logs, E2EE key distribution & maintenance broadcasts.")
                }
            }
        }
    }
}

@Composable
private fun PermissionRowItem(role: String, permission: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = role, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = permission, fontSize = 11.sp, color = Color(0xFF475569))
        }
    }
}

// ----------------------------------------------------
// TAB 3: Prescriptions (Sub-tabs: 1. Active Prescriptions, 2. Paused / Past Meds)
// ----------------------------------------------------
@Composable
private fun PrescriptionsTabContent(
    allMedications: List<MedicationEntity>,
    selectedSubTab: Int,
    onSubTabChange: (Int) -> Unit,
    onAddMedClick: () -> Unit,
    onEditMedClick: (MedicationEntity) -> Unit,
    onToggleStatus: (Long, Boolean) -> Unit,
    onDeleteMed: (Long) -> Unit
) {
    val runningMeds = allMedications.filter { it.status == "RUNNING" }
    val stoppedMeds = allMedications.filter { it.status == "STOPPED" }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Sub-Tab Row Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedSubTab == 0,
                onClick = { onSubTabChange(0) },
                label = { Text("Running (${runningMeds.size})", fontSize = 11.5.sp) },
                leadingIcon = { Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(14.dp)) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = selectedSubTab == 1,
                onClick = { onSubTabChange(1) },
                label = { Text("Stopped (${stoppedMeds.size})", fontSize = 11.5.sp) },
                leadingIcon = { Icon(Icons.Default.PauseCircle, contentDescription = null, modifier = Modifier.size(14.dp)) },
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("prescriptions_management_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedSubTab == 0) "Active Running Prescriptions" else "Paused & Past Prescriptions",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text("Medicine name, dosage & schedule", fontSize = 11.sp, color = Color(0xFF64748B))
                    }

                    Button(
                        onClick = onAddMedClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyLight, contentColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_prescription_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Med", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val displayList = if (selectedSubTab == 0) runningMeds else stoppedMeds

                if (displayList.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = if (selectedSubTab == 0) "No active running prescriptions. Tap 'Add Med' to register new medicine." else "No paused medications.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                } else {
                    displayList.forEach { med ->
                        val isRunning = med.status == "RUNNING"
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isRunning) Color(0xFFF8FAFC) else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isRunning) Color(0xFFCBD5E1) else Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("med_card_${med.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(med.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isRunning) NavyPrimary else Color(0xFF64748B))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(shape = RoundedCornerShape(4.dp), color = if (isRunning) SkyLight else Color(0xFFE2E8F0)) {
                                                Text(med.dosage, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isRunning) NavyPrimary else Color(0xFF64748B), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "${med.frequency} • ${med.scheduledTime}", fontSize = 11.5.sp, color = Color(0xFF64748B))
                                        if (med.startDateFormatted.isNotBlank() || med.endDateFormatted.isNotBlank()) {
                                            Text(
                                                text = "Timeline: ${med.startDateFormatted.ifEmpty { "Start" }} → ${med.endDateFormatted.ifEmpty { "Ongoing" }}",
                                                fontSize = 10.5.sp,
                                                color = Color(0xFF0284C7)
                                            )
                                        }
                                    }

                                    // Running/Stopped Toggle
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isRunning) "RUNNING" else "STOPPED",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isRunning) HealthNormalGreen else HealthCriticalRed,
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                        Switch(
                                            checked = isRunning,
                                            onCheckedChange = { onToggleStatus(med.id, it) },
                                            colors = SwitchDefaults.colors(checkedThumbColor = HealthNormalGreen, checkedTrackColor = Color(0xFFD1FAE5)),
                                            modifier = Modifier.testTag("med_toggle_${med.id}")
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Prescribed by: ${med.prescribedBy}", fontSize = 10.5.sp, color = Color(0xFF94A3B8))
                                    Row {
                                        IconButton(onClick = { onEditMedClick(med) }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TealAccent, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(onClick = { onDeleteMed(med.id) }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
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

// ----------------------------------------------------
// TAB 4: Security & Access (Sub-tabs: 1. Biometrics & MFA, 2. E2EE Encryption, 3. Sign Out)
// ----------------------------------------------------
@Composable
private fun SecurityAccessTabContent(
    account: UserAccountEntity,
    selectedSubTab: Int,
    onSubTabChange: (Int) -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    onToggleMfa: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Sub-Tab Row Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedSubTab == 0,
                onClick = { onSubTabChange(0) },
                label = { Text("Auth & 2FA", fontSize = 11.5.sp) },
                leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(14.dp)) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = selectedSubTab == 1,
                onClick = { onSubTabChange(1) },
                label = { Text("Encryption & Session", fontSize = 11.5.sp) },
                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp)) },
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("security_access_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedSubTab == 0) {
                    Text("Biometric & MFA Authentication", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Biometric Authentication Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Biometric Authentication", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                Text("Unlock portal with Fingerprint / Face scan", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                        Switch(
                            checked = account.biometricEnabled,
                            onCheckedChange = onToggleBiometric,
                            colors = SwitchDefaults.colors(checkedThumbColor = TealAccent, checkedTrackColor = SkyLight),
                            modifier = Modifier.testTag("biometric_toggle_switch")
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    // MFA (2FA) Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Multi-Factor Authentication (2FA)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                Text("Require 6-digit OTP on sign in", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                        Switch(
                            checked = account.mfaEnabled,
                            onCheckedChange = onToggleMfa,
                            colors = SwitchDefaults.colors(checkedThumbColor = TealAccent, checkedTrackColor = SkyLight),
                            modifier = Modifier.testTag("mfa_toggle_switch")
                        )
                    }
                } else {
                    Text("End-to-End Encryption & Active Session", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = HealthNormalGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AES-256-CBC End-to-End Encryption Active. Health metrics, blood tests & provider messages remain private and HIPAA compliant.",
                                fontSize = 11.sp,
                                color = Color(0xFF166534)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onLogout,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("profile_logout_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = HealthCriticalRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out of Secure Portal", color = HealthCriticalRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Medication Edit Dialog Component
// ----------------------------------------------------
@Composable
fun MedicationEditDialog(
    initialMedication: MedicationEntity?,
    patientId: String,
    onDismiss: () -> Unit,
    onSave: (MedicationEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialMedication?.name ?: "") }
    var dosage by remember { mutableStateOf(initialMedication?.dosage ?: "") }
    var frequency by remember { mutableStateOf(initialMedication?.frequency ?: "Once daily") }
    var route by remember { mutableStateOf(initialMedication?.route ?: "Oral") }
    var scheduledTime by remember { mutableStateOf(initialMedication?.scheduledTime ?: "08:00 AM") }
    var startDate by remember { mutableStateOf(initialMedication?.startDateFormatted ?: "May 10, 2026") }
    var endDate by remember { mutableStateOf(initialMedication?.endDateFormatted ?: "") }
    var instructions by remember { mutableStateOf(initialMedication?.instructions ?: "") }
    var prescribedBy by remember { mutableStateOf(initialMedication?.prescribedBy ?: "Dr. Sarah Jenkins, MD") }
    var isRunning by remember { mutableStateOf(initialMedication?.status != "STOPPED") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Medication, contentDescription = null, tint = TealAccent, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (initialMedication == null) "Add Prescription" else "Edit Prescription",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medicine Name *") },
                    placeholder = { Text("e.g. Lisinopril, Metformin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_med_name_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage *") },
                        placeholder = { Text("e.g. 10 mg") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("dialog_med_dosage_input")
                    )
                    OutlinedTextField(
                        value = scheduledTime,
                        onValueChange = { scheduledTime = it },
                        label = { Text("Time") },
                        placeholder = { Text("08:00 AM") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Start Date") },
                        placeholder = { Text("May 10, 2026") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("End Date") },
                        placeholder = { Text("Ongoing or Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequency") },
                    placeholder = { Text("Once daily, Twice daily, etc.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = route,
                    onValueChange = { route = it },
                    label = { Text("Route") },
                    placeholder = { Text("Oral, Injection, etc.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    placeholder = { Text("Take with food and water") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && dosage.isNotBlank()) {
                        val med = (initialMedication ?: MedicationEntity(patientId = patientId, name = name, dosage = dosage, frequency = frequency)).copy(
                            name = name,
                            dosage = dosage,
                            frequency = frequency,
                            route = route,
                            scheduledTime = scheduledTime,
                            startDateFormatted = startDate,
                            endDateFormatted = endDate,
                            instructions = instructions,
                            prescribedBy = prescribedBy,
                            status = if (isRunning) "RUNNING" else "STOPPED"
                        )
                        onSave(med)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("dialog_save_med_btn")
            ) {
                Text("Save Prescription")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
