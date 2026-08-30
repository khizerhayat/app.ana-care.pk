package com.example.ui.screens.subtabs

import android.content.res.Configuration
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.MedicationEntity
import com.example.ui.components.MedicalGallerySection
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.PortalViewModel



@Composable
fun AddRecordsScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSubTab by viewModel.addRecordsSubTab.collectAsState()
    val scrollState = rememberScrollState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("add_records_screen")
    ) {
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
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { viewModel.setAddRecordsSubTab(0) },
                    text = if (isLandscape) { { Text("Vital Signs", fontSize = 12.5.sp, fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Normal) } } else null,
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Vitals Tab", modifier = Modifier.size(18.dp)) },
                    selectedContentColor = SkyLight,
                    unselectedContentColor = Color(0xFF94A3B8),
                    modifier = Modifier.testTag("subtab_add_vitals")
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { viewModel.setAddRecordsSubTab(1) },
                    text = if (isLandscape) { { Text("Medications", fontSize = 12.5.sp, fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Normal) } } else null,
                    icon = { Icon(Icons.Default.Medication, contentDescription = "Medications Tab", modifier = Modifier.size(18.dp)) },
                    selectedContentColor = SkyLight,
                    unselectedContentColor = Color(0xFF94A3B8),
                    modifier = Modifier.testTag("subtab_add_medications")
                )
                Tab(
                    selected = selectedSubTab == 2,
                    onClick = { viewModel.setAddRecordsSubTab(2) },
                    text = if (isLandscape) { { Text("Daily Activities", fontSize = 12.5.sp, fontWeight = if (selectedSubTab == 2) FontWeight.Bold else FontWeight.Normal) } } else null,
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Activities Tab", modifier = Modifier.size(18.dp)) },
                    selectedContentColor = SkyLight,
                    unselectedContentColor = Color(0xFF94A3B8),
                    modifier = Modifier.testTag("subtab_add_activities")
                )
                Tab(
                    selected = selectedSubTab == 3,
                    onClick = { viewModel.setAddRecordsSubTab(3) },
                    text = if (isLandscape) { { Text("Medical Gallery", fontSize = 12.5.sp, fontWeight = if (selectedSubTab == 3) FontWeight.Bold else FontWeight.Normal) } } else null,
                    icon = { Icon(Icons.Default.Collections, contentDescription = "Gallery Tab", modifier = Modifier.size(18.dp)) },
                    selectedContentColor = SkyLight,
                    unselectedContentColor = Color(0xFF94A3B8),
                    modifier = Modifier.testTag("subtab_add_gallery")
                )
            }
        }

        // Sub Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            when (selectedSubTab) {
                0 -> AddVitalsSubTab(viewModel = viewModel)
                1 -> AddMedicationsSubTab(viewModel = viewModel)
                2 -> AddActivitiesSubTab(viewModel = viewModel)
                3 -> MedicalGallerySection(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun AddVitalsSubTab(
    viewModel: PortalViewModel
) {
    var systolic by remember { mutableStateOf("120") }
    var diastolic by remember { mutableStateOf("80") }
    var heartRate by remember { mutableStateOf("72") }
    var spo2 by remember { mutableStateOf("98") }
    var tempF by remember { mutableStateOf("98.6") }
    var glucose by remember { mutableStateOf("100") }
    var respRate by remember { mutableStateOf("16") }
    var weightLbs by remember { mutableStateOf("142.0") }
    var notes by remember { mutableStateOf("") }

    // Live Classification
    val sysInt = systolic.toIntOrNull() ?: 120
    val diaInt = diastolic.toIntOrNull() ?: 80
    val hrInt = heartRate.toIntOrNull() ?: 72
    val spo2Int = spo2.toIntOrNull() ?: 98
    val gluInt = glucose.toIntOrNull() ?: 100
    val tempFloat = tempF.toFloatOrNull() ?: 98.6f

    val isNormal = sysInt < 130 && diaInt < 85 && spo2Int >= 95 && hrInt in 60..100 && tempFloat < 99.5f && gluInt < 140
    val isCritical = sysInt >= 160 || diaInt >= 100 || spo2Int < 92 || hrInt > 120 || tempFloat >= 101.5f || gluInt > 220

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("add_vitals_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Record Vital Signs",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Encrypted On-Device & Cloud Sync",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isCritical -> Color(0xFFFEE2E2)
                        isNormal -> Color(0xFFD1FAE5)
                        else -> Color(0xFFFEF3C7)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCritical -> HealthCriticalRed
                                        isNormal -> HealthNormalGreen
                                        else -> HealthWarningAmber
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = when {
                                isCritical -> "Critical Alert"
                                isNormal -> "Normal Range"
                                else -> "Elevated Range"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCritical -> Color(0xFF991B1B)
                                isNormal -> Color(0xFF065F46)
                                else -> Color(0xFF92400E)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Blood Pressure
            Text("Blood Pressure (mmHg) *", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = systolic,
                    onValueChange = { systolic = it },
                    label = { Text("Systolic (e.g. 120)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("vitals_systolic_input")
                )
                OutlinedTextField(
                    value = diastolic,
                    onValueChange = { diastolic = it },
                    label = { Text("Diastolic (e.g. 80)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("vitals_diastolic_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Heart Rate & SpO2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = heartRate,
                    onValueChange = { heartRate = it },
                    label = { Text("Heart Rate (bpm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("vitals_hr_input")
                )
                OutlinedTextField(
                    value = spo2,
                    onValueChange = { spo2 = it },
                    label = { Text("Oxygen SpO2 (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("vitals_spo2_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Temp & Blood Glucose
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = tempF,
                    onValueChange = { tempF = it },
                    label = { Text("Body Temp (°F)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("vitals_temp_input")
                )
                OutlinedTextField(
                    value = glucose,
                    onValueChange = { glucose = it },
                    label = { Text("Blood Glucose (mg/dL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("vitals_glucose_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Respiratory Rate & Weight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = respRate,
                    onValueChange = { respRate = it },
                    label = { Text("Resp. Rate (/min)") },
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

            Spacer(modifier = Modifier.height(12.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Clinical Notes / Symptoms / Context") },
                placeholder = { Text("e.g., Felt mild dizziness after afternoon walk; took rest.") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth().testTag("vitals_notes_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.recordVitals(
                        systolic = sysInt,
                        diastolic = diaInt,
                        heartRate = hrInt,
                        spo2 = spo2Int,
                        tempF = tempFloat,
                        glucose = gluInt,
                        respRate = respRate.toIntOrNull() ?: 16,
                        weightLbs = weightLbs.toFloatOrNull() ?: 142f,
                        notes = notes
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_vitals_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Vitals to Encrypted Portal", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AddMedicationsSubTab(
    viewModel: PortalViewModel
) {
    val runningMedications by viewModel.runningMedicationsList.collectAsState()
    val medicationLogs by viewModel.medicationLogsList.collectAsState()

    var showAddForm by remember { mutableStateOf(true) }
    var newMedName by remember { mutableStateOf("") }
    var newDosage by remember { mutableStateOf("") }
    var newStartDate by remember { mutableStateOf("May 10, 2026") }
    var newEndDate by remember { mutableStateOf("") }
    var newFrequency by remember { mutableStateOf("Once daily") }
    var newTime by remember { mutableStateOf("08:00 AM") }
    var newInstructions by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("add_medications_card"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Add Prescription Form (Medicine Name, Dosage, Start Date, End Date in a single tab)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("add_prescription_form_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Medication, contentDescription = null, tint = TealAccent, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Add Prescription Details",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text(
                                text = "Name, dosage, start and end dates",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showAddForm = !showAddForm },
                        modifier = Modifier.testTag("toggle_med_form_button")
                    ) {
                        Text(
                            text = if (showAddForm) "−" else "+",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealAccent
                        )
                    }
                }

                if (showAddForm) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Medicine Name
                    OutlinedTextField(
                        value = newMedName,
                        onValueChange = { newMedName = it },
                        label = { Text("Medicine Name *") },
                        placeholder = { Text("e.g. Metformin, Lisinopril, Atorvastatin") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_med_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dosage & Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = newDosage,
                            onValueChange = { newDosage = it },
                            label = { Text("Dosage *") },
                            placeholder = { Text("e.g. 500 mg, 10 mg") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("add_med_dosage_input")
                        )

                        OutlinedTextField(
                            value = newTime,
                            onValueChange = { newTime = it },
                            label = { Text("Scheduled Time") },
                            placeholder = { Text("08:00 AM") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("add_med_time_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Start Date & End Date (Single Tab Unified Form)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = newStartDate,
                            onValueChange = { newStartDate = it },
                            label = { Text("Start Date *") },
                            placeholder = { Text("May 10, 2026") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("add_med_start_date_input")
                        )

                        OutlinedTextField(
                            value = newEndDate,
                            onValueChange = { newEndDate = it },
                            label = { Text("End Date") },
                            placeholder = { Text("e.g. Jun 10, 2026 / Ongoing") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("add_med_end_date_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Frequency & Instructions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = newFrequency,
                            onValueChange = { newFrequency = it },
                            label = { Text("Frequency") },
                            placeholder = { Text("Once daily, Twice daily") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = newInstructions,
                            onValueChange = { newInstructions = it },
                            label = { Text("Instructions") },
                            placeholder = { Text("With meals") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (newMedName.isNotBlank() && newDosage.isNotBlank()) {
                                viewModel.recordMedication(
                                    name = newMedName.trim(),
                                    dosage = newDosage.trim(),
                                    frequency = newFrequency.trim(),
                                    route = "Oral",
                                    scheduledTime = newTime.trim(),
                                    startDateFormatted = newStartDate.trim(),
                                    endDateFormatted = newEndDate.trim(),
                                    instructions = newInstructions.trim(),
                                    prescribedBy = "Dr. Sarah Jenkins, MD"
                                )
                                newMedName = ""
                                newDosage = ""
                                newInstructions = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_new_prescription_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Add Prescription", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Today's Running Schedule (Taken / Skipped)
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
                    Text(
                        text = "Today's Active Dose Tracker",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD1FAE5)
                    ) {
                        Text(
                            text = "${runningMedications.size} Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (runningMedications.isEmpty()) {
                    Text(
                        text = "No active medications scheduled. Add a prescription above to track daily doses.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    runningMedications.forEach { med ->
                        val isTaken = med.lastAction == "TAKEN"
                        val isSkipped = med.lastAction == "SKIPPED"

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isTaken -> Color(0xFFF0FDF4)
                                isSkipped -> Color(0xFFFFFBEB)
                                else -> Color(0xFFF8FAFC)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when {
                                    isTaken -> Color(0xFF86EFAC)
                                    isSkipped -> Color(0xFFFDE68A)
                                    else -> Color(0xFFE2E8F0)
                                }
                            ),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("running_med_card_${med.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(med.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(4.dp), color = SkyLight) {
                                            Text(med.dosage, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }

                                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE0F2FE)) {
                                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Schedule, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(med.scheduledTime, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        }
                                    }
                                }

                                if (med.startDateFormatted.isNotBlank() || med.endDateFormatted.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Duration: ${med.startDateFormatted.ifEmpty { "Start" }} ${if (med.endDateFormatted.isNotBlank()) "→ ${med.endDateFormatted}" else "(Ongoing)"}",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF0284C7)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.markMedicationTaken(med.id, med.name, med.dosage) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isTaken) Color(0xFF059669) else HealthNormalGreen
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(38.dp).testTag("mark_taken_button_${med.id}")
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isTaken) "Taken ✓" else "Mark Taken", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.markMedicationSkipped(med.id, med.name, med.dosage) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFFD97706)
                                        ),
                                        modifier = Modifier.weight(1f).height(38.dp).testTag("mark_skipped_button_${med.id}")
                                    ) {
                                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFD97706))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isSkipped) "Skipped ⊘" else "Skip Dose", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Record of Medicines Already Given (Historical Administration Logs)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("medication_history_log_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Record of Medicines Already Given",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }

                    Text(
                        text = "${medicationLogs.size} records",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (medicationLogs.isEmpty()) {
                    Text(
                        text = "No doses recorded yet. When you tap 'Mark Taken' or 'Skip Dose', a verified administration log is saved here automatically.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        medicationLogs.take(10).forEach { log ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (log.status == "TAKEN") Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (log.status == "TAKEN") HealthNormalGreen else HealthCriticalRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${log.medicationName} • ${log.dosage}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                        Text(
                                            text = "${log.administeredDateFormatted} • By ${log.administeredBy}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
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


@Composable
private fun AddActivitiesSubTab(
    viewModel: PortalViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val activityTypes = listOf(
        "Walking / Steps" to Icons.Default.DirectionsWalk,
        "Physical Therapy" to Icons.Default.MedicalServices,
        "Exercise & Mobility" to Icons.Default.FitnessCenter,
        "Sleep & Rest" to Icons.Default.NightsStay,
        "Water Intake" to Icons.Default.LocalDrink,
        "Diet & Nutrition" to Icons.Default.Restaurant
    )

    var selectedType by remember { mutableStateOf("Walking / Steps") }
    var durationMinutes by remember { mutableStateOf("30") }
    var metricValue by remember { mutableStateOf("4,200 steps") }
    var painScore by remember { mutableFloatStateOf(1f) }
    var selectedMood by remember { mutableStateOf("Good") }
    var notes by remember { mutableStateOf("") }

    val moods = listOf(
        "Great" to "😄",
        "Good" to "🙂",
        "Neutral" to "😐",
        "Tired" to "🥱",
        "In Pain" to "😣"
    )

    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left Column: Activity Logging Form
            Card(
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("add_activities_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Log Daily Activity & Wellness",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Track rehabilitation progress, mobility, pain and mood",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Activity Type Selector Chips
                    Text("Select Activity Type", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activityTypes.take(3).forEach { (type, icon) ->
                            val isSel = selectedType == type
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealAccent else Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedType = type
                                        if (type.contains("Walking")) metricValue = "4,200 steps"
                                        if (type.contains("Therapy")) metricValue = "Lower body routine"
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(icon, contentDescription = null, tint = if (isSel) TealAccent else Color(0xFF64748B), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = type.split(" ").first(),
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) NavyPrimary else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activityTypes.drop(3).forEach { (type, icon) ->
                            val isSel = selectedType == type
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealAccent else Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedType = type
                                        if (type.contains("Sleep")) metricValue = "8.0 hours"
                                        if (type.contains("Water")) metricValue = "2,000 mL"
                                        if (type.contains("Diet")) metricValue = "Low sodium meal"
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(icon, contentDescription = null, tint = if (isSel) TealAccent else Color(0xFF64748B), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = type.split(" ").first(),
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) NavyPrimary else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Duration & Metric Value
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = durationMinutes,
                            onValueChange = { durationMinutes = it },
                            label = { Text("Duration (mins)", fontSize = 11.5.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f).testTag("activity_duration_input")
                        )
                        OutlinedTextField(
                            value = metricValue,
                            onValueChange = { metricValue = it },
                            label = { Text("Summary / Value", fontSize = 11.5.sp) },
                            placeholder = { Text("e.g. 5,000 steps") },
                            modifier = Modifier.weight(1.2f).testTag("activity_metric_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pain Score
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pain Level:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                painScore.toInt() >= 7 -> Color(0xFFFEE2E2)
                                painScore.toInt() >= 4 -> Color(0xFFFEF3C7)
                                else -> Color(0xFFD1FAE5)
                            }
                        ) {
                            Text(
                                text = "${painScore.toInt()}/10",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    painScore.toInt() >= 7 -> HealthCriticalRed
                                    painScore.toInt() >= 4 -> HealthWarningAmber
                                    else -> Color(0xFF065F46)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Slider(
                        value = painScore,
                        onValueChange = { painScore = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = TealAccent, activeTrackColor = TealAccent),
                        modifier = Modifier.fillMaxWidth().testTag("pain_score_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Activity Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Activity / Exercise Notes") },
                        placeholder = { Text("e.g., Routine performed smoothly with assistance.") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("activity_notes_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.recordDailyActivity(
                                activityType = selectedType,
                                durationMinutes = durationMinutes.toIntOrNull() ?: 30,
                                metricValue = metricValue.ifEmpty { "Completed" },
                                painScore = painScore.toInt(),
                                mood = selectedMood,
                                notes = notes
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_activity_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Activity Record", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Right Column: Medical & Activity Gallery Component (Beside Notes)
            Box(modifier = Modifier.weight(0.9f)) {
                MedicalGallerySection(viewModel = viewModel)
            }
        }
    } else {
        // Portrait Layout: Activity Form and Medical Gallery Card stacked with dedicated section beside notes
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_activities_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Log Daily Activity & Wellness",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Track rehabilitation progress, mobility, pain and mood",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Activity Type Selector Chips
                    Text("Select Activity Type", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activityTypes.take(3).forEach { (type, icon) ->
                            val isSel = selectedType == type
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealAccent else Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedType = type
                                        if (type.contains("Walking")) metricValue = "4,200 steps"
                                        if (type.contains("Therapy")) metricValue = "Lower body routine"
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(icon, contentDescription = null, tint = if (isSel) TealAccent else Color(0xFF64748B), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = type.split(" ").first(),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) NavyPrimary else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activityTypes.drop(3).forEach { (type, icon) ->
                            val isSel = selectedType == type
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealAccent else Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedType = type
                                        if (type.contains("Sleep")) metricValue = "8.0 hours"
                                        if (type.contains("Water")) metricValue = "2,000 mL"
                                        if (type.contains("Diet")) metricValue = "Low sodium meal"
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(icon, contentDescription = null, tint = if (isSel) TealAccent else Color(0xFF64748B), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = type.split(" ").first(),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) NavyPrimary else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Duration & Metric Value
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = durationMinutes,
                            onValueChange = { durationMinutes = it },
                            label = { Text("Duration (mins)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f).testTag("activity_duration_input")
                        )
                        OutlinedTextField(
                            value = metricValue,
                            onValueChange = { metricValue = it },
                            label = { Text("Measurement / Summary") },
                            placeholder = { Text("e.g. 5,000 steps, 2.5L water") },
                            modifier = Modifier.weight(1.2f).testTag("activity_metric_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pain Score (0-10) Interactive Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pain Level (0 - 10):", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                painScore.toInt() >= 7 -> Color(0xFFFEE2E2)
                                painScore.toInt() >= 4 -> Color(0xFFFEF3C7)
                                else -> Color(0xFFD1FAE5)
                            }
                        ) {
                            Text(
                                text = "${painScore.toInt()} / 10 ${if (painScore.toInt() == 0) "(No Pain)" else if (painScore.toInt() < 4) "(Mild)" else if (painScore.toInt() < 7) "(Moderate)" else "(Severe)"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    painScore.toInt() >= 7 -> HealthCriticalRed
                                    painScore.toInt() >= 4 -> HealthWarningAmber
                                    else -> Color(0xFF065F46)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Slider(
                        value = painScore,
                        onValueChange = { painScore = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = TealAccent,
                            activeTrackColor = TealAccent
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("pain_score_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mood Selector
                    Text("How are you feeling today?", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        moods.forEach { (moodName, emoji) ->
                            val isSel = selectedMood == moodName
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFFE0F2FE) else Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealAccent else Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedMood = moodName }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(emoji, fontSize = 18.sp)
                                    Text(
                                        text = moodName,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) NavyPrimary else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Activity Notes Section
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Activity / Exercise Notes") },
                        placeholder = { Text("e.g., Routine performed smoothly with assistance.") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("activity_notes_input")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            viewModel.recordDailyActivity(
                                activityType = selectedType,
                                durationMinutes = durationMinutes.toIntOrNull() ?: 30,
                                metricValue = metricValue.ifEmpty { "Completed" },
                                painScore = painScore.toInt(),
                                mood = selectedMood,
                                notes = notes
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_activity_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Daily Activity Record", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
