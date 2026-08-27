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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.EncryptedMessageEntity
import com.example.ui.theme.HealthNormalGreen
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
fun SecureMessagingScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messagesList.collectAsState()
    val activeUser by viewModel.activeAccount.collectAsState()
    val listState = rememberLazyListState()

    val sortedMessages = remember(messages) { messages.sortedByDescending { it.timestamp } }

    var messageInput by remember { mutableStateOf("") }
    var selectedDoctor by remember { mutableStateOf("Dr. Sarah Jenkins, MD") }
    var selectedDoctorId by remember { mutableStateOf("doc_sarah_10") }
    var showCipherInspector by remember { mutableStateOf(false) }
    var showAttachMenu by remember { mutableStateOf(false) }
    var attachedDocName by remember { mutableStateOf<String?>(null) }
    var attachedDocType by remember { mutableStateOf<String?>(null) }

    val quickQuestions = listOf(
        "Please review my latest vitals record.",
        "Requesting medication refill approval.",
        "Experiencing mild headache after morning routine.",
        "Sharing my weekly exercise & mobility log."
    )

    LaunchedEffect(sortedMessages.size) {
        if (sortedMessages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .testTag("secure_messaging_screen")
    ) {
        // E2EE Doctor Consultation Header Bar
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
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
                                .background(SkyLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("SJ", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = selectedDoctor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HealthNormalGreen))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Online • Encrypted Consultation Channel", fontSize = 10.sp, color = Color(0xFF93C5FD))
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x3338BDF8),
                        modifier = Modifier
                            .clickable { showCipherInspector = !showCipherInspector }
                            .testTag("cipher_inspector_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = HealthNormalGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AES-256", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SkyLight)
                        }
                    }
                }

                // Cipher Inspector Banner
                AnimatedVisibility(visible = showCipherInspector) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0A192F),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4D38BDF8)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "🔒 END-TO-END ENCRYPTION ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNormalGreen
                                )
                                Text(
                                    text = "Algorithm: AES-256-CBC • Key Exchange: ECDH 256 • HIPAA Compliant\nMessages and shared medical documents are encrypted before transmission.",
                                    fontSize = 9.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sortedMessages, key = { it.id }) { msg ->
                val isMe = msg.senderId == (activeUser?.userId ?: "pat_eleanor_01")
                MessageBubble(msg = msg, isMe = isMe)
            }
        }

        // Quick Medical Query Suggestions
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickQuestions) { query ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.clickable { messageInput = query }
                ) {
                    Text(
                        text = query,
                        fontSize = 11.sp,
                        color = NavyPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Attachment Preview Tag if attached
        if (attachedDocName != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Attached: $attachedDocName",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                    Text(
                        text = "Remove",
                        fontSize = 11.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            attachedDocName = null
                            attachedDocType = null
                        }
                    )
                }
            }
        }

        // Message Input Row with Attachments
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attach Button
                Box {
                    IconButton(
                        onClick = { showAttachMenu = true },
                        modifier = Modifier.testTag("attach_document_button")
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Attach Document", tint = TealAccent)
                    }

                    DropdownMenu(
                        expanded = showAttachMenu,
                        onDismissRequest = { showAttachMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Attach Vitals PDF Summary") },
                            leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626)) },
                            onClick = {
                                attachedDocName = "Vitals_Summary_Signed.pdf"
                                attachedDocType = "PDF_REPORT"
                                showAttachMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Attach Metabolic Lab Report") },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = TealAccent) },
                            onClick = {
                                attachedDocName = "CMP_Lab_Results.pdf"
                                attachedDocType = "LAB_RESULT"
                                showAttachMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Attach Care & Mobility Plan") },
                            leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF7C3AED)) },
                            onClick = {
                                attachedDocName = "CarePlan_Checkup_Guide.pdf"
                                attachedDocType = "CARE_PLAN"
                                showAttachMenu = false
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text("Write encrypted message...", fontSize = 13.sp) },
                    singleLine = false,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (messageInput.isNotBlank() || attachedDocName != null) {
                                viewModel.sendMessage(
                                    peerId = selectedDoctorId,
                                    peerName = selectedDoctor,
                                    messageText = messageInput.ifEmpty { "Attached document for your review." },
                                    attachmentName = attachedDocName,
                                    attachmentType = attachedDocType,
                                    attachmentSize = if (attachedDocName != null) "520 KB" else null
                                )
                                messageInput = ""
                                attachedDocName = null
                                attachedDocType = null
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .testTag("message_text_input")
                )

                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank() || attachedDocName != null) {
                            viewModel.sendMessage(
                                peerId = selectedDoctorId,
                                peerName = selectedDoctor,
                                messageText = messageInput.ifEmpty { "Attached document for your review." },
                                attachmentName = attachedDocName,
                                attachmentType = attachedDocType,
                                attachmentSize = if (attachedDocName != null) "520 KB" else null
                            )
                            messageInput = ""
                            attachedDocName = null
                            attachedDocType = null
                        }
                    },
                    modifier = Modifier.testTag("send_message_button")
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NavyPrimary,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    msg: EncryptedMessageEntity,
    isMe: Boolean
) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        // Sender Label
        Text(
            text = if (isMe) "You" else msg.senderName,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = if (isMe) NavyPrimary else Color(0xFFF1F5F9),
            border = if (!isMe) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
            modifier = Modifier.fillMaxWidth(0.85f).testTag("message_bubble_${msg.id}")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Attached document chip if present
                if (msg.attachmentName != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMe) Color(0xFF0F2544) else Color(0xFFFFFFFF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isMe) Color(0x3338BDF8) else Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Document Attached",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = msg.attachmentName,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMe) Color.White else NavyPrimary
                                )
                                Text(
                                    text = "Encrypted PDF • ${msg.attachmentSize ?: "400 KB"}",
                                    fontSize = 9.5.sp,
                                    color = if (isMe) Color(0xFF93C5FD) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = msg.messageText,
                    fontSize = 13.sp,
                    color = if (isMe) Color.White else Color(0xFF1E293B),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = if (isMe) SkyLight else Color(0xFF64748B),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeFormat.format(Date(msg.timestamp)),
                        fontSize = 9.5.sp,
                        color = if (isMe) Color(0xFF93C5FD) else Color(0xFF94A3B8)
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        when {
                            msg.isRead -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Read",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            System.currentTimeMillis() - msg.timestamp > 3000L -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Delivered",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Sent",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
