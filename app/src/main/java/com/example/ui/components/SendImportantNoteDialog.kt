package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.UserAccountEntity
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SkyLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendImportantNoteDialog(
    senderRole: String, // "DOCTOR" or "ADMIN"
    senderName: String,
    patientList: List<UserAccountEntity>,
    preselectedPatientId: String? = null,
    onSend: (targetPatientId: String, targetPatientName: String, title: String, message: String, severity: String, actionLink: String) -> Unit,
    onDismiss: () -> Unit
) {
    val eligiblePatients = patientList.filter { it.role == "PATIENT" }
    
    var selectedTargetId by remember {
        mutableStateOf(
            if (preselectedPatientId != null && preselectedPatientId != "ALL") {
                preselectedPatientId
            } else if (senderRole == "ADMIN") {
                "ALL"
            } else {
                eligiblePatients.firstOrNull()?.userId ?: "ALL"
            }
        )
    }

    var selectedTargetName by remember {
        mutableStateOf(
            if (selectedTargetId == "ALL") "All Registered Patients"
            else eligiblePatients.find { it.userId == selectedTargetId }?.name ?: "Selected Patient"
        )
    }

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedSeverity by remember { mutableStateOf(if (senderRole == "DOCTOR") "MEDICATION_ALERT" else "INFO") } // "URGENT", "MEDICATION_ALERT", "WARNING", "INFO"
    var selectedActionLink by remember { mutableStateOf("NONE") } // "MEDICATIONS", "LABS", "VITALS", "APPOINTMENTS", "NONE"
    var isTargetDropdownExpanded by remember { mutableStateOf(false) }

    // Quick templates
    val doctorTemplates = listOf(
        "Fasting Notice: Please avoid solid food or sweet drinks 8 hrs prior to morning lab draws." to "MEDICATION_ALERT",
        "Urgent: Recent blood pressure elevated. Please sit, rest 10 minutes, and take a repeat reading." to "URGENT",
        "Prescription Update: New Lisinopril dosage active in your chart. Stop old tablets." to "MEDICATION_ALERT",
        "Follow-up Notice: Dr. Jenkins requested a telehealth check-in regarding recent glucose spikes." to "WARNING"
    )

    val adminTemplates = listOf(
        "System Notice: ANA Care 24/7 Home Health Emergency Line is live and monitored." to "INFO",
        "Important: Scheduled portal maintenance on Sunday 2:00 AM - 3:00 AM EST." to "WARNING",
        "Urgent Safety: Please verify your emergency contact number and caregiver permissions in Profile." to "URGENT"
    )

    val templates = if (senderRole == "DOCTOR") doctorTemplates else adminTemplates

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("send_important_note_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (senderRole == "ADMIN") Color(0xFFD97706) else NavyPrimary)
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (senderRole == "ADMIN") Icons.Default.AdminPanelSettings else Icons.Default.MedicalServices,
                                        contentDescription = "Sender",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (senderRole == "ADMIN") "Admin Important Note Dispatcher" else "Doctor Important Pop-up Note",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Direct Pop-up on Patient's Screen",
                                    fontSize = 11.sp,
                                    color = SkyLight
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Body Form
                Column(modifier = Modifier.padding(18.dp)) {
                    // Target Patient Selector
                    Text(
                        text = "TARGET PATIENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = isTargetDropdownExpanded,
                        onExpandedChange = { isTargetDropdownExpanded = !isTargetDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedTargetName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTargetDropdownExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("target_patient_dropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = isTargetDropdownExpanded,
                            onDismissRequest = { isTargetDropdownExpanded = false }
                        ) {
                            if (senderRole == "ADMIN") {
                                DropdownMenuItem(
                                    text = { Text("📢 All Patients (System Broadcast)", fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        selectedTargetId = "ALL"
                                        selectedTargetName = "All Registered Patients"
                                        isTargetDropdownExpanded = false
                                    }
                                )
                            }
                            eligiblePatients.forEach { patient ->
                                DropdownMenuItem(
                                    text = { Text("${patient.name} (${patient.userId})") },
                                    onClick = {
                                        selectedTargetId = patient.userId
                                        selectedTargetName = patient.name
                                        isTargetDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Priority / Severity Chips
                    Text(
                        text = "ALERT PRIORITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "URGENT" to Color(0xFFDC2626),
                            "MEDICATION_ALERT" to Color(0xFFD97706),
                            "WARNING" to Color(0xFFEA580C),
                            "INFO" to Color(0xFF0284C7)
                        ).forEach { (sev, col) ->
                            FilterChip(
                                selected = selectedSeverity == sev,
                                onClick = { selectedSeverity = sev },
                                label = {
                                    Text(
                                        text = when (sev) {
                                            "URGENT" -> "Urgent"
                                            "MEDICATION_ALERT" -> "Med Alert"
                                            "WARNING" -> "Advisory"
                                            else -> "Info"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedSeverity == sev) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = col.copy(alpha = 0.15f),
                                    selectedLabelColor = col
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Templates
                    Text(
                        text = "QUICK CLINICAL TEMPLATES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        templates.forEach { (tmpl, sev) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val parts = tmpl.split(":", limit = 2)
                                        if (parts.size == 2) {
                                            title = parts[0].trim()
                                            message = parts[1].trim()
                                        } else {
                                            title = "Important Notice"
                                            message = tmpl
                                        }
                                        selectedSeverity = sev
                                    }
                            ) {
                                Text(
                                    text = "• $tmpl",
                                    fontSize = 11.sp,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Alert Title / Subject") },
                        placeholder = { Text("e.g. Fasting Notice for Tomorrow's Labs") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("alert_title_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Message input
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Important Note Details (Displayed as Pop-up)") },
                        placeholder = { Text("Type full clinical instructions or system announcement...") },
                        minLines = 3,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("alert_message_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Link Shortcut
                    Text(
                        text = "ATTACH ACTION SHORTCUT (OPTIONAL)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "NONE" to "None",
                            "MEDICATIONS" to "Meds",
                            "LABS" to "Labs",
                            "VITALS" to "Vitals"
                        ).forEach { (link, label) ->
                            FilterChip(
                                selected = selectedActionLink == link,
                                onClick = { selectedActionLink = link },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Dispatch Button
                    Button(
                        onClick = {
                            if (title.isNotBlank() && message.isNotBlank()) {
                                onSend(
                                    selectedTargetId,
                                    selectedTargetName,
                                    title.trim(),
                                    message.trim(),
                                    selectedSeverity,
                                    selectedActionLink
                                )
                            }
                        },
                        enabled = title.isNotBlank() && message.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (senderRole == "ADMIN") Color(0xFFD97706) else NavyPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dispatch_alert_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dispatch Important Pop-up Note",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
