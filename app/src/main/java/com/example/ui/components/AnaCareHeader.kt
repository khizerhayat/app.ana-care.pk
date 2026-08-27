package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.UserAccountEntity
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent

@Composable
fun AnaCareHeader(
    activeAccount: UserAccountEntity? = null,
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit,
    onProfileClick: () -> Unit,
    onOpenAccountSwitcher: () -> Unit = {},
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDoctor = activeAccount?.role == "MEDICAL_PROFESSIONAL"
    val isAdmin = activeAccount?.role == "ADMIN"
    val isCaregiver = activeAccount?.role == "CAREGIVER"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ana_care_main_header"),
        color = NavyPrimary,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Brand Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onProfileClick)
                        .testTag("ana_care_brand_header_container")
                ) {
                    AnaCareHeaderBrand(isDarkMode = true)
                }

                // Right Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Switch Account Button
                    IconButton(
                        onClick = onOpenAccountSwitcher,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_account_switcher_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Profile",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Dark/Light Mode Switcher
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkMode) "Light Mode" else "Dark Mode",
                            tint = if (isDarkMode) Color(0xFFFDE047) else SkyLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Profile / Settings Button
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Settings",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Logout Button
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFFDA4AF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
