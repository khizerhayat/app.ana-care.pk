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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.UserAccountEntity
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent

@Composable
fun UserAccountSwitcherDialog(
    accounts: List<UserAccountEntity>,
    activeAccount: UserAccountEntity?,
    onSelectAccount: (String) -> Unit,
    onOpenAddUser: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("ALL") }

    val filteredAccounts = accounts.filter { account ->
        val matchesRole = when (selectedRoleFilter) {
            "PATIENTS" -> account.role == "PATIENT"
            "DOCTORS" -> account.role == "MEDICAL_PROFESSIONAL"
            "CAREGIVERS" -> account.role == "CAREGIVER"
            "ADMIN" -> account.role == "ADMIN"
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                account.name.contains(searchQuery, ignoreCase = true) ||
                account.userId.contains(searchQuery, ignoreCase = true) ||
                account.email.contains(searchQuery, ignoreCase = true)

        matchesRole && matchesSearch
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("user_account_switcher_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Switch Health Profile",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "10 Doctors • 50 Patients • Caregivers • Admin",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE0F2FE)
                    ) {
                        Text(
                            text = "${filteredAccounts.size}/${accounts.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search name, ID or role...", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("switcher_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Role Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val filterOptions = listOf(
                        "ALL" to "All (${accounts.size})",
                        "PATIENTS" to "Pt. (${accounts.count { it.role == "PATIENT" }})",
                        "DOCTORS" to "Dr. (${accounts.count { it.role == "MEDICAL_PROFESSIONAL" }})",
                        "CAREGIVERS" to "CG. (${accounts.count { it.role == "CAREGIVER" }})"
                    )

                    filterOptions.forEach { (key, label) ->
                        val isFilterSelected = selectedRoleFilter == key
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isFilterSelected) NavyPrimary else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .clickable { selectedRoleFilter = key }
                                .testTag("filter_chip_$key")
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isFilterSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isFilterSelected) Color.White else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(filteredAccounts, key = { it.userId }) { account ->
                        val isSelected = account.userId == activeAccount?.userId

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) TealAccent else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectAccount(account.userId)
                                    onDismiss()
                                }
                                .testTag("account_item_${account.userId}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
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
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = account.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (account.isPrimaryPatient) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFD1FAE5)
                                            ) {
                                                Text(
                                                    text = "Primary",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF065F46),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "ID: ${account.userId} • ${account.relationship.ifEmpty { account.role }}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = account.email,
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (account.role == "CAREGIVER") {
                                        Text(
                                            text = "Permission: ${account.caregiverPermissions.replace('_', ' ')}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF7C3AED),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Active Selection",
                                        tint = TealAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action to Add Multi-User Account
                Button(
                    onClick = {
                        onDismiss()
                        onOpenAddUser()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_add_multi_user_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Add User", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Family Member / Caregiver")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_switcher_dialog_button")
                ) {
                    Text("Close", color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
fun AddMultiUserDialog(
    onAddUser: (name: String, memberId: String, relationship: String, role: String, permissions: String, emergencyContact: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var memberId by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Caregiver (Son/Daughter)") }
    var role by remember { mutableStateOf("CAREGIVER") }
    var permissions by remember { mutableStateOf("FULL_ACCESS") }
    var emergencyContact by remember { mutableStateOf("") }

    val roles = listOf("CAREGIVER" to "Caregiver / Guardian", "PATIENT" to "Dependent / Family Patient")
    val relationships = listOf("Son/Daughter", "Spouse / Partner", "Parent", "Sibling", "Authorized Nurse/Aide")
    val permOptions = listOf("FULL_ACCESS" to "Full Access (View & Add)", "VIEW_ONLY" to "View Records Only", "EMERGENCY_ONLY" to "Emergency Access Only")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("add_multi_user_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Add Multi-User Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Grant secure access to a caregiver or family member using their Member ID.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    placeholder = { Text("e.g. John Vance") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("multiuser_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = memberId,
                    onValueChange = { memberId = it },
                    label = { Text("Member ID (Optional)") },
                    placeholder = { Text("e.g. 3002 or leave blank to auto-generate") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("multiuser_member_id_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship to Patient") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("multiuser_relationship_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Access Permissions Level",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    permOptions.forEach { (permKey, permLabel) ->
                        val isSelected = permissions == permKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) TealAccent else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { permissions = permKey }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) TealAccent else Color(0xFFCBD5E1))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = permLabel,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NavyPrimary else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_add_user_button")
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank() && memberId.isNotBlank()) {
                                onAddUser(name, memberId, relationship, role, permissions, emergencyContact)
                            }
                        },
                        enabled = name.isNotBlank() && memberId.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier.testTag("confirm_add_user_button")
                    ) {
                        Text("Grant Access")
                    }
                }
            }
        }
    }
}
