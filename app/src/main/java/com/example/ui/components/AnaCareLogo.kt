package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent

/**
 * High-fidelity representation of the ANA Care Logo.
 * Uses the Dark Logo (pure white typography on dark background) or Light Logo (navy typography on light background).
 */
@Composable
fun AnaCareLogo(
    modifier: Modifier = Modifier,
    isDarkBackground: Boolean = isSystemInDarkTheme(),
    showTagline: Boolean = true,
    scaleFactor: Float = 1.0f,
    animated: Boolean = false
) {
    val primaryColor = if (isDarkBackground) Color.White else NavyPrimary
    val accentColor = if (isDarkBackground) Color.White else Color(0xFF1E3A5F)
    val subColor = if (isDarkBackground) Color(0xFF93C5FD) else Color(0xFF475569)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.97f,
            targetValue = 1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static"
        )
    }

    Column(
        modifier = modifier
            .scale(if (animated) pulseScale else 1f)
            .testTag(if (isDarkBackground) "ana_care_dark_logo" else "ana_care_light_logo"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main Stylized Monogram Brand Name: A N A ®
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // First 'A' with stylish architectural serif
            Text(
                text = "A",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = (38 * scaleFactor).sp,
                color = primaryColor,
                letterSpacing = 2.sp
            )
            
            // Stylized 'N' with top serif touch
            Text(
                text = "N",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = (42 * scaleFactor).sp,
                color = primaryColor,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = (2 * scaleFactor).dp)
            )

            // Second 'A'
            Text(
                text = "A",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = (38 * scaleFactor).sp,
                color = primaryColor,
                letterSpacing = 2.sp
            )

            // ® Registered symbol
            Text(
                text = "®",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = (11 * scaleFactor).sp,
                color = accentColor,
                modifier = Modifier
                    .padding(bottom = (16 * scaleFactor).dp, start = (2 * scaleFactor).dp)
            )
        }

        // 'C  A  R  E'
        Text(
            text = "C   A   R   E",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = (14 * scaleFactor).sp,
            color = accentColor,
            letterSpacing = (4 * scaleFactor).sp,
            modifier = Modifier.padding(top = (1 * scaleFactor).dp)
        )

        if (showTagline) {
            Spacer(modifier = Modifier.height((3 * scaleFactor).dp))
            // Tagline
            Text(
                text = "Home Healthcare Service Provider",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = (9.5f * scaleFactor).sp,
                color = subColor,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Image-based Logo Banner Composable supporting exact raster logo drawables
 */
@Composable
fun AnaCareImageBanner(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme()
) {
    Image(
        painter = painterResource(id = R.drawable.ana_care_app_logo_1787863935033),
        contentDescription = "ANA Care Logo",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .testTag(if (isDarkMode) "ana_care_dark_logo_image" else "ana_care_light_logo_image")
    )
}

/**
 * Centered Official Logo image with rounded white background
 */
@Composable
fun AnaCareAppLogoImage(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = modifier.size(size)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ana_care_app_logo_1787863935033),
                contentDescription = "ANA Care Official Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * Compact Header Variant for Top App Bar
 */
@Composable
fun AnaCareHeaderBrand(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = true
) {
    Row(
        modifier = modifier.testTag("ana_care_header_brand"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Official Logo Image Badge
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3338BDF8)),
            modifier = Modifier.size(36.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(3.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ana_care_app_logo_1787863935033),
                    contentDescription = "ANA Care Official Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ANA CARE",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (isDarkMode) Color.White else NavyPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "®",
                    fontFamily = FontFamily.Serif,
                    fontSize = 9.sp,
                    color = SkyLight,
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                )
            }
            Text(
                text = "Home Healthcare Services",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color(0xFF93C5FD) else Color(0xFF64748B)
            )
        }
    }
}
