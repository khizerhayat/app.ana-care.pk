package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AnaCareLogo
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loadingStatus by remember { mutableStateOf("Initializing secure patient portal...") }
    var progress by remember { mutableFloatStateOf(0.15f) }

    LaunchedEffect(Unit) {
        delay(600)
        progress = 0.45f
        loadingStatus = "Verifying AES-256 end-to-end encryption keys..."
        delay(600)
        progress = 0.80f
        loadingStatus = "Synchronizing encrypted vitals & medical schedule..."
        delay(600)
        progress = 1.0f
        loadingStatus = "Security handshake complete."
        delay(400)
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NavyDark,
                        NavyPrimary,
                        Color(0xFF0F2B52)
                    )
                )
            )
            .testTag("ana_care_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(28.dp)
        ) {
            // Branded Logo with Pulse Animation
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFFFFFFF),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("splash_logo_card")
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 20.dp)
                        .height(110.dp)
                        .width(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ana_care_app_logo_1787863935033),
                        contentDescription = "ANA CARE Splash Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("splash_ana_care_logo"),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Loading Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SkyLight,
                trackColor = Color(0x3338BDF8)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Security Status Text
            Text(
                text = loadingStatus,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF93C5FD),
                modifier = Modifier.testTag("splash_status_text")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Security Badges Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x2638BDF8))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Encrypted",
                    tint = HealthNormalGreen,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "HIPAA Compliant • Biometric Ready • E2EE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
