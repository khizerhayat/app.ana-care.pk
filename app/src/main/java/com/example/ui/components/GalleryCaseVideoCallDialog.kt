package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entities.MedicalGalleryEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.data.local.entities.VitalSignEntity
import com.example.data.util.ImageStorageHelper
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.PortalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data structure for live in-call clinical chat messages
 */
data class VideoCallChatMessage(
    val senderName: String,
    val senderRole: String,
    val text: String,
    val timeFormatted: String,
    val isDirective: Boolean = false
)

/**
 * Data structure for interactive clinical pointer pins placed on the shared gallery image
 */
data class ClinicalImagePin(
    val id: Long = System.currentTimeMillis(),
    val xFraction: Float,
    val yFraction: Float,
    val placedBy: String,
    val label: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryCaseVideoCallDialog(
    initialGalleryItem: MedicalGalleryEntity?,
    allGalleryItems: List<MedicalGalleryEntity>,
    viewModel: PortalViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activeAccount by viewModel.activeAccount.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val vitalsList by viewModel.vitalsList.collectAsState()

    // 1. Identify Connected Triad: Patient, Caretaker, Doctor
    val targetPatient: UserAccountEntity = remember(activeAccount, allAccounts) {
        val active = activeAccount
        when {
            active?.role == "PATIENT" -> active
            active?.role == "CAREGIVER" -> {
                allAccounts.find { it.userId == active.assignedPatientId }
                    ?: allAccounts.find { it.role == "PATIENT" && it.isPrimaryPatient }
                    ?: allAccounts.find { it.role == "PATIENT" }
                    ?: active
            }
            active?.role in listOf("DOCTOR", "MEDICAL_PROFESSIONAL") -> {
                viewModel.doctorTargetPatient.value
                    ?: allAccounts.find { it.role == "PATIENT" && it.isPrimaryPatient }
                    ?: allAccounts.find { it.role == "PATIENT" }
                    ?: active
            }
            else -> {
                allAccounts.find { it.role == "PATIENT" && it.isPrimaryPatient }
                    ?: allAccounts.find { it.role == "PATIENT" }
                    ?: UserAccountEntity(
                        userId = "21001001",
                        name = "Pt. Eleanor Vance",
                        email = "eleanor.vance@example.com",
                        role = "PATIENT"
                    )
            }
        }
    }

    val linkedCaregiver: UserAccountEntity = remember(targetPatient, allAccounts) {
        allAccounts.find { it.role == "CAREGIVER" && it.assignedPatientId == targetPatient.userId }
            ?: allAccounts.find { it.role == "CAREGIVER" }
            ?: UserAccountEntity(
                userId = "3000",
                name = "CG. James Vance",
                email = "james.vance@example.com",
                role = "CAREGIVER",
                relationship = "Son & Primary Caregiver"
            )
    }

    val assignedDoctor: UserAccountEntity = remember(targetPatient, allAccounts) {
        allAccounts.find { it.userId == targetPatient.assignedDoctorId }
            ?: allAccounts.find { it.role in listOf("DOCTOR", "MEDICAL_PROFESSIONAL") }
            ?: UserAccountEntity(
                userId = "1001",
                name = "Dr. Sarah Jenkins, MD",
                email = "dr.jenkins@anacare.org",
                role = "MEDICAL_PROFESSIONAL",
                specialty = "Internal Medicine & Wound Specialist"
            )
    }

    // 2. Active Image under discussion
    var currentCaseItem by remember {
        mutableStateOf(initialGalleryItem ?: allGalleryItems.firstOrNull())
    }

    // 3. Call State Controls
    var isMicMuted by remember { mutableStateOf(false) }
    var isVideoOff by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var showChatDrawer by remember { mutableStateOf(false) }
    var showVitalsHud by remember { mutableStateOf(true) }
    var showDirectivesInput by remember { mutableStateOf(false) }
    var selectedViewMode by remember { mutableStateOf(0) } // 0: Shared Split-Screen, 1: Full Case Image, 2: 3-Way Video Grid
    var showEndCallConfirm by remember { mutableStateOf(false) }

    // 4. Timer & Audio Animation
    var callSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        // Log Audit event on call start
        viewModel.logAuditAction(
            actionType = "VIDEO_CALL_START",
            category = "TELEHEALTH VIDEO",
            description = "Started 3-way Gallery Video Consultation for ${targetPatient.name} with Caregiver ${linkedCaregiver.name} and ${assignedDoctor.name}.",
            details = "Image: ${currentCaseItem?.title ?: "General Case"}, Room: ROOM-GAL-${(1000..9999).random()}",
            severity = "SUCCESS"
        )
        while (true) {
            delay(1000L)
            callSeconds++
        }
    }

    val formattedDuration = remember(callSeconds) {
        val mins = callSeconds / 60
        val secs = callSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    // Active speaking simulation rotation
    var activeSpeakerIndex by remember { mutableIntStateOf(2) } // 0: Patient, 1: Caregiver, 2: Doctor
    LaunchedEffect(Unit) {
        while (true) {
            delay(6000L)
            activeSpeakerIndex = (activeSpeakerIndex + 1) % 3
        }
    }

    // Interactive Image Annotations / Pointer Pins
    val imagePins = remember {
        mutableStateListOf(
            ClinicalImagePin(
                xFraction = 0.52f,
                yFraction = 0.44f,
                placedBy = assignedDoctor.name,
                label = "Granulation Margin",
                color = Color(0xFF2563EB)
            )
        )
    }

    // Live In-Call Messages
    val chatMessages = remember {
        mutableStateListOf(
            VideoCallChatMessage(
                senderName = assignedDoctor.name,
                senderRole = "DOCTOR",
                text = "Hello Eleanor and James. I am reviewing the uploaded photo together with you on the shared screen.",
                timeFormatted = "Just now"
            ),
            VideoCallChatMessage(
                senderName = linkedCaregiver.name,
                senderRole = "CAREGIVER",
                text = "Good afternoon Dr. Jenkins. We applied the prescribed saline dressing this morning at 9:00 AM.",
                timeFormatted = "Just now"
            ),
            VideoCallChatMessage(
                senderName = targetPatient.name,
                senderRole = "PATIENT",
                text = "The tenderness is much less than 3 days ago. No fever noted.",
                timeFormatted = "Just now"
            )
        )
    }

    var inputChatMessage by remember { mutableStateOf("") }
    var inputDoctorDirective by remember { mutableStateOf("") }

    val latestVital: VitalSignEntity? = vitalsList.firstOrNull()

    Dialog(
        onDismissRequest = { showEndCallConfirm = true },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B132B))
                .testTag("gallery_video_call_dialog"),
            color = Color(0xFF0B132B)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // ==========================================
                // 1. TOP SECURE TELEHEALTH HEADER
                // ==========================================
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2541)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Live Pulsing Timer & Encrypted Room Badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(HealthNormalGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE: $formattedDuration",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF064E3B)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = "Encrypted",
                                        tint = HealthNormalGreen,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "256-bit HIPAA E2EE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNormalGreen
                                    )
                                }
                            }
                        }

                        // Center: View Mode Switcher Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (selectedViewMode == 0) TealAccent else Color(0xFF28385E),
                                modifier = Modifier.clickable { selectedViewMode = 0 }
                            ) {
                                Text(
                                    text = "Split View",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedViewMode == 0) NavyDark else Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (selectedViewMode == 1) TealAccent else Color(0xFF28385E),
                                modifier = Modifier.clickable { selectedViewMode = 1 }
                            ) {
                                Text(
                                    text = "Case Focus",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedViewMode == 1) NavyDark else Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (selectedViewMode == 2) TealAccent else Color(0xFF28385E),
                                modifier = Modifier.clickable { selectedViewMode = 2 }
                            ) {
                                Text(
                                    text = "Video Grid",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedViewMode == 2) NavyDark else Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Right: Quick Vitals Toggle & Close/End
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showVitalsHud = !showVitalsHud },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = "Toggle Vitals",
                                    tint = if (showVitalsHud) HealthCriticalRed else Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { showEndCallConfirm = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Exit Consultation",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 2. LIVE VITALS QUICK HUD (Collapsible)
                // ==========================================
                AnimatedVisibility(visible = showVitalsHud) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF101B37)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF233560)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Telehealth Vitals Telemetry (${targetPatient.name}):",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyLight
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val bpText = if (latestVital != null) "${latestVital.systolic}/${latestVital.diastolic} mmHg" else "128/82 mmHg"
                                val hrText = if (latestVital != null) "${latestVital.heartRate} bpm" else "72 bpm"
                                val spo2Text = if (latestVital != null) "${latestVital.spo2}%" else "98%"
                                val tempText = if (latestVital != null) "${latestVital.temperatureF}°F" else "98.4°F"

                                VitalsBadge(label = "BP", value = bpText, color = TealAccent)
                                VitalsBadge(label = "HR", value = hrText, color = Color(0xFFF87171))
                                VitalsBadge(label = "SpO2", value = spo2Text, color = Color(0xFF60A5FA))
                                VitalsBadge(label = "Temp", value = tempText, color = Color(0xFFFBBF24))
                            }
                        }
                    }
                }

                // ==========================================
                // 3. MAIN VIDEO & CASE WHITEBOARD DISPLAY
                // ==========================================
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedViewMode) {
                        0 -> {
                            // SPLIT VIEW: Shared Image on Top/Left, 3-Party Video Grid below
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Shared Gallery Case Board
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.58f)
                                ) {
                                    SharedCaseDisplay(
                                        caseItem = currentCaseItem,
                                        imagePins = imagePins,
                                        allGalleryItems = allGalleryItems,
                                        onSelectCase = { currentCaseItem = it },
                                        onAddPin = { x, y ->
                                            val author = activeAccount?.name ?: assignedDoctor.name
                                            imagePins.add(
                                                ClinicalImagePin(
                                                    xFraction = x,
                                                    yFraction = y,
                                                    placedBy = author,
                                                    label = "Marked by $author",
                                                    color = when (activeAccount?.role) {
                                                        "DOCTOR", "MEDICAL_PROFESSIONAL" -> Color(0xFF3B82F6)
                                                        "CAREGIVER" -> Color(0xFFF59E0B)
                                                        else -> TealAccent
                                                    }
                                                )
                                            )
                                        },
                                        onClearPins = { imagePins.clear() }
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // 3 Video Streams Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.42f),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ParticipantVideoTile(
                                        name = targetPatient.name,
                                        role = "PATIENT (Self / Home)",
                                        roleColor = TealAccent,
                                        isSpeaking = activeSpeakerIndex == 0,
                                        isMuted = false,
                                        isVideoOff = isVideoOff && activeAccount?.role == "PATIENT",
                                        avatarInitials = targetPatient.avatarInitials.ifEmpty { "PT" },
                                        modifier = Modifier.weight(1f)
                                    )

                                    ParticipantVideoTile(
                                        name = linkedCaregiver.name,
                                        role = "CARETAKER (${linkedCaregiver.relationship.ifEmpty { "Primary Caregiver" }})",
                                        roleColor = Color(0xFFF59E0B),
                                        isSpeaking = activeSpeakerIndex == 1,
                                        isMuted = false,
                                        isVideoOff = isVideoOff && activeAccount?.role == "CAREGIVER",
                                        avatarInitials = linkedCaregiver.avatarInitials.ifEmpty { "CG" },
                                        modifier = Modifier.weight(1f)
                                    )

                                    ParticipantVideoTile(
                                        name = assignedDoctor.name,
                                        role = "DOCTOR (${assignedDoctor.specialty.ifEmpty { "Attending Physician" }})",
                                        roleColor = Color(0xFF3B82F6),
                                        isSpeaking = activeSpeakerIndex == 2,
                                        isMuted = false,
                                        isVideoOff = isVideoOff && activeAccount?.role in listOf("DOCTOR", "MEDICAL_PROFESSIONAL"),
                                        avatarInitials = assignedDoctor.avatarInitials.ifEmpty { "DR" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        1 -> {
                            // FULL CASE FOCUS: Shared Image Expanded with floating PiP video badges
                            Box(modifier = Modifier.fillMaxSize()) {
                                SharedCaseDisplay(
                                    caseItem = currentCaseItem,
                                    imagePins = imagePins,
                                    allGalleryItems = allGalleryItems,
                                    onSelectCase = { currentCaseItem = it },
                                    onAddPin = { x, y ->
                                        val author = activeAccount?.name ?: assignedDoctor.name
                                        imagePins.add(
                                            ClinicalImagePin(
                                                xFraction = x,
                                                yFraction = y,
                                                placedBy = author,
                                                label = "Point by $author",
                                                color = Color(0xFF3B82F6)
                                            )
                                        )
                                    },
                                    onClearPins = { imagePins.clear() }
                                )

                                // Floating Mini Video Strip at Top Right
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    MiniParticipantBadge(targetPatient.avatarInitials.ifEmpty { "PT" }, targetPatient.name, TealAccent, activeSpeakerIndex == 0)
                                    MiniParticipantBadge(linkedCaregiver.avatarInitials.ifEmpty { "CG" }, linkedCaregiver.name, Color(0xFFF59E0B), activeSpeakerIndex == 1)
                                    MiniParticipantBadge(assignedDoctor.avatarInitials.ifEmpty { "DR" }, assignedDoctor.name, Color(0xFF3B82F6), activeSpeakerIndex == 2)
                                }
                            }
                        }
                        2 -> {
                            // 3-WAY VIDEO GRID (Large Multi-party Video Layout)
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Top Row: Doctor
                                ParticipantVideoTile(
                                    name = assignedDoctor.name,
                                    role = "ATTENDING DOCTOR (${assignedDoctor.specialty.ifEmpty { "Internal Medicine" }})",
                                    roleColor = Color(0xFF3B82F6),
                                    isSpeaking = activeSpeakerIndex == 2,
                                    isMuted = false,
                                    isVideoOff = isVideoOff && activeAccount?.role in listOf("DOCTOR", "MEDICAL_PROFESSIONAL"),
                                    avatarInitials = assignedDoctor.avatarInitials.ifEmpty { "DR" },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )

                                // Bottom Row: Patient & Caregiver Side by Side
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ParticipantVideoTile(
                                        name = targetPatient.name,
                                        role = "PATIENT (Eleanor)",
                                        roleColor = TealAccent,
                                        isSpeaking = activeSpeakerIndex == 0,
                                        isMuted = false,
                                        isVideoOff = isVideoOff && activeAccount?.role == "PATIENT",
                                        avatarInitials = targetPatient.avatarInitials.ifEmpty { "PT" },
                                        modifier = Modifier.weight(1f)
                                    )

                                    ParticipantVideoTile(
                                        name = linkedCaregiver.name,
                                        role = "CARETAKER (James)",
                                        roleColor = Color(0xFFF59E0B),
                                        isSpeaking = activeSpeakerIndex == 1,
                                        isMuted = false,
                                        isVideoOff = isVideoOff && activeAccount?.role == "CAREGIVER",
                                        avatarInitials = linkedCaregiver.avatarInitials.ifEmpty { "CG" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // In-Call Chat Drawer Overlay (Slide-in)
                    AnimatedVisibility(
                        visible = showChatDrawer,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.92f)
                            .align(Alignment.CenterEnd)
                    ) {
                        InCallChatDrawer(
                            messages = chatMessages,
                            currentAccount = activeAccount,
                            onSendMessage = { text ->
                                val sender = activeAccount?.name ?: "Dr. Sarah Jenkins"
                                val role = activeAccount?.role ?: "DOCTOR"
                                chatMessages.add(
                                    VideoCallChatMessage(
                                        senderName = sender,
                                        senderRole = role,
                                        text = text,
                                        timeFormatted = "Just now"
                                    )
                                )
                            },
                            onClose = { showChatDrawer = false }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // ==========================================
                // 4. IN-CALL CASE DIRECTIVES BAR
                // ==========================================
                AnimatedVisibility(visible = showDirectivesInput) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Record Doctor Directive / Care Decision for Gallery Case",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF93C5FD)
                                )
                                IconButton(
                                    onClick = { showDirectivesInput = false },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = inputDoctorDirective,
                                onValueChange = { inputDoctorDirective = it },
                                placeholder = { Text("e.g., Continue daily sterile saline dressing; apply topical mupirocin 2% ointment; re-check in 4 days.", fontSize = 11.5.sp, color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = Color.White)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        if (inputDoctorDirective.isNotBlank()) {
                                            viewModel.addAlertNote(
                                                title = "Gallery Consultation Directive (${currentCaseItem?.title ?: "Clinical Photo"})",
                                                message = inputDoctorDirective.trim(),
                                                priority = "HIGH",
                                                category = "CARE_PLAN",
                                                preselectedPatientId = targetPatient.userId
                                            )
                                            chatMessages.add(
                                                VideoCallChatMessage(
                                                    senderName = activeAccount?.name ?: assignedDoctor.name,
                                                    senderRole = "DOCTOR",
                                                    text = "📋 CLINICAL DIRECTIVE SAVED: $inputDoctorDirective",
                                                    timeFormatted = "Just now",
                                                    isDirective = true
                                                )
                                            )
                                            viewModel.logAuditAction(
                                                actionType = "CONSULTATION_DIRECTIVE",
                                                category = "TELEHEALTH VIDEO",
                                                description = "Doctor directive recorded during video consultation: '$inputDoctorDirective'.",
                                                details = "Patient: ${targetPatient.name}, Doctor: ${assignedDoctor.name}, Case: ${currentCaseItem?.title}",
                                                severity = "SUCCESS"
                                            )
                                            Toast.makeText(context, "Directive saved to patient chart & alert center.", Toast.LENGTH_SHORT).show()
                                            inputDoctorDirective = ""
                                            showDirectivesInput = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Save Directive to Patient Chart", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 5. CALL BOTTOM CONTROLS DOCK
                // ==========================================
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2541)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A506B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mic Mute Toggle
                        ControlButton(
                            icon = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            label = if (isMicMuted) "Unmute" else "Mute",
                            isActive = !isMicMuted,
                            activeColor = Color(0xFF334155),
                            inactiveColor = HealthCriticalRed,
                            onClick = {
                                isMicMuted = !isMicMuted
                                Toast.makeText(context, if (isMicMuted) "Microphone Muted" else "Microphone Active", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // Camera Toggle
                        ControlButton(
                            icon = if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            label = if (isVideoOff) "Start Video" else "Stop Video",
                            isActive = !isVideoOff,
                            activeColor = Color(0xFF334155),
                            inactiveColor = HealthCriticalRed,
                            onClick = {
                                isVideoOff = !isVideoOff
                                Toast.makeText(context, if (isVideoOff) "Camera Stopped" else "Camera Started", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // Flip Camera
                        ControlButton(
                            icon = Icons.Default.Cameraswitch,
                            label = if (isFrontCamera) "Rear Cam" else "Front Cam",
                            isActive = true,
                            activeColor = Color(0xFF334155),
                            onClick = {
                                isFrontCamera = !isFrontCamera
                                Toast.makeText(context, if (isFrontCamera) "Switched to Front Camera" else "Switched to Rear Camera", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // In-Call Chat Drawer Toggle
                        ControlButton(
                            icon = Icons.Default.Chat,
                            label = "Chat (${chatMessages.size})",
                            isActive = showChatDrawer,
                            activeColor = TealAccent,
                            inactiveColor = Color(0xFF334155),
                            badgeCount = chatMessages.size,
                            onClick = { showChatDrawer = !showChatDrawer }
                        )

                        // Doctor Clinical Directive recorder toggle
                        ControlButton(
                            icon = Icons.Default.MedicalServices,
                            label = "Directive",
                            isActive = showDirectivesInput,
                            activeColor = Color(0xFF60A5FA),
                            inactiveColor = Color(0xFF334155),
                            onClick = { showDirectivesInput = !showDirectivesInput }
                        )

                        // End Consultation Call Button
                        Surface(
                            shape = CircleShape,
                            color = HealthCriticalRed,
                            modifier = Modifier
                                .size(46.dp)
                                .clickable { showEndCallConfirm = true }
                                .testTag("end_video_call_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.CallEnd,
                                    contentDescription = "End Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog to End Call
    if (showEndCallConfirm) {
        AlertDialog(
            onDismissRequest = { showEndCallConfirm = false },
            title = { Text("End Multi-Party Video Consultation?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(
                    "You are about to leave the live case discussion for ${targetPatient.name}. Total session duration: $formattedDuration. A record of this consultation will be saved in the system audit trail.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logAuditAction(
                            actionType = "VIDEO_CALL_END",
                            category = "TELEHEALTH VIDEO",
                            description = "Completed 3-way Gallery Video Consultation for ${targetPatient.name} (Duration: $formattedDuration).",
                            details = "Participants: Patient ${targetPatient.name}, Caregiver ${linkedCaregiver.name}, Doctor ${assignedDoctor.name}. Case: ${currentCaseItem?.title}",
                            severity = "SUCCESS"
                        )
                        showEndCallConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthCriticalRed)
                ) {
                    Text("End Call", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndCallConfirm = false }) {
                    Text("Resume Call")
                }
            }
        )
    }
}

/**
 * Shared Medical Gallery Case Whiteboard / Display Component
 */
@Composable
private fun SharedCaseDisplay(
    caseItem: MedicalGalleryEntity?,
    imagePins: List<ClinicalImagePin>,
    allGalleryItems: List<MedicalGalleryEntity>,
    onSelectCase: (MedicalGalleryEntity) -> Unit,
    onAddPin: (x: Float, y: Float) -> Unit,
    onClearPins: () -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3D66)),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            // Case Title and Category Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1E3A5F),
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Collections,
                                contentDescription = null,
                                tint = TealAccent,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = caseItem?.title ?: "Shared Clinical Situation",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${caseItem?.category ?: "Clinical Record"} • ${caseItem?.dateFormatted ?: "Today"}",
                            fontSize = 10.sp,
                            color = SkyLight
                        )
                    }
                }

                // Pointer Pin tool info & Clear
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = "Tap image to Pin (${imagePins.size})",
                            fontSize = 9.5.sp,
                            color = Color(0xFF93C5FD),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    if (imagePins.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onClearPins,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Clear Pins", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Interactive Shared Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .clickable {
                        // Place a pin in center area
                        onAddPin(0.5f, 0.5f)
                    }
            ) {
                if (caseItem != null) {
                    val localFile = if (caseItem.imageUri.startsWith("/")) File(caseItem.imageUri) else null
                    if (localFile != null && localFile.exists()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(localFile)
                                .crossfade(true)
                                .build(),
                            contentDescription = caseItem.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (caseItem.imageUri.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(caseItem.imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = caseItem.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                                Text(caseItem.title, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No gallery item selected. Select from below.", color = Color.Gray, fontSize = 11.sp)
                    }
                }

                // Render Annotations / Pins
                imagePins.forEach { pin ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = ((pin.xFraction - 0.5f) * 200).dp, y = ((pin.yFraction - 0.5f) * 120).dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = pin.color.copy(alpha = 0.9f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PinDrop, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(pin.label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // Case Notes Badge overlay at bottom
                if (caseItem?.notes?.isNotBlank() == true) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        color = Color(0xDD000000),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Notes: ${caseItem.notes}",
                            fontSize = 10.5.sp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Carousel to switch to other gallery images during call
            if (allGalleryItems.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(allGalleryItems) { item ->
                        val isSelected = item.id == caseItem?.id
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) TealAccent else Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) Color.White else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .clickable { onSelectCase(item) }
                                .width(90.dp)
                                .height(38.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    tint = if (isSelected) NavyDark else Color.LightGray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NavyDark else Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Single Video Stream Tile with Animated Speaking Indicator & Avatar
 */
@Composable
private fun ParticipantVideoTile(
    name: String,
    role: String,
    roleColor: Color,
    isSpeaking: Boolean,
    isMuted: Boolean,
    isVideoOff: Boolean,
    avatarInitials: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111D36)),
        border = androidx.compose.foundation.BorderStroke(
            if (isSpeaking) 2.dp else 1.dp,
            if (isSpeaking) HealthNormalGreen else Color(0xFF24365A)
        ),
        modifier = modifier
            .fillMaxHeight()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!isVideoOff) {
                // High-fidelity Simulated Video Feed Background with subtle gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1B2A4A),
                                    Color(0xFF0F1A2E)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Avatar with speaking wave
                    Box(
                        modifier = Modifier
                            .size((52 * waveScale).dp)
                            .clip(CircleShape)
                            .background(roleColor.copy(alpha = 0.25f))
                            .border(2.dp, if (isSpeaking) HealthNormalGreen else roleColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatarInitials,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Video Off Slate
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0A0F1D)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Camera Off", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            // Top Status Badges: Mic & Speaking Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSpeaking) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF064E3B)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(HealthNormalGreen)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Speaking", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = HealthNormalGreen)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Surface(
                    shape = CircleShape,
                    color = if (isMuted) HealthCriticalRed else Color(0x88000000),
                    modifier = Modifier.size(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            // Bottom Name and Role Badge
            Surface(
                shape = RoundedCornerShape(topStart = 8.dp),
                color = Color(0xDD000000),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)) {
                    Text(
                        text = name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = role,
                        fontSize = 8.sp,
                        color = roleColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniParticipantBadge(
    initials: String,
    name: String,
    color: Color,
    isSpeaking: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xCC000000),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpeaking) HealthNormalGreen else color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(name.take(10), fontSize = 9.sp, color = Color.White)
        }
    }
}

@Composable
private fun VitalsBadge(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)) {
            Text("$label: ", fontSize = 9.5.sp, color = Color.LightGray)
            Text(value, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color = Color(0xFF1E293B),
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box {
            Surface(
                shape = CircleShape,
                color = if (isActive) activeColor else inactiveColor,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (badgeCount > 0) {
                Surface(
                    shape = CircleShape,
                    color = HealthCriticalRed,
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(badgeCount.toString(), fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 8.5.sp, color = Color(0xFFCBD5E1))
    }
}

/**
 * Real-time In-Call Discussion Chat Drawer
 */
@Composable
private fun InCallChatDrawer(
    messages: List<VideoCallChatMessage>,
    currentAccount: UserAccountEntity?,
    onSendMessage: (String) -> Unit,
    onClose: () -> Unit
) {
    var chatInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131F37)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, TealAccent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = TealAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("In-Call Case Discussion", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Divider(color = Color(0xFF28385E), modifier = Modifier.padding(vertical = 6.dp))

            // Quick Preset Response Chips
            Text("Quick Responses:", fontSize = 9.sp, color = SkyLight)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val presets = listOf(
                    "Healing looks normal",
                    "Dressing changed on schedule",
                    "Patient pain level: 2/10",
                    "Prescribing topical antibiotic"
                )
                presets.forEach { preset ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E2E4E),
                        modifier = Modifier.clickable { onSendMessage(preset) }
                    ) {
                        Text(
                            text = preset,
                            fontSize = 9.sp,
                            color = Color(0xFFBAE6FD),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Messages List
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages) { msg ->
                    val isSelf = msg.senderName == currentAccount?.name
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isSelf) Alignment.End else Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 10.dp,
                                topEnd = 10.dp,
                                bottomStart = if (isSelf) 10.dp else 2.dp,
                                bottomEnd = if (isSelf) 2.dp else 10.dp
                            ),
                            color = if (msg.isDirective) Color(0xFF1E3A8A) else if (isSelf) TealAccent else Color(0xFF1E293B),
                            border = if (msg.isDirective) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF60A5FA)) else null,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = msg.senderName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (msg.isDirective) Color(0xFF93C5FD) else if (isSelf) NavyDark else TealAccent
                                    )
                                    Text(
                                        text = msg.timeFormatted,
                                        fontSize = 8.sp,
                                        color = if (isSelf && !msg.isDirective) NavyDark.copy(alpha = 0.7f) else Color.LightGray
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = msg.text,
                                    fontSize = 11.sp,
                                    color = if (isSelf && !msg.isDirective) NavyDark else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Message Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = { Text("Type discussion note...", fontSize = 11.sp, color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = Color.White)
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        if (chatInput.isNotBlank()) {
                            onSendMessage(chatInput.trim())
                            chatInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .background(TealAccent, CircleShape)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = NavyDark, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
