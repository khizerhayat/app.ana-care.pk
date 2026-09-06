package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.CallLogEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.CallType
import com.example.ui.viewmodel.PortalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * WhatsApp-Style Telehealth Calls Screen.
 * Displays call history (Missed, Received, Dialed) with top buttons to initiate
 * Audio or Video calls and seamless bridges to chat and participant invites.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val activeUser by viewModel.activeAccount.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val callLogs by viewModel.callLogsList.collectAsState()
    val selectedFilter by viewModel.callFilter.collectAsState()
    val searchQuery by viewModel.callSearchQuery.collectAsState()
    val missedCount by viewModel.missedCallCount.collectAsState()

    var showNewCallDialog by remember { mutableStateOf(false) }
    var selectedInitiationType by remember { mutableStateOf(CallType.VIDEO) }
    var showClearHistoryConfirm by remember { mutableStateOf(false) }

    // Filter contacts strictly to relevant patient, doctor, or caregiver links
    val callableContacts = remember(allAccounts, activeUser) {
        val current = activeUser ?: return@remember emptyList()
        val myId = current.userId
        when (current.role) {
            "PATIENT" -> {
                allAccounts.filter {
                    it.userId != myId &&
                    (it.role in listOf("DOCTOR", "MEDICAL_PROFESSIONAL") || it.role == "CAREGIVER")
                }
            }
            "DOCTOR", "MEDICAL_PROFESSIONAL" -> {
                allAccounts.filter {
                    it.userId != myId &&
                    (it.role == "PATIENT" || it.role == "CAREGIVER")
                }
            }
            "CAREGIVER" -> {
                allAccounts.filter {
                    it.userId != myId &&
                    (it.role == "PATIENT" || it.role in listOf("DOCTOR", "MEDICAL_PROFESSIONAL"))
                }
            }
            else -> {
                allAccounts.filter { it.userId != myId }
            }
        }
    }

    // Filter call logs based on search & tab filter
    val filteredLogs = remember(callLogs, selectedFilter, searchQuery, activeUser?.userId) {
        val myId = activeUser?.userId ?: ""
        callLogs.filter { log ->
            // Direction filter
            val matchesDirection = when (selectedFilter) {
                "MISSED" -> log.callDirection == "MISSED"
                "RECEIVED" -> log.callDirection == "RECEIVED"
                "DIALED" -> log.callDirection == "DIALED"
                else -> true
            }

            // Search filter
            val otherPersonName = if (log.callerId == myId) log.recipientName else log.callerName
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                otherPersonName.contains(searchQuery, ignoreCase = true) ||
                        log.callerRole.contains(searchQuery, ignoreCase = true) ||
                        log.recipientRole.contains(searchQuery, ignoreCase = true) ||
                        (log.caseTitle?.contains(searchQuery, ignoreCase = true) == true)
            }

            matchesDirection && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("calls_screen")
    ) {
        // 1. Top Bar / Header with Encrypted Status
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Calls",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x33FFFFFF)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "E2EE",
                                        tint = HealthNormalGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "End-to-End Encrypted",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Text(
                            text = "WhatsApp-style HD voice & multi-party video consultations",
                            fontSize = 12.sp,
                            color = Color(0xFF93C5FD)
                        )
                    }

                    if (callLogs.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearHistoryConfirm = true },
                            modifier = Modifier.testTag("clear_call_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear Call Logs",
                                tint = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Top Quick Call Action Buttons (Audio & Video)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Audio Call Button (Green WhatsApp style)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F5132)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedInitiationType = CallType.AUDIO
                                showNewCallDialog = true
                            }
                            .testTag("top_audio_call_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(HealthNormalGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "New Audio Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Audio Call",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Voice Consultation",
                                    fontSize = 11.sp,
                                    color = Color(0xFFA7F3D0)
                                )
                            }
                        }
                    }

                    // Video Call Button (Teal/Indigo WhatsApp style)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedInitiationType = CallType.VIDEO
                                showNewCallDialog = true
                            }
                            .testTag("top_video_call_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(TealAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "New Video Call",
                                    tint = NavyDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Video Call",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "HD Screen & Vitals",
                                    fontSize = 11.sp,
                                    color = Color(0xFFBFDBFE)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Search Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setCallSearchQuery(it) },
                    placeholder = { Text("Search calls by name, role or case...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setCallSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("call_history_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips (All, Missed, Received, Dialed)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        Triple("ALL", "All Calls", callLogs.size),
                        Triple("MISSED", "Missed", callLogs.count { it.callDirection == "MISSED" }),
                        Triple("RECEIVED", "Received", callLogs.count { it.callDirection == "RECEIVED" }),
                        Triple("DIALED", "Dialed", callLogs.count { it.callDirection == "DIALED" })
                    )

                    items(filters) { (key, label, count) ->
                        val isSelected = selectedFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setCallFilter(key) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                    if (count > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = if (key == "MISSED" && count > 0) {
                                                HealthCriticalRed
                                            } else if (isSelected) {
                                                NavyPrimary
                                            } else {
                                                Color(0xFFE2E8F0)
                                            }
                                        ) {
                                            Text(
                                                text = count.toString(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (key == "MISSED" || isSelected) Color.White else NavyDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealAccent.copy(alpha = 0.2f),
                                selectedLabelColor = NavyDark
                            ),
                            modifier = Modifier.testTag("filter_chip_$key")
                        )
                    }
                }
            }
        }

        // 4. Calls List Content
        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (selectedFilter) {
                                "MISSED" -> Icons.Default.CallMissed
                                "RECEIVED" -> Icons.Default.CallReceived
                                "DIALED" -> Icons.Default.CallMade
                                else -> Icons.Default.Phone
                            },
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No matching calls found" else "No ${selectedFilter.lowercase()} calls recorded",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Make a secure audio or video consultation with linked doctors, caregivers, or patients.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            selectedInitiationType = CallType.AUDIO
                            showNewCallDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("empty_state_new_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start a New Call", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredLogs, key = { it.id }) { logItem ->
                    CallLogListItem(
                        log = logItem,
                        currentUserId = activeUser?.userId ?: "",
                        allAccounts = allAccounts,
                        onAudioCall = { targetAccount ->
                            viewModel.initiateAudioCall(targetAccount)
                        },
                        onVideoCall = { targetAccount ->
                            viewModel.initiateVideoCall(targetAccount)
                        },
                        onMessage = { targetAccount ->
                            viewModel.navigateToChatWithPeer(targetAccount)
                        },
                        onDelete = {
                            viewModel.deleteCallLog(logItem.id)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // 5. Contact Picker Modal to initiate New Call (Audio or Video)
    if (showNewCallDialog) {
        NewCallContactPickerDialog(
            title = if (selectedInitiationType == CallType.AUDIO) "New Audio Call" else "New Video Consultation",
            callType = selectedInitiationType,
            contacts = callableContacts,
            onSelectContact = { chosenUser, requestedType ->
                showNewCallDialog = false
                if (requestedType == CallType.AUDIO) {
                    viewModel.initiateAudioCall(chosenUser)
                } else {
                    viewModel.initiateVideoCall(chosenUser)
                }
            },
            onDismiss = { showNewCallDialog = false }
        )
    }

    // 6. Clear History Confirmation Dialog
    if (showClearHistoryConfirm) {
        AlertDialog(
            onDismissRequest = { showClearHistoryConfirm = false },
            title = { Text("Clear Call History?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently clear all recorded audio and video consultation call logs for your profile.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllCallLogs()
                        showClearHistoryConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthCriticalRed)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Single Call Log Row Item (WhatsApp Style)
 */
@Composable
fun CallLogListItem(
    log: CallLogEntity,
    currentUserId: String,
    allAccounts: List<UserAccountEntity>,
    onAudioCall: (UserAccountEntity) -> Unit,
    onVideoCall: (UserAccountEntity) -> Unit,
    onMessage: (UserAccountEntity) -> Unit,
    onDelete: () -> Unit
) {
    val isMeCaller = log.callerId == currentUserId
    val otherPersonId = if (isMeCaller) log.recipientId else log.callerId
    val otherPersonName = if (isMeCaller) log.recipientName else log.callerName
    val otherPersonRole = if (isMeCaller) log.recipientRole else log.callerRole
    val otherPersonAvatar = if (isMeCaller) log.recipientAvatar else log.callerAvatar

    val targetAccount = remember(allAccounts, otherPersonId) {
        allAccounts.find { it.userId == otherPersonId } ?: UserAccountEntity(
            userId = otherPersonId,
            name = otherPersonName,
            email = "$otherPersonId@anacare.org",
            role = otherPersonRole,
            avatarInitials = otherPersonAvatar
        )
    }

    val isMissed = log.callDirection == "MISSED"
    val isReceived = log.callDirection == "RECEIVED"
    val isDialed = log.callDirection == "DIALED"
    val isVideo = log.callType == "VIDEO"

    val timeFormatted = remember(log.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        sdf.format(Date(log.timestamp))
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("call_log_item_${log.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with Role Indicator
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        when (otherPersonRole) {
                            "DOCTOR", "MEDICAL_PROFESSIONAL" -> NavyDark
                            "CAREGIVER" -> Color(0xFF0F766E)
                            else -> Color(0xFF1D4ED8)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = otherPersonAvatar.ifBlank { otherPersonName.take(2).uppercase() },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = otherPersonName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMissed) HealthCriticalRed else NavyDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Direction Icon + Status + Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            isMissed -> Icons.Default.CallMissed
                            isReceived -> Icons.Default.CallReceived
                            else -> Icons.Default.CallMade
                        },
                        contentDescription = log.callDirection,
                        tint = when {
                            isMissed -> HealthCriticalRed
                            isReceived -> HealthNormalGreen
                            else -> Color(0xFF2563EB)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = when {
                            isMissed -> "Missed"
                            isReceived -> "Received"
                            else -> "Outgoing"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            isMissed -> HealthCriticalRed
                            isReceived -> HealthNormalGreen
                            else -> Color(0xFF2563EB)
                        }
                    )

                    Text(
                        text = " • $timeFormatted",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Call type badge and clinical case title if present
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isVideo) Color(0xFFEEF2FF) else Color(0xFFECFDF5)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Call,
                                contentDescription = null,
                                tint = if (isVideo) Color(0xFF4F46E5) else HealthNormalGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isVideo) "Video" else "Audio",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isVideo) Color(0xFF4F46E5) else HealthNormalGreen
                            )
                        }
                    }

                    if (!log.caseTitle.isNullOrBlank()) {
                        Text(
                            text = "Case: ${log.caseTitle}",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Action Buttons: Audio Call, Video Call, Message
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Audio Call Action
                IconButton(
                    onClick = { onAudioCall(targetAccount) },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("item_audio_call_${log.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Voice Call",
                        tint = HealthNormalGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Video Call Action
                IconButton(
                    onClick = { onVideoCall(targetAccount) },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("item_video_call_${log.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = Color(0xFF4F46E5),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Direct Chat Action
                IconButton(
                    onClick = { onMessage(targetAccount) },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("item_message_${log.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Send Message",
                        tint = NavyPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Contact Picker Dialog for Starting New Audio or Video Consultations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCallContactPickerDialog(
    title: String,
    callType: CallType,
    contacts: List<UserAccountEntity>,
    onSelectContact: (UserAccountEntity, CallType) -> Unit,
    onDismiss: () -> Unit
) {
    var searchInput by remember { mutableStateOf("") }
    var selectedCategoryTab by remember { mutableIntStateOf(0) } // 0: All, 1: Doctors, 2: Patients, 3: Caregivers

    val filteredContacts = remember(contacts, searchInput, selectedCategoryTab) {
        contacts.filter { contact ->
            val matchesCategory = when (selectedCategoryTab) {
                1 -> contact.role in listOf("DOCTOR", "MEDICAL_PROFESSIONAL")
                2 -> contact.role == "PATIENT"
                3 -> contact.role == "CAREGIVER"
                else -> true
            }

            val matchesSearch = if (searchInput.isBlank()) true else {
                contact.name.contains(searchInput, ignoreCase = true) ||
                        contact.role.contains(searchInput, ignoreCase = true) ||
                        (contact.specialty?.contains(searchInput, ignoreCase = true) == true)
            }

            matchesCategory && matchesSearch
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("new_call_contact_picker_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (callType == CallType.AUDIO) HealthNormalGreen else TealAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (callType == CallType.AUDIO) Icons.Default.Call else Icons.Default.Videocam,
                                contentDescription = null,
                                tint = if (callType == CallType.AUDIO) Color.White else NavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search field
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    placeholder = { Text("Search doctor, patient or caregiver...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Role Category Tabs
                val tabTitles = listOf("All", "Doctors", "Patients", "Caregivers")
                TabRow(
                    selectedTabIndex = selectedCategoryTab,
                    containerColor = Color(0xFFF8FAFC),
                    contentColor = NavyPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategoryTab]),
                            color = NavyPrimary
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedCategoryTab == index,
                            onClick = { selectedCategoryTab = index },
                            text = {
                                Text(
                                    text = tabTitle,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedCategoryTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Contact list
                if (filteredContacts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No linked contacts available in this category.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredContacts, key = { it.userId }) { contact ->
                            ContactPickerRowItem(
                                contact = contact,
                                onAudioCall = { onSelectContact(contact, CallType.AUDIO) },
                                onVideoCall = { onSelectContact(contact, CallType.VIDEO) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }
}

/**
 * Contact Picker Row Item inside New Call Dialog
 */
@Composable
fun ContactPickerRowItem(
    contact: UserAccountEntity,
    onAudioCall: () -> Unit,
    onVideoCall: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            when (contact.role) {
                                "DOCTOR", "MEDICAL_PROFESSIONAL" -> NavyDark
                                "CAREGIVER" -> Color(0xFF0F766E)
                                else -> Color(0xFF1D4ED8)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.avatarInitials.ifBlank { contact.name.take(2).uppercase() },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = contact.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = when (contact.role) {
                            "DOCTOR", "MEDICAL_PROFESSIONAL" -> contact.specialty ?: "Medical Specialist"
                            "CAREGIVER" -> "Linked Caregiver"
                            else -> "Primary Patient • ID: ${contact.userId}"
                        },
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Audio Call Button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFECFDF5),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onAudioCall() }
                        .testTag("dial_audio_contact_${contact.userId}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Audio Call",
                            tint = HealthNormalGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Video Call Button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEEF2FF),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onVideoCall() }
                        .testTag("dial_video_contact_${contact.userId}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
