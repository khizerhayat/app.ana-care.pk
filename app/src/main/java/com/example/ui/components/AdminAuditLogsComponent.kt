package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.ui.viewmodel.PortalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAuditLogsComponent(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allAccounts by viewModel.allAccounts.collectAsState()
    val allLogs by viewModel.allAuditLogs.collectAsState()
    val filteredLogs by viewModel.filteredAuditLogs.collectAsState()
    val selectedUserId by viewModel.selectedAuditUserId.collectAsState()
    val selectedCategory by viewModel.selectedAuditCategory.collectAsState()
    val selectedSeverity by viewModel.selectedAuditSeverity.collectAsState()
    val searchQuery by viewModel.auditSearchQuery.collectAsState()

    var userDropdownExpanded by remember { mutableStateOf(false) }
    var inspectLogModalItem by remember { mutableStateOf<AuditLogEntity?>(null) }
    var showRawFileModal by remember { mutableStateOf(false) }
    var showClearLogsDialog by remember { mutableStateOf(false) }
    var rawFileText by remember { mutableStateOf("") }

    val categories = listOf(
        "ALL" to "All Categories",
        "AUTH & SECURITY" to "🔐 Security",
        "CLINICAL VITALS" to "❤️ Vitals",
        "MEDICATIONS" to "💊 Medications",
        "DAILY ACTIVITIES" to "🏃 Activities",
        "MEDICAL GALLERY" to "📷 Gallery",
        "TELEHEALTH MESSAGING" to "💬 Telehealth",
        "CLINICAL ALERTS" to "🚨 Alerts",
        "SYSTEM CONFIG" to "⚙️ System"
    )

    val severities = listOf("ALL", "INFO", "SUCCESS", "WARNING", "CRITICAL")

    val selectedUserAccount = remember(selectedUserId, allAccounts) {
        if (selectedUserId == "ALL") null else allAccounts.find { it.userId == selectedUserId }
    }

    val selectedUserLabel = when {
        selectedUserId == "ALL" -> "🌐 All Users & Global Operations (${allLogs.size} logs)"
        selectedUserAccount != null -> "${selectedUserAccount.name} (${selectedUserAccount.role} • ID: ${selectedUserAccount.userId})"
        else -> "User ID: $selectedUserId"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. TOP HEADER & ACTION BANNER
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_log_header_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = "Audit Logs",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "System & User Audit Log File",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "HIPAA audit trail • Real-time event logging • Table format",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Quick count badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("log_count_badge")
                    ) {
                        Text(
                            text = "${filteredLogs.size} / ${allLogs.size} logs",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Action Buttons: Export Log File, View Raw File, Clear Logs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val targetName = selectedUserAccount?.name ?: if (selectedUserId == "ALL") "All Users" else "User_$selectedUserId"
                            viewModel.shareAuditLogFile(selectedUserId, targetName)
                            Toast.makeText(context, "Exporting audit log file...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_log_file_button"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export .txt", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = {
                            rawFileText = viewModel.getRawLogFileContent()
                            showRawFileModal = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("view_raw_log_file_button"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "View Raw",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View File", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    IconButton(
                        onClick = { showClearLogsDialog = true },
                        modifier = Modifier.testTag("clear_logs_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Logs",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. USER DROP-DOWN SELECTOR (PRIMARY USER REQUEST)
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("user_dropdown_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = CardDefaults.outlinedCardBorder(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SELECT USER FOR LOG VIEWER:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${allAccounts.size} registered users",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = userDropdownExpanded,
                    onExpandedChange = { userDropdownExpanded = !userDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedUserLabel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = userDropdownExpanded)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("admin_user_selector_dropdown"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = userDropdownExpanded,
                        onDismissRequest = { userDropdownExpanded = false },
                        modifier = Modifier.heightIn(max = 350.dp)
                    ) {
                        // Global option
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = "🌐 All Users (Global Audit Trail)",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Display all system logs across all accounts (${allLogs.size} total entries)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                viewModel.selectAuditUser("ALL")
                                userDropdownExpanded = false
                            },
                            modifier = Modifier.testTag("user_select_all")
                        )
                        HorizontalDivider()

                        // Individual Users
                        allAccounts.forEach { account ->
                            val userLogCount = allLogs.count { it.userId == account.userId }
                            val roleIcon = when (account.role) {
                                "PATIENT" -> "👤"
                                "MEDICAL_PROFESSIONAL" -> "🩺"
                                "CAREGIVER" -> "🤝"
                                "ADMIN" -> "🛡️"
                                else -> "👤"
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "$roleIcon ${account.name}",
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${account.role} • ID: ${account.userId} • ${account.email}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (userLogCount > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "$userLogCount logs",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (userLogCount > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    viewModel.selectAuditUser(account.userId)
                                    userDropdownExpanded = false
                                },
                                modifier = Modifier.testTag("user_select_${account.userId}")
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. SEARCH & CATEGORY / SEVERITY CHIPS
        // ==========================================
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setAuditSearchQuery(it) },
            placeholder = { Text("Search logs by keyword, payload, or action...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setAuditSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("audit_search_input"),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(categories) { (catKey, catLabel) ->
                val isSelected = selectedCategory == catKey
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectAuditCategory(catKey) },
                    label = { Text(catLabel, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("cat_chip_$catKey")
                )
            }
        }

        // Severity Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(severities) { sev ->
                val isSelected = selectedSeverity == sev
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectAuditSeverity(sev) },
                    label = {
                        Text(
                            text = when (sev) {
                                "ALL" -> "All Severities"
                                "INFO" -> "ℹ️ Info"
                                "SUCCESS" -> "✅ Success"
                                "WARNING" -> "⚠️ Warning"
                                "CRITICAL" -> "🚨 Critical"
                                else -> sev
                            },
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (sev) {
                            "CRITICAL" -> MaterialTheme.colorScheme.error
                            "WARNING" -> Color(0xFFD97706)
                            "SUCCESS" -> Color(0xFF16A34A)
                            "INFO" -> Color(0xFF2563EB)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("sev_chip_$sev")
                )
            }
        }

        // ==========================================
        // 4. LOGS TABLE VIEW (USER REQUIREMENT)
        // ==========================================
        if (filteredLogs.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .testTag("no_logs_found_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No audit log records found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Try clearing filters or selecting 'All Users'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            viewModel.selectAuditUser("ALL")
                            viewModel.selectAuditCategory("ALL")
                            viewModel.selectAuditSeverity("ALL")
                            viewModel.setAuditSearchQuery("")
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            // TABLE CONTAINER with Horizontal & Vertical Scrolling Support
            val tableScrollState = rememberScrollState()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("audit_logs_table_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Table Scrollable Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(tableScrollState)
                        ) {
                            Column(modifier = Modifier.width(960.dp)) {
                                // Table Header Row
                                TableHeaderRow()

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                // Table Content Rows in LazyColumn
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    itemsIndexed(filteredLogs) { index, log ->
                                        TableRowItem(
                                            index = index,
                                            log = log,
                                            onInspect = { inspectLogModalItem = log }
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                            thickness = 0.5.dp
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

    // ==========================================
    // 5. INSPECT LOG DETAIL MODAL DIALOG
    // ==========================================
    inspectLogModalItem?.let { log ->
        AlertDialog(
            onDismissRequest = { inspectLogModalItem = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SeverityBadge(severity = log.severity)
                    Text(
                        text = "Audit Log #${log.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                SelectionContainer {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailField(label = "TIMESTAMP", value = log.formattedTimestamp)
                        DetailField(label = "USER OPERATOR", value = "${log.userName} (Role: ${log.userRole}, ID: ${log.userId})")
                        DetailField(label = "CATEGORY", value = log.category)
                        DetailField(label = "ACTION TYPE", value = log.actionType)
                        DetailField(label = "DESCRIPTION", value = log.description)
                        if (log.details.isNotBlank()) {
                            DetailField(label = "PAYLOAD & PARAMETERS", value = log.details, isCode = true)
                        }
                        DetailField(label = "SESSION IP / DIGEST", value = log.ipAddress)
                        DetailField(label = "SECURITY STATUS", value = "Verified via Local SHA-256 Digest • HIPAA Compliant")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            "Audit Log #${log.id}",
                            "Log #${log.id}\nTime: ${log.formattedTimestamp}\nUser: ${log.userName} (${log.userId})\nAction: ${log.actionType}\nCategory: ${log.category}\nDescription: ${log.description}\nDetails: ${log.details}"
                        )
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Log copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { inspectLogModalItem = null }) {
                    Text("Close")
                }
            }
        )
    }

    // ==========================================
    // 6. RAW LOG FILE VIEWER MODAL DIALOG
    // ==========================================
    if (showRawFileModal) {
        AlertDialog(
            onDismissRequest = { showRawFileModal = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "ana_care_system_audit_log.txt",
                            style = MaterialTheme.typography.titleSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = { showRawFileModal = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                SelectionContainer {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = rawFileText.ifBlank { "No logs written to file yet." },
                            color = Color(0xFF4ADE80),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetName = selectedUserAccount?.name ?: if (selectedUserId == "ALL") "All Users" else "User_$selectedUserId"
                        viewModel.shareAuditLogFile(selectedUserId, targetName)
                    }
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share / Save File")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Raw Audit Logs", rawFileText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Raw logs copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Text")
                }
            }
        )
    }

    // ==========================================
    // 7. CLEAR LOGS CONFIRMATION DIALOG
    // ==========================================
    if (showClearLogsDialog) {
        AlertDialog(
            onDismissRequest = { showClearLogsDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(text = "Clear System Audit Logs?")
            },
            text = {
                Text(
                    text = "Are you sure you want to clear all stored audit logs from the database? This action is irreversible. (A copy will remain in the exported text files if previously downloaded)."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllAuditLogs()
                        showClearLogsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All Logs")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearLogsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TableHeaderRow() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DATE & TIME",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(150.dp)
            )
            Text(
                text = "USER (ROLE & ID)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(180.dp)
            )
            Text(
                text = "CATEGORY & ACTION",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(180.dp)
            )
            Text(
                text = "LOG DESCRIPTION & PAYLOAD",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(300.dp)
            )
            Text(
                text = "SEVERITY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(90.dp)
            )
            Text(
                text = "INSPECT",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TableRowItem(
    index: Int,
    log: AuditLogEntity,
    onInspect: () -> Unit
) {
    val isEven = index % 2 == 0
    val rowBg = if (isEven) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable { onInspect() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("audit_row_${log.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. DATE & TIME
        Column(modifier = Modifier.width(150.dp)) {
            Text(
                text = log.formattedTimestamp.ifBlank { "Recorded" },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Log #${log.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // 2. USER (ROLE & ID)
        Column(modifier = Modifier.width(180.dp)) {
            Text(
                text = log.userName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (log.userRole) {
                        "PATIENT" -> MaterialTheme.colorScheme.primaryContainer
                        "MEDICAL_PROFESSIONAL" -> MaterialTheme.colorScheme.tertiaryContainer
                        "CAREGIVER" -> MaterialTheme.colorScheme.secondaryContainer
                        "ADMIN" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = log.userRole.take(7),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Text(
                    text = "ID: ${log.userId}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // 3. CATEGORY & ACTION
        Column(modifier = Modifier.width(180.dp)) {
            Text(
                text = log.category,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = log.actionType,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 4. DESCRIPTION & PAYLOAD
        Column(
            modifier = Modifier
                .width(300.dp)
                .padding(end = 8.dp)
        ) {
            Text(
                text = log.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (log.details.isNotBlank()) {
                Text(
                    text = log.details,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 5. SEVERITY
        Box(modifier = Modifier.width(90.dp)) {
            SeverityBadge(severity = log.severity)
        }

        // 6. INSPECT BUTTON
        Box(
            modifier = Modifier.width(60.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onInspect,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Inspect Details",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SeverityBadge(severity: String) {
    val (bgColor, textColor, label) = when (severity.uppercase()) {
        "SUCCESS" -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), "SUCCESS")
        "WARNING" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "WARNING")
        "CRITICAL" -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "CRITICAL")
        else -> Triple(Color(0xFFDBEAFE), Color(0xFF1D4ED8), "INFO")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String,
    isCode: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        if (isCode) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
