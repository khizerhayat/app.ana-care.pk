package com.example.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AddMultiUserDialog
import com.example.ui.components.AnaCareHeader
import com.example.ui.components.BiometricDialog
import com.example.ui.components.MfaVerificationDialog
import com.example.ui.components.PatientAlertPopupDialog
import com.example.ui.components.PdfPreviewDialog
import com.example.ui.components.UserAccountSwitcherDialog
import com.example.ui.screens.subtabs.AddRecordsScreen
import com.example.ui.screens.subtabs.ViewRecordsScreen
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.PortalViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainPortalScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val activeTab by viewModel.selectedMainTab.collectAsState()
    val activeAccount by viewModel.activeAccount.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val unreadMessageCount by viewModel.unreadMessageCount.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val currentAlertPopup by viewModel.currentAlertPopup.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    // Dialog state collectors
    val showBiometricDialog by viewModel.showBiometricDialog.collectAsState()
    val showMfaDialog by viewModel.showMfaDialog.collectAsState()
    val currentMfaCode by viewModel.currentMfaCode.collectAsState()
    val showUserSwitcherDialog by viewModel.showUserSwitcherDialog.collectAsState()
    val showAddMultiUserDialog by viewModel.showAddMultiUserDialog.collectAsState()
    val pdfExportedFile by viewModel.pdfExportedFile.collectAsState()

    val isDoctor = activeAccount?.role == "MEDICAL_PROFESSIONAL"
    val isAdmin = activeAccount?.role == "ADMIN"

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Listen to user feedback events and trigger login alert checks
    LaunchedEffect(Unit) {
        viewModel.triggerLoginAlertPopup()
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_portal_scaffold"),
        topBar = {
            Column {
                AnaCareHeader(
                    activeAccount = activeAccount,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onProfileClick = { viewModel.setMainTab(MainTab.PROFILE_SETTINGS) },
                    onOpenAccountSwitcher = { viewModel.setShowUserSwitcherDialog(true) },
                    onLogoutClick = { viewModel.logout() }
                )

                // Live OTA Update Banner if active
                if (appConfig.isUpdateBannerVisible) {
                    androidx.compose.material3.Surface(
                        color = Color(0xFF0284C7),
                        modifier = Modifier.fillMaxWidth().testTag("app_ota_update_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.Assessment,
                                    contentDescription = "Update",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "System Update v${appConfig.appVersionName}: ${appConfig.updateReleaseNotes}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                            androidx.compose.material3.IconButton(
                                onClick = { viewModel.dismissUpdateBanner() },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("dismiss_ota_update_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close update notification",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Live Maintenance Banner if enabled by Admin
                if (appConfig.isMaintenanceMode) {
                    androidx.compose.material3.Surface(
                        color = Color(0xFFDC2626),
                        modifier = Modifier.fillMaxWidth().testTag("system_maintenance_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️ System Maintenance: ${appConfig.maintenanceAnnouncement.ifBlank { "Live server upgrades in progress." }}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavyPrimary,
                contentColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_portal_bottom_navigation")
            ) {
                when {
                    isDoctor -> {
                        // 1. DOCTOR NAVIGATION
                        NavigationBarItem(
                            selected = activeTab == MainTab.DOCTOR_PORTAL || activeTab == MainTab.HOME,
                            onClick = { viewModel.setMainTab(MainTab.DOCTOR_PORTAL) },
                            icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Doctor Workstation", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Doctor Home", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.DOCTOR_PORTAL || activeTab == MainTab.HOME) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_doctor_portal")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.VIEW_RECORDS,
                            onClick = { viewModel.setMainTab(MainTab.VIEW_RECORDS) },
                            icon = { Icon(Icons.Default.Assessment, contentDescription = "Vitals & Charts", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Vitals & Charts", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.VIEW_RECORDS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_view_records")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.ADD_RECORDS,
                            onClick = { viewModel.setMainTab(MainTab.ADD_RECORDS) },
                            icon = { Icon(Icons.Default.NoteAdd, contentDescription = "Prescribe", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Prescribe", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.ADD_RECORDS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_add_vitals")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.LAB_RESULTS,
                            onClick = { viewModel.setMainTab(MainTab.LAB_RESULTS) },
                            icon = { Icon(Icons.Default.Biotech, contentDescription = "Lab Reviews", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Lab Reviews", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.LAB_RESULTS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_labs")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.MESSAGING,
                            onClick = { viewModel.setMainTab(MainTab.MESSAGING) },
                            icon = {
                                if (unreadMessageCount > 0) {
                                    BadgedBox(badge = { Badge { Text(unreadMessageCount.toString()) } }) {
                                        Icon(Icons.Default.Chat, contentDescription = "Consults", modifier = Modifier.size(20.dp))
                                    }
                                } else {
                                    Icon(Icons.Default.Chat, contentDescription = "Consults", modifier = Modifier.size(20.dp))
                                }
                            },
                            label = if (isLandscape) { { Text("Consults", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.MESSAGING) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_messages")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.PROFILE_SETTINGS,
                            onClick = { viewModel.setMainTab(MainTab.PROFILE_SETTINGS) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Physician ID", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Physician ID", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.PROFILE_SETTINGS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_profile")
                        )
                    }

                    isAdmin -> {
                        // 2. ADMIN NAVIGATION
                        NavigationBarItem(
                            selected = activeTab == MainTab.ADMIN_DASHBOARD,
                            onClick = { viewModel.setMainTab(MainTab.ADMIN_DASHBOARD) },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Console", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Console", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.ADMIN_DASHBOARD) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_admin_dashboard")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.VIEW_RECORDS,
                            onClick = { viewModel.setMainTab(MainTab.VIEW_RECORDS) },
                            icon = { Icon(Icons.Default.Assessment, contentDescription = "Audit Records", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Audit Records", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.VIEW_RECORDS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_view_records")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.LAB_RESULTS,
                            onClick = { viewModel.setMainTab(MainTab.LAB_RESULTS) },
                            icon = { Icon(Icons.Default.Biotech, contentDescription = "Clinic Labs", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Clinic Labs", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.LAB_RESULTS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_labs")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.MESSAGING,
                            onClick = { viewModel.setMainTab(MainTab.MESSAGING) },
                            icon = {
                                if (unreadMessageCount > 0) {
                                    BadgedBox(badge = { Badge { Text(unreadMessageCount.toString()) } }) {
                                        Icon(Icons.Default.Chat, contentDescription = "Broadcasts", modifier = Modifier.size(20.dp))
                                    }
                                } else {
                                    Icon(Icons.Default.Chat, contentDescription = "Broadcasts", modifier = Modifier.size(20.dp))
                                }
                            },
                            label = if (isLandscape) { { Text("Broadcasts", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.MESSAGING) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_messages")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.PROFILE_SETTINGS,
                            onClick = { viewModel.setMainTab(MainTab.PROFILE_SETTINGS) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Security Settings", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Settings", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.PROFILE_SETTINGS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_profile")
                        )
                    }

                    activeAccount?.role == "CAREGIVER" -> {
                        // 3. CAREGIVER NAVIGATION (Full Patient Menu & Screens)
                        NavigationBarItem(
                            selected = activeTab == MainTab.HOME,
                            onClick = { viewModel.setMainTab(MainTab.HOME) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Dashboard", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.HOME) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_home")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.VIEW_RECORDS,
                            onClick = { viewModel.setMainTab(MainTab.VIEW_RECORDS) },
                            icon = { Icon(Icons.Default.Assessment, contentDescription = "Records", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Records", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.VIEW_RECORDS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_view_records")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.ADD_RECORDS,
                            onClick = { viewModel.setMainTab(MainTab.ADD_RECORDS) },
                            icon = { Icon(Icons.Default.NoteAdd, contentDescription = "Care Log", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Care Log", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.ADD_RECORDS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_add_vitals")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.LAB_RESULTS,
                            onClick = { viewModel.setMainTab(MainTab.LAB_RESULTS) },
                            icon = { Icon(Icons.Default.Biotech, contentDescription = "Labs", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Labs", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.LAB_RESULTS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_labs")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.APPOINTMENTS,
                            onClick = { viewModel.setMainTab(MainTab.APPOINTMENTS) },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Visits", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Visits", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.APPOINTMENTS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_appointments")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.MESSAGING,
                            onClick = { viewModel.setMainTab(MainTab.MESSAGING) },
                            icon = {
                                if (unreadMessageCount > 0) {
                                    BadgedBox(badge = { Badge { Text(unreadMessageCount.toString()) } }) {
                                        Icon(Icons.Default.Chat, contentDescription = "Messages", modifier = Modifier.size(20.dp))
                                    }
                                } else {
                                    Icon(Icons.Default.Chat, contentDescription = "Messages", modifier = Modifier.size(20.dp))
                                }
                            },
                            label = if (isLandscape) { { Text("Messages", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.MESSAGING) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_messages")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.PROFILE_SETTINGS,
                            onClick = { viewModel.setMainTab(MainTab.PROFILE_SETTINGS) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Profile", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.PROFILE_SETTINGS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_profile")
                        )
                    }

                    else -> {
                        // 4. PATIENT NAVIGATION (All patient screens)
                        NavigationBarItem(
                            selected = activeTab == MainTab.HOME,
                            onClick = { viewModel.setMainTab(MainTab.HOME) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home Dashboard", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Home", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.HOME) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_home")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.VIEW_RECORDS,
                            onClick = { viewModel.setMainTab(MainTab.VIEW_RECORDS) },
                            icon = { Icon(Icons.Default.Assessment, contentDescription = "My Records", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("My Records", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.VIEW_RECORDS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_view_records")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.ADD_RECORDS,
                            onClick = { viewModel.setMainTab(MainTab.ADD_RECORDS) },
                            icon = { Icon(Icons.Default.NoteAdd, contentDescription = "Add Vitals", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Add Vitals", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.ADD_RECORDS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_add_vitals")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.LAB_RESULTS,
                            onClick = { viewModel.setMainTab(MainTab.LAB_RESULTS) },
                            icon = { Icon(Icons.Default.Biotech, contentDescription = "My Labs", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("My Labs", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.LAB_RESULTS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_labs")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.APPOINTMENTS,
                            onClick = { viewModel.setMainTab(MainTab.APPOINTMENTS) },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Visits", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Visits", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.APPOINTMENTS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_appointments")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.MESSAGING,
                            onClick = { viewModel.setMainTab(MainTab.MESSAGING) },
                            icon = {
                                if (unreadMessageCount > 0) {
                                    BadgedBox(badge = { Badge { Text(unreadMessageCount.toString()) } }) {
                                        Icon(Icons.Default.Chat, contentDescription = "Messages", modifier = Modifier.size(20.dp))
                                    }
                                } else {
                                    Icon(Icons.Default.Chat, contentDescription = "Messages", modifier = Modifier.size(20.dp))
                                }
                            },
                            label = if (isLandscape) { { Text("Messages", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.MESSAGING) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_messages")
                        )

                        NavigationBarItem(
                            selected = activeTab == MainTab.PROFILE_SETTINGS,
                            onClick = { viewModel.setMainTab(MainTab.PROFILE_SETTINGS) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(20.dp)) },
                            label = if (isLandscape) { { Text("Profile", fontSize = 9.5.sp, fontWeight = if (activeTab == MainTab.PROFILE_SETTINGS) FontWeight.Bold else FontWeight.Normal) } } else null,
                            alwaysShowLabel = isLandscape,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = SkyLight,
                                indicatorColor = SkyLight,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("bottom_tab_profile")
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 1280.dp)
            ) {
                when (activeTab) {
                    MainTab.HOME -> {
                        if (isDoctor) {
                            DoctorDashboardScreen(viewModel = viewModel)
                        } else {
                            HomeScreen(viewModel = viewModel, onNavigateToTab = { viewModel.setMainTab(it) })
                        }
                    }
                    MainTab.DOCTOR_PORTAL -> DoctorDashboardScreen(viewModel = viewModel)
                    MainTab.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                    MainTab.ADD_RECORDS -> AddRecordsScreen(viewModel = viewModel)
                    MainTab.VIEW_RECORDS -> ViewRecordsScreen(viewModel = viewModel)
                    MainTab.LAB_RESULTS -> LabResultsScreen(viewModel = viewModel)
                    MainTab.APPOINTMENTS -> AppointmentsScreen(viewModel = viewModel)
                    MainTab.MESSAGING -> SecureMessagingScreen(viewModel = viewModel)
                    MainTab.PROFILE_SETTINGS -> ProfileSettingsScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Modal Dialogs
    if (showBiometricDialog) {
        BiometricDialog(
            userName = activeAccount?.name ?: "Patient",
            onSuccess = { viewModel.completeBiometricLogin() },
            onDismiss = { viewModel.setShowBiometricDialog(false) }
        )
    }

    if (showMfaDialog) {
        MfaVerificationDialog(
            generatedCode = currentMfaCode,
            onVerify = { viewModel.verifyMfaAndCompleteLogin(it) },
            onDismiss = { viewModel.setShowMfaDialog(false) }
        )
    }

    if (showUserSwitcherDialog) {
        UserAccountSwitcherDialog(
            accounts = allAccounts,
            activeAccount = activeAccount,
            onSelectAccount = { viewModel.switchAccount(it) },
            onOpenAddUser = { viewModel.setShowAddMultiUserDialog(true) },
            onDismiss = { viewModel.setShowUserSwitcherDialog(false) }
        )
    }

    if (showAddMultiUserDialog) {
        AddMultiUserDialog(
            onAddUser = { name, memberId, rel, role, perms, emerg ->
                viewModel.addMultiUserAccount(name, memberId, rel, role, perms, emerg)
            },
            onDismiss = { viewModel.setShowAddMultiUserDialog(false) }
        )
    }

    pdfExportedFile?.let { file ->
        PdfPreviewDialog(
            file = file,
            onShare = { viewModel.shareCurrentPdf() },
            onDismiss = { viewModel.clearPdfExport() }
        )
    }

    // Patient Screen Modal Pop-up from Doctor / Admin
    currentAlertPopup?.let { alert ->
        PatientAlertPopupDialog(
            alert = alert,
            onAcknowledge = { alertId -> viewModel.acknowledgeAlertNote(alertId) },
            onDismiss = { viewModel.dismissCurrentAlertPopup() },
            onActionLinkClick = { link ->
                when (link) {
                    "MEDICATIONS" -> {
                        viewModel.setMainTab(MainTab.VIEW_RECORDS)
                        viewModel.setViewRecordsSubTab(1)
                    }
                    "LABS" -> {
                        viewModel.setMainTab(MainTab.LAB_RESULTS)
                    }
                    "VITALS" -> {
                        viewModel.setMainTab(MainTab.VIEW_RECORDS)
                        viewModel.setViewRecordsSubTab(0)
                    }
                    "APPOINTMENTS" -> {
                        viewModel.setMainTab(MainTab.APPOINTMENTS)
                    }
                }
            }
        )
    }
}
