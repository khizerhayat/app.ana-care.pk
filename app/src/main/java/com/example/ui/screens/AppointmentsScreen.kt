package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.AppointmentEntity
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.PortalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AppointmentsScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val appointments by viewModel.appointmentsList.collectAsState()
    var showScheduleDialog by remember { mutableStateOf(false) }
    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0: Upcoming, 1: Past / Completed

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val now = System.currentTimeMillis()
    val upcoming = appointments.filter { it.scheduledEpochMillis >= now - 3600 * 1000L }
    val past = appointments.filter { it.scheduledEpochMillis < now - 3600 * 1000L }

    val displayedList = if (selectedFilterTab == 0) upcoming else past
    val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("appointments_screen")
    ) {
        // Top Action Header
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Appointments & Care Visits",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Home visits, nursing & telemedicine consultations",
                            fontSize = 11.5.sp,
                            color = Color(0xFF93C5FD)
                        )
                    }

                    Button(
                        onClick = { showScheduleDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SkyLight, contentColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("schedule_appointment_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33000000))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (selectedFilterTab == 0) SkyLight else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFilterTab = 0 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Upcoming Visits",
                                tint = if (selectedFilterTab == 0) NavyPrimary else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            if (isLandscape) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Upcoming (${upcoming.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedFilterTab == 0) NavyPrimary else Color.White
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (selectedFilterTab == 1) SkyLight else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFilterTab = 1 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Past History",
                                tint = if (selectedFilterTab == 1) NavyPrimary else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            if (isLandscape) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Past History (${past.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedFilterTab == 1) NavyPrimary else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Appointments List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (displayedList.isEmpty()) {
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
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedFilterTab == 0) "No Upcoming Appointments" else "No Past Appointments",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text(
                                text = "Use the 'Schedule' button above to request a home healthcare visit or encrypted video consultation.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else {
                items(displayedList, key = { it.id }) { appt ->
                    AppointmentCard(
                        item = appt,
                        dateFormatted = dateFormat.format(Date(appt.scheduledEpochMillis)),
                        onCancel = { viewModel.cancelAppointment(appt.id) }
                    )
                }
            }
        }
    }

    if (showScheduleDialog) {
        ScheduleAppointmentDialog(
            onSchedule = { doc, spec, type, dateEpoch, timeSlot, reason, loc ->
                viewModel.bookAppointment(doc, spec, type, dateEpoch, timeSlot, 30, reason, loc)
                showScheduleDialog = false
            },
            onDismiss = { showScheduleDialog = false }
        )
    }
}

@Composable
private fun AppointmentCard(
    item: AppointmentEntity,
    dateFormatted: String,
    onCancel: () -> Unit
) {
    val isHomeVisit = item.appointmentType == "HOME_VISIT"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("appointment_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Type Badge & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isHomeVisit) Color(0xFFEFF6FF) else Color(0xFFF3E8FF)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isHomeVisit) Icons.Default.Home else Icons.Default.Videocam,
                                contentDescription = null,
                                tint = if (isHomeVisit) TealAccent else Color(0xFF9333EA),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHomeVisit) "Home Care Visit" else "Encrypted Video Consultation",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHomeVisit) NavyPrimary else Color(0xFF7E22CE)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (item.status) {
                        "CONFIRMED" -> Color(0xFFD1FAE5)
                        "COMPLETED" -> Color(0xFFF1F5F9)
                        else -> Color(0xFFFEE2E2)
                    }
                ) {
                    Text(
                        text = item.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (item.status) {
                            "CONFIRMED" -> Color(0xFF065F46)
                            "COMPLETED" -> Color(0xFF64748B)
                            else -> HealthCriticalRed
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Doctor Info
            Text(
                text = item.doctorName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Text(
                text = item.specialty,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Date & Time Box
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
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = TealAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(dateFormatted, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text(item.timeSlotString, fontSize = 11.5.sp, color = Color(0xFF475569))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reason & Location
            Text("Purpose: ${item.reason}", fontSize = 12.sp, color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(item.locationOrLink, fontSize = 11.sp, color = Color(0xFF64748B))
            }

            if (item.doctorNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Doctor's Notes: ${item.doctorNotes}", fontSize = 11.sp, color = Color(0xFF047857), fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isHomeVisit) {
                    Button(
                        onClick = { /* Simulated Join Telehealth Session */ },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("join_video_session_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Join Encrypted Room", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = { /* Contact Care Team */ },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Contact Team", fontSize = 11.5.sp)
                    }
                }

                if (item.status == "CONFIRMED") {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("cancel_appt_button_${item.id}")
                    ) {
                        Text("Cancel", fontSize = 11.sp, color = HealthCriticalRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleAppointmentDialog(
    onSchedule: (doc: String, spec: String, type: String, dateEpoch: Long, timeSlot: String, reason: String, loc: String) -> Unit,
    onDismiss: () -> Unit
) {
    val doctors = listOf(
        Triple("Dr. Sarah Jenkins, MD", "Internal Medicine & Home Care", "Attending"),
        Triple("Dr. Robert Chen, Cardiologist", "Cardiovascular Medicine", "Specialist"),
        Triple("Nurse Emily Watson, RN", "Home Nursing & Wound Care", "Home Nurse")
    )
    val apptTypes = listOf("HOME_VISIT" to "Home Healthcare Visit", "VIDEO_CONSULTATION" to "Encrypted Telehealth Video")
    val timeSlots = listOf("09:00 AM - 09:30 AM", "10:30 AM - 11:15 AM", "02:00 PM - 02:45 PM", "04:00 PM - 04:30 PM")

    var selectedDoctor by remember { mutableStateOf(doctors[0]) }
    var selectedType by remember { mutableStateOf("HOME_VISIT") }
    var selectedTimeSlot by remember { mutableStateOf(timeSlots[1]) }
    var reason by remember { mutableStateOf("Routine Vitals Assessment & Care Plan Review") }
    var location by remember { mutableStateOf("Home Visit - 742 Evergreen Way") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("schedule_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Schedule Care Appointment", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Select provider, visit type, and preferred time slot", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(14.dp))

                Text("Select Healthcare Professional", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Spacer(modifier = Modifier.height(4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    doctors.forEach { doc ->
                        val isSel = selectedDoctor.first == doc.first
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealAccent else Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth().clickable { selectedDoctor = doc }
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(12.dp).clip(CircleShape).background(if (isSel) TealAccent else Color(0xFFCBD5E1))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(doc.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    Text(doc.second, fontSize = 10.5.sp, color = Color(0xFF64748B))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Appointment Format", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    apptTypes.forEach { (typeKey, typeLabel) ->
                        val isSel = selectedType == typeKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealAccent else Color(0xFFCBD5E1)),
                            modifier = Modifier.weight(1f).clickable {
                                selectedType = typeKey
                                location = if (typeKey == "HOME_VISIT") "Home Visit - Patient Residence" else "ANA Encrypted Telehealth Portal Room"
                            }
                        ) {
                            Text(
                                text = typeLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) NavyPrimary else Color(0xFF475569),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Appointment / Symptoms") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("appt_reason_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            val cal = Calendar.getInstance()
                            cal.add(Calendar.DAY_OF_MONTH, 3)
                            onSchedule(
                                selectedDoctor.first,
                                selectedDoctor.second,
                                selectedType,
                                cal.timeInMillis,
                                selectedTimeSlot,
                                reason,
                                location
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier.testTag("confirm_schedule_button")
                    ) {
                        Text("Confirm Appointment")
                    }
                }
            }
        }
    }
}
