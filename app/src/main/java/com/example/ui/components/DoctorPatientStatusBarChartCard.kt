package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserAccountEntity
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import kotlin.math.max

/**
 * Premium, polished, perfectly aligned interactive Bar Graph Card for the Doctor Dashboard.
 * Accurately anchors bars to a calibrated 0-baseline, shows background track capsules,
 * and provides responsive filtering and patient triage directives.
 */
@Composable
fun DoctorPatientStatusBarChartCard(
    totalPatientsCount: Int,
    normalPatientsCount: Int,
    criticalPatientsCount: Int,
    selectedScope: String, // "MY_PATIENTS" or "ALL_PATIENTS"
    onScopeChange: (String) -> Unit,
    selectedBarFilter: String?, // null for ALL, "NORMAL", "CRITICAL"
    onSelectBarFilter: (String?) -> Unit,
    criticalPatients: List<UserAccountEntity> = emptyList(),
    onSelectCriticalPatient: (UserAccountEntity) -> Unit = {},
    onSendCaregiverInstruction: (UserAccountEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val maxCount = max(totalPatientsCount, 1)
    val normalPercent = if (totalPatientsCount > 0) (normalPatientsCount * 100 / totalPatientsCount) else 0
    val criticalPercent = if (totalPatientsCount > 0) (criticalPatientsCount * 100 / totalPatientsCount) else 0

    // Calibrated dynamic animations for bar heights
    val animatedTotalFraction by animateFloatAsState(
        targetValue = if (maxCount > 0) (totalPatientsCount.toFloat() / maxCount).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "total_bar_anim"
    )
    val animatedNormalFraction by animateFloatAsState(
        targetValue = if (maxCount > 0) (normalPatientsCount.toFloat() / maxCount).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "normal_bar_anim"
    )
    val animatedCriticalFraction by animateFloatAsState(
        targetValue = if (maxCount > 0) (criticalPatientsCount.toFloat() / maxCount).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "critical_bar_anim"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("doctor_patient_status_bar_chart_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 1. Header Row with Title, Badge, and Scope Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "Triage Analytics",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Patient Clinical Status",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            if (selectedBarFilter != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (selectedBarFilter == "CRITICAL") Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                                    border = BorderStroke(1.dp, if (selectedBarFilter == "CRITICAL") Color(0xFFFCA5A5) else Color(0xFF86EFAC))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Filter: $selectedBarFilter",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedBarFilter == "CRITICAL") Color(0xFFDC2626) else Color(0xFF16A34A)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear filter",
                                            tint = if (selectedBarFilter == "CRITICAL") Color(0xFFDC2626) else Color(0xFF16A34A),
                                            modifier = Modifier
                                                .size(13.dp)
                                                .clickable { onSelectBarFilter(null) }
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = "Real-time biometric triage & cohort distribution",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Scope Switcher Pills
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    val isMyScope = selectedScope == "MY_PATIENTS"
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMyScope) NavyPrimary else Color.Transparent,
                        modifier = Modifier
                            .clickable { onScopeChange("MY_PATIENTS") }
                            .testTag("scope_toggle_my_patients")
                    ) {
                        Text(
                            text = "🩺 Connected",
                            fontSize = 11.sp,
                            fontWeight = if (isMyScope) FontWeight.Bold else FontWeight.Medium,
                            color = if (isMyScope) Color.White else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }

                    val isAllScope = selectedScope == "ALL_PATIENTS"
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isAllScope) NavyPrimary else Color.Transparent,
                        modifier = Modifier
                            .clickable { onScopeChange("ALL_PATIENTS") }
                            .testTag("scope_toggle_all_patients")
                    ) {
                        Text(
                            text = "🏥 All Clinic",
                            fontSize = 11.sp,
                            fontWeight = if (isAllScope) FontWeight.Bold else FontWeight.Medium,
                            color = if (isAllScope) Color.White else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. The Main Bar Chart Visual Container
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("doctor_bar_graph_plot_area")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                ) {
                    // Instruction / Hint row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PATIENT VOLUME (COHORT N = $totalPatientsCount)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFE2E8F0).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tap bar to filter roster below",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dedicated Calibrated Plot Area (Gridlines + Pillars aligned with fixed baseline)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        // 1. Precise Background Grid Lines & Y-Axis Scale
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            val gridSteps = listOf(
                                "$maxCount" to "Max",
                                "${(maxCount * 3) / 4}" to "75%",
                                "${maxCount / 2}" to "50%",
                                "${maxCount / 4}" to "25%",
                                "0" to "Base"
                            )
                            gridSteps.forEachIndexed { index, (valLabel, percentLabel) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.width(42.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = valLabel,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Text(
                                            text = percentLabel,
                                            fontSize = 8.5.sp,
                                            color = Color(0xFFCBD5E1)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(if (index == gridSteps.size - 1) 1.5.dp else 1.dp)
                                            .background(if (index == gridSteps.size - 1) Color(0xFF94A3B8) else Color(0xFFE2E8F0))
                                    )
                                }
                            }
                        }

                        // 2. Foreground 3 Bars (Total, Normal, Critical) perfectly anchored to baseline
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 50.dp, end = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // 1. TOTAL PATIENTS BAR
                            CalibratedBarItem(
                                count = totalPatientsCount,
                                percentageText = "100%",
                                animatedFraction = animatedTotalFraction,
                                gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                                trackColor = Color(0xFFDBEAFE).copy(alpha = 0.5f),
                                badgeBgColor = Color(0xFFDBEAFE),
                                badgeFgColor = Color(0xFF1E40AF),
                                isSelected = selectedBarFilter == null,
                                onClick = { onSelectBarFilter(null) },
                                testTag = "bar_total_patients",
                                modifier = Modifier.weight(1f)
                            )

                            // 2. NORMAL PATIENTS BAR
                            CalibratedBarItem(
                                count = normalPatientsCount,
                                percentageText = "$normalPercent%",
                                animatedFraction = animatedNormalFraction,
                                gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                                trackColor = Color(0xFFDCFCE7).copy(alpha = 0.5f),
                                badgeBgColor = Color(0xFFDCFCE7),
                                badgeFgColor = Color(0xFF15803D),
                                isSelected = selectedBarFilter == "NORMAL",
                                onClick = {
                                    if (selectedBarFilter == "NORMAL") onSelectBarFilter(null) else onSelectBarFilter("NORMAL")
                                },
                                testTag = "bar_normal_patients",
                                modifier = Modifier.weight(1f)
                            )

                            // 3. CRITICAL PATIENTS BAR
                            CalibratedBarItem(
                                count = criticalPatientsCount,
                                percentageText = "$criticalPercent%",
                                animatedFraction = animatedCriticalFraction,
                                gradientColors = listOf(Color(0xFFF43F5E), Color(0xFFE11D48)),
                                trackColor = Color(0xFFFEE2E2).copy(alpha = 0.5f),
                                badgeBgColor = Color(0xFFFEE2E2),
                                badgeFgColor = Color(0xFFB91C1C),
                                isSelected = selectedBarFilter == "CRITICAL",
                                onClick = {
                                    if (selectedBarFilter == "CRITICAL") onSelectBarFilter(null) else onSelectBarFilter("CRITICAL")
                                },
                                testTag = "bar_critical_patients",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // 3. Dedicated X-Axis Labels Row (Aligned directly underneath the bars)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 50.dp, end = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BarLabelItem(
                            title = "Total Cohort",
                            subtitle = "All Registered",
                            icon = Icons.Default.Person,
                            iconTint = Color(0xFF2563EB),
                            isSelected = selectedBarFilter == null,
                            onClick = { onSelectBarFilter(null) },
                            modifier = Modifier.weight(1f)
                        )

                        BarLabelItem(
                            title = "Normal",
                            subtitle = "Vitals Stable",
                            icon = Icons.Default.CheckCircle,
                            iconTint = Color(0xFF16A34A),
                            isSelected = selectedBarFilter == "NORMAL",
                            onClick = {
                                if (selectedBarFilter == "NORMAL") onSelectBarFilter(null) else onSelectBarFilter("NORMAL")
                            },
                            modifier = Modifier.weight(1f)
                        )

                        BarLabelItem(
                            title = "Critical",
                            subtitle = "Urgent Triage",
                            icon = Icons.Default.NotificationsActive,
                            iconTint = Color(0xFFE11D48),
                            isSelected = selectedBarFilter == "CRITICAL",
                            onClick = {
                                if (selectedBarFilter == "CRITICAL") onSelectBarFilter(null) else onSelectBarFilter("CRITICAL")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. 3 Detailed Status KPI Metric Cards Below Graph
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Summary Card
                StatusSummaryPillCard(
                    title = "TOTAL REGISTERED",
                    countText = "$totalPatientsCount Patients",
                    badgeText = "100%",
                    badgeColor = Color(0xFFDBEAFE),
                    badgeTextColor = Color(0xFF1E40AF),
                    description = "Monitored on active EMR",
                    borderColor = if (selectedBarFilter == null) Color(0xFF3B82F6) else Color(0xFFE2E8F0),
                    onClick = { onSelectBarFilter(null) },
                    modifier = Modifier.weight(1f)
                )

                // Normal Summary Card
                StatusSummaryPillCard(
                    title = "NORMAL / STABLE",
                    countText = "$normalPatientsCount Patients",
                    badgeText = "$normalPercent%",
                    badgeColor = Color(0xFFDCFCE7),
                    badgeTextColor = Color(0xFF15803D),
                    description = "BP & Glucose in range",
                    borderColor = if (selectedBarFilter == "NORMAL") Color(0xFF10B981) else Color(0xFFE2E8F0),
                    onClick = { onSelectBarFilter("NORMAL") },
                    modifier = Modifier.weight(1f)
                )

                // Critical Summary Card
                StatusSummaryPillCard(
                    title = "CRITICAL / ALERT",
                    countText = "$criticalPatientsCount Patients",
                    badgeText = "$criticalPercent%",
                    badgeColor = Color(0xFFFEE2E2),
                    badgeTextColor = Color(0xFFB91C1C),
                    description = "High BP / Biometric Alert",
                    borderColor = if (selectedBarFilter == "CRITICAL") Color(0xFFEF4444) else Color(0xFFE2E8F0),
                    onClick = { onSelectBarFilter("CRITICAL") },
                    modifier = Modifier.weight(1f)
                )
            }

            // 4. Critical Patient Direct Action Alert Banner (if critical patients exist)
            if (criticalPatientsCount > 0 && criticalPatients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                val targetCritical = criticalPatients.first()
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF1F2),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("critical_patient_triage_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE11D48),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ACTION REQUIRED: ${targetCritical.name} (Pt. ID: ${targetCritical.userId})",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9F1239)
                                )
                                Text(
                                    text = "Elevated Blood Pressure (145/95 mmHg) • Diagnosis: ${targetCritical.diagnosis.ifEmpty { "Hypertension" }}",
                                    fontSize = 11.sp,
                                    color = Color(0xFFBE123C)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onSelectCriticalPatient(targetCritical) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("chart_jump_critical_btn")
                            ) {
                                Text("Open Chart", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = { onSendCaregiverInstruction(targetCritical) },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE11D48)),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("instruction_critical_btn")
                            ) {
                                Text("Directive", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Calibrated Bar Item rendered inside the Plot Area.
 * Features a background capsule track (100% height), an animated fill bar anchored to the bottom,
 * and a floating badge positioned cleanly on top.
 */
@Composable
private fun CalibratedBarItem(
    count: Int,
    percentageText: String,
    animatedFraction: Float,
    gradientColors: List<Color>,
    trackColor: Color,
    badgeBgColor: Color,
    badgeFgColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
            .clickable { onClick() }
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Floating Top Metric Badge (Fixed padding above the track)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) badgeBgColor else Color(0xFFF1F5F9),
            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) badgeFgColor else Color(0xFFCBD5E1)),
            modifier = Modifier
                .padding(bottom = 6.dp)
                .shadow(if (isSelected) 2.dp else 0.dp, RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "$count ($percentageText)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) badgeFgColor else Color(0xFF475569),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        // The Capsule Track & Active Gradient Fill Bar (Anchored at Baseline)
        Box(
            modifier = Modifier
                .width(46.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(trackColor)
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) badgeFgColor.copy(alpha = 0.5f) else Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Gradient Active Bar Fill
            val fillHeightFraction = max(0.04f, animatedFraction)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fillHeightFraction)
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(Brush.verticalGradient(gradientColors))
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                1.5.dp,
                                Color.White.copy(alpha = 0.9f),
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                            )
                        } else Modifier
                    )
            )
        }
    }
}

/**
 * X-Axis Label Item positioned cleanly below the baseline of the chart.
 */
@Composable
private fun BarLabelItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) iconTint else Color(0xFF94A3B8),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) NavyPrimary else Color(0xFF475569),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        Text(
            text = subtitle,
            fontSize = 10.sp,
            color = if (isSelected) iconTint.copy(alpha = 0.85f) else Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/**
 * Summary KPI Card underneath the Bar Chart for quick triage reference.
 */
@Composable
private fun StatusSummaryPillCard(
    title: String,
    countText: String,
    badgeText: String,
    badgeColor: Color,
    badgeTextColor: Color,
    description: String,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, borderColor),
        shadowElevation = 1.dp,
        modifier = modifier
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = countText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
