package com.example.ui.components

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.ActiveCallSession
import com.example.ui.viewmodel.CallType
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * High-priority Incoming Telehealth Consultation Ringing Dialog.
 * Triggered automatically on the recipient's screen when a patient, doctor, or caregiver initiates a call.
 * Rings bell and counts down for 60 seconds until accepted or rejected.
 */
@Composable
fun IncomingCallRingingDialog(
    callSession: ActiveCallSession,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val caller = callSession.caller
    val caseItem = callSession.caseItem
    val isVideo = callSession.callType == CallType.VIDEO

    var elapsedSeconds by remember { mutableIntStateOf(0) }

    // 60-second incoming call timer
    LaunchedEffect(Unit) {
        while (elapsedSeconds < 60) {
            delay(1000L)
            elapsedSeconds++
        }
        // Auto-decline / miss after 60s
        onDecline()
    }

    // Audible incoming ring bell tone generator
    LaunchedEffect(Unit) {
        var toneGen: ToneGenerator? = null
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_RING, 85)
            while (isActive && elapsedSeconds < 60) {
                toneGen.startTone(ToneGenerator.TONE_SUP_RINGTONE, 1200)
                delay(2500L)
            }
        } catch (_: Throwable) {
        } finally {
            try {
                toneGen?.release()
            } catch (_: Throwable) {}
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ringing_waves")
    val pulseRing1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring1"
    )
    val ringAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val pulseRing2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, delayMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring2"
    )
    val ringAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, delayMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    val bellVibe by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bell"
    )

    Dialog(
        onDismissRequest = onDecline,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("incoming_call_ringing_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, TealAccent.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Ringing Header Pill with 60s Countdown
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Ringing",
                                tint = HealthNormalGreen,
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(1.1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = (if (isVideo) "INCOMING VIDEO CALL" else "INCOMING VOICE CALL") + " • ${60 - elapsedSeconds}s",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HealthNormalGreen,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (60 - elapsedSeconds) / 60f },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = HealthNormalGreen,
                        trackColor = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Pulsing Avatar Radar Waves
                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Expanding Ring 2
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(pulseRing2)
                                .border(2.dp, TealAccent.copy(alpha = ringAlpha2), CircleShape)
                        )
                        // Expanding Ring 1
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(pulseRing1)
                                .border(3.dp, HealthNormalGreen.copy(alpha = ringAlpha1), CircleShape)
                        )

                        // Central Caller Avatar
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            when (caller.role) {
                                                "PATIENT" -> TealAccent
                                                "CAREGIVER" -> Color(0xFFF59E0B)
                                                else -> Color(0xFF3B82F6)
                                            },
                                            NavyPrimary
                                        )
                                    )
                                )
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = caller.avatarInitials.ifEmpty { caller.name.take(2).uppercase() },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Caller Name & Role Badge
                    Text(
                        text = caller.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (caller.role) {
                            "PATIENT" -> TealAccent.copy(alpha = 0.2f)
                            "CAREGIVER" -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                            else -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = when (caller.role) {
                                "PATIENT" -> "Patient • Primary Healthcare Chart"
                                "CAREGIVER" -> "Caregiver • ${caller.relationship.ifEmpty { "Family Caretaker" }}"
                                "DOCTOR", "MEDICAL_PROFESSIONAL" -> "Attending Physician • ${caller.specialty.ifEmpty { "Internal Medicine" }}"
                                else -> caller.role
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (caller.role) {
                                "PATIENT" -> TealAccent
                                "CAREGIVER" -> Color(0xFFF59E0B)
                                else -> SkyLight
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Attached Clinical Case if present
                    if (caseItem != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF24365A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = TealAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Attached Clinical Case:", fontSize = 10.5.sp, color = Color.Gray)
                                    Text(caseItem.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // E2EE Security info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = HealthNormalGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AES-256 E2EE End-to-End Encrypted Telehealth Link",
                            fontSize = 10.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Call Action Buttons (Decline / Accept Video)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Decline Button (Red)
                        Button(
                            onClick = onDecline,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("decline_call_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HealthCriticalRed)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Decline", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        // Accept Button (Green with Call/Video icon)
                        Button(
                            onClick = onAccept,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(52.dp)
                                .testTag("accept_call_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HealthNormalGreen)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Call,
                                    contentDescription = if (isVideo) "Answer Video" else "Answer Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isVideo) "Answer Video" else "Answer Call",
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
}
