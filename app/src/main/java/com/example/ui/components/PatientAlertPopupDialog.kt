package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.PatientAlertNoteEntity
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SkyLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PatientAlertPopupDialog(
    alert: PatientAlertNoteEntity,
    onAcknowledge: (Long) -> Unit,
    onDismiss: () -> Unit,
    onActionLinkClick: (String) -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(alert.timestamp))

    val (headerBg, iconBg, iconTint, badgeText, badgeColor) = when (alert.severity) {
        "URGENT" -> Pair(
            Color(0xFF991B1B), // Dark Red
            Color(0xFFFEE2E2)
        ) to Triple(Color(0xFFDC2626), "URGENT CLINICAL ALERT", HealthCriticalRed)
        "MEDICATION_ALERT" -> Pair(
            Color(0xFF854D0E), // Amber Dark
            Color(0xFFFEF3C7)
        ) to Triple(Color(0xFFD97706), "MEDICATION INSTRUCTION", HealthWarningAmber)
        "WARNING" -> Pair(
            Color(0xFF9A3412), // Orange Dark
            Color(0xFFFFEDD5)
        ) to Triple(Color(0xFFEA580C), "CLINICAL ADVISORY", Color(0xFFEA580C))
        else -> Pair(
            NavyPrimary,
            Color(0xFFE0F2FE)
        ) to Triple(Color(0xFF0284C7), "IMPORTANT NOTICE", Color(0xFF0284C7))
    }.let {
        Tuple5(it.first.first, it.first.second, it.second.first, it.second.second, it.second.third)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier
                .widthIn(max = 540.dp)
                .fillMaxWidth(0.94f)
                .testTag("patient_alert_popup_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = iconBg,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (alert.senderRole == "ADMIN") Icons.Default.AdminPanelSettings else Icons.Default.NotificationImportant,
                                        contentDescription = "Alert Icon",
                                        tint = iconTint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = badgeText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyLight,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = if (alert.senderRole == "ADMIN") "From: System Administration" else "From: Attending Physician",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Content Body
                Column(modifier = Modifier.padding(20.dp)) {
                    // Sender info card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (alert.senderRole == "ADMIN") Icons.Default.AdminPanelSettings else Icons.Default.MedicalServices,
                                contentDescription = "Sender",
                                tint = NavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = alert.senderName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                                Text(
                                    text = dateStr,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = alert.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Message Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = alert.message,
                            fontSize = 14.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    // Action Shortcut Link if applicable
                    if (alert.actionLink.isNotEmpty() && alert.actionLink != "NONE") {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                onActionLinkClick(alert.actionLink)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("alert_action_link_button")
                        ) {
                            Text(
                                text = when (alert.actionLink) {
                                    "MEDICATIONS" -> "💊 View Prescribed Medications"
                                    "LABS" -> "🧪 View Diagnostic Lab Results"
                                    "VITALS" -> "📊 Review Vitals Stream"
                                    "APPOINTMENTS" -> "📅 View Scheduled Appointments"
                                    else -> "View Associated Health Records"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Acknowledge Action Button
                    Button(
                        onClick = { onAcknowledge(alert.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("acknowledge_alert_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Acknowledge",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "I Understand & Acknowledge",
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

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
