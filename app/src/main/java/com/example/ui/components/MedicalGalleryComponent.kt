package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entities.MedicalGalleryEntity
import com.example.data.util.ImageStorageHelper
import com.example.ui.theme.HealthCriticalRed
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.PortalViewModel
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val GALLERY_CATEGORIES = listOf(
    "Prescription / Rx",
    "Lab & Diagnostic Report",
    "Wound & Clinical Photo",
    "Physical Therapy / Mobility",
    "Diet & Meal Photo",
    "Insurance & ID Card",
    "General Clinical Record"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MedicalGallerySection(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val galleryItems by viewModel.galleryList.collectAsState()
    val activeAccount by viewModel.activeAccount.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showCameraFallbackDialog by remember { mutableStateOf(false) }
    var pendingImageUri by remember { mutableStateOf<String?>(null) }
    var pendingImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var inputTitle by remember { mutableStateOf("") }
    var inputCategory by remember { mutableStateOf(GALLERY_CATEGORIES.first()) }
    var inputNotes by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var viewingItem by remember { mutableStateOf<MedicalGalleryEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<MedicalGalleryEntity?>(null) }
    var videoCallTargetItem by remember { mutableStateOf<MedicalGalleryEntity?>(null) }
    var showVideoCallDialog by remember { mutableStateOf(false) }

    // Launcher for Browsing Gallery / Files
    val browseLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val savedPath = ImageStorageHelper.saveUriToInternalStorage(context, uri, "browse_upload")
                pendingImageUri = savedPath.ifEmpty { uri.toString() }
                pendingImageBitmap = null
                inputTitle = "Medical Document / Image"
                inputCategory = "General Clinical Record"
                inputNotes = ""
                showAddDialog = true
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load selected image: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher for Taking Photo via Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            try {
                val savedPath = ImageStorageHelper.saveBitmapToInternalStorage(context, bitmap, "camera_photo")
                pendingImageUri = savedPath
                pendingImageBitmap = bitmap
                inputTitle = "Clinical Photo (${SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date())})"
                inputCategory = "Wound & Clinical Photo"
                inputNotes = ""
                showAddDialog = true
            } catch (e: Exception) {
                Toast.makeText(context, "Error saving captured photo: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Permission launcher for Camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: ActivityNotFoundException) {
                showCameraFallbackDialog = true
            } catch (e: Exception) {
                showCameraFallbackDialog = true
            }
        } else {
            Toast.makeText(context, "Camera permission is required to capture photos.", Toast.LENGTH_LONG).show()
        }
    }

    fun launchCameraSafely() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            try {
                cameraLauncher.launch(null)
            } catch (e: ActivityNotFoundException) {
                showCameraFallbackDialog = true
            } catch (e: Exception) {
                showCameraFallbackDialog = true
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("medical_gallery_section_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Role Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0F2FE),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Collections,
                                contentDescription = null,
                                tint = TealAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Activity & Clinical Gallery",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Upload photos & clinical records (All Roles)",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Active Role Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (activeAccount?.role) {
                        "DOCTOR", "MEDICAL_PROFESSIONAL" -> Color(0xFFEFF6FF)
                        "CAREGIVER" -> Color(0xFFFEF3C7)
                        "ADMIN" -> Color(0xFFF3E8FF)
                        else -> Color(0xFFE0F2FE)
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (activeAccount?.role) {
                            "DOCTOR", "MEDICAL_PROFESSIONAL" -> Color(0xFF93C5FD)
                            "CAREGIVER" -> Color(0xFFFCD34D)
                            "ADMIN" -> Color(0xFFD8B4FE)
                            else -> Color(0xFFBAE6FD)
                        }
                    )
                ) {
                    Text(
                        text = "Role: ${activeAccount?.role ?: "PATIENT"}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (activeAccount?.role) {
                            "DOCTOR", "MEDICAL_PROFESSIONAL" -> Color(0xFF1E40AF)
                            "CAREGIVER" -> Color(0xFF92400E)
                            "ADMIN" -> Color(0xFF6B21A8)
                            else -> NavyPrimary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-Party Video Consultation Action Banner
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        videoCallTargetItem = galleryItems.firstOrNull()
                        showVideoCallDialog = true
                    }
                    .testTag("gallery_start_videocall_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF9333EA).copy(alpha = 0.25f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Videocam,
                                    contentDescription = "Video Call",
                                    tint = Color(0xFFC084FC),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "3-Way Case Video Call",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = HealthNormalGreen.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "LIVE CONSULT",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNormalGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Linked Patient + Caretaker + Doctor Room",
                                fontSize = 11.sp,
                                color = SkyLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            videoCallTargetItem = galleryItems.firstOrNull()
                            showVideoCallDialog = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Join Call", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Browse & Camera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Browse / Files Button
                Button(
                    onClick = {
                        try {
                            browseLauncher.launch("image/*")
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to launch file selector: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("gallery_browse_button")
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = "Browse Gallery",
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Browse Files", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }

                // Camera Photo Button
                Button(
                    onClick = { launchCameraSafely() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("gallery_camera_button")
                ) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = "Camera",
                        modifier = Modifier.size(17.dp),
                        tint = NavyDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Take Photo", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Clinical Preset Stamp Buttons
            Text(
                text = "Quick Clinical Photo Stamps (Instant Demo / Simulation):",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickStampChip(
                    title = "💊 Rx Bottle Label",
                    category = "Prescription / Rx",
                    uriKey = "sample_rx_label",
                    notes = "Scanned prescription bottle label verifying daily dosage.",
                    onClick = { uri, cat, tit, nts ->
                        viewModel.recordGalleryImage(title = tit, category = cat, imageUri = uri, notes = nts)
                    }
                )
                QuickStampChip(
                    title = "🩹 Wound Healing",
                    category = "Wound & Clinical Photo",
                    uriKey = "sample_wound_healing",
                    notes = "Surgical site incision assessment - clean, dry, and healing well.",
                    onClick = { uri, cat, tit, nts ->
                        viewModel.recordGalleryImage(title = tit, category = cat, imageUri = uri, notes = nts)
                    }
                )
                QuickStampChip(
                    title = "🧪 Lab Panel Scan",
                    category = "Lab & Diagnostic Report",
                    uriKey = "sample_lab_scan",
                    notes = "Official diagnostic printout scanned for physician chart review.",
                    onClick = { uri, cat, tit, nts ->
                        viewModel.recordGalleryImage(title = tit, category = cat, imageUri = uri, notes = nts)
                    }
                )
                QuickStampChip(
                    title = "🏃 Mobility Form",
                    category = "Physical Therapy / Mobility",
                    uriKey = "sample_therapy_form",
                    notes = "Physical therapy resistance routine posture record.",
                    onClick = { uri, cat, tit, nts ->
                        viewModel.recordGalleryImage(title = tit, category = cat, imageUri = uri, notes = nts)
                    }
                )
                QuickStampChip(
                    title = "🥗 Meal Photo",
                    category = "Diet & Meal Photo",
                    uriKey = "sample_diet_meal",
                    notes = "Low-sodium diabetic breakfast meal log.",
                    onClick = { uri, cat, tit, nts ->
                        viewModel.recordGalleryImage(title = tit, category = cat, imageUri = uri, notes = nts)
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gallery Thumbnails List / Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Gallery Records (${galleryItems.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                if (galleryItems.isNotEmpty()) {
                    Text(
                        text = "Tap image to enlarge",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (galleryItems.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No images in gallery yet",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = "Use Browse or Camera above to attach photos to patient records.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(galleryItems, key = { it.id }) { item ->
                        GalleryThumbnailCard(
                            item = item,
                            onClick = { viewingItem = item },
                            onDelete = { itemToDelete = item }
                        )
                    }
                }
            }
        }
    }

    // Add Image / Confirm Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                pendingImageUri = null
                pendingImageBitmap = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save to Medical Gallery", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Image Preview in Dialog
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (pendingImageBitmap != null) {
                            Image(
                                bitmap = pendingImageBitmap!!.asImageBitmap(),
                                contentDescription = "Captured Photo Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (!pendingImageUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(File(pendingImageUri!!))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Selected Photo Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("Image / Record Title") },
                        placeholder = { Text("e.g. Prescription Label, Wound Check") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("gallery_input_title")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = inputCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("gallery_category_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            GALLERY_CATEGORIES.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category, fontSize = 13.sp) },
                                    onClick = {
                                        inputCategory = category
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputNotes,
                        onValueChange = { inputNotes = it },
                        label = { Text("Clinical Observation / Notes") },
                        placeholder = { Text("e.g. Taken post-therapy session.") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("gallery_input_notes")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Logged by: ${activeAccount?.name ?: "User"} (${activeAccount?.role ?: "PATIENT"})",
                                fontSize = 11.5.sp,
                                color = NavyPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uriToSave = pendingImageUri ?: ""
                        viewModel.recordGalleryImage(
                            title = inputTitle,
                            category = inputCategory,
                            imageUri = uriToSave,
                            notes = inputNotes
                        )
                        showAddDialog = false
                        pendingImageUri = null
                        pendingImageBitmap = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    modifier = Modifier.testTag("gallery_save_confirm_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save to Gallery")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        pendingImageUri = null
                        pendingImageBitmap = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Camera Fallback Dialog for Cloud/Emulator Environments without Camera App
    if (showCameraFallbackDialog) {
        AlertDialog(
            onDismissRequest = { showCameraFallbackDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Camera App Not Found", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "The virtual Android emulator does not have a default camera app or physical camera sensor attached.",
                        fontSize = 13.sp,
                        color = NavyDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "You can upload an image using the file browser or select a simulated clinical record.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCameraFallbackDialog = false
                        try {
                            browseLauncher.launch("image/*")
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open file browser", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Browse Files")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCameraFallbackDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // View Image Fullscreen Modal Dialog
    if (viewingItem != null) {
        val item = viewingItem!!
        Dialog(onDismissRequest = { viewingItem = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewingItem = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Big Image / Art Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                getCategoryGradient(item.category)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.imageUri.startsWith("/") || item.imageUri.startsWith("file://") || item.imageUri.startsWith("content://")) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(if (item.imageUri.startsWith("/")) File(item.imageUri) else item.imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = item.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            // High fidelity Category Art Canvas for Presets
                            ClinicalArtIllustration(
                                category = item.category,
                                title = item.title
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Meta Details
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getCategoryPillColor(item.category)
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = getCategoryTextColor(item.category),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "📅 Recorded: ${item.formattedDate.ifEmpty { SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp)) }}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "👤 Logged by: ${item.loggedByName} (${item.loggedByRole})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )

                    if (item.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Clinical Notes / Observation:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(item.notes, fontSize = 12.5.sp, color = NavyPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                videoCallTargetItem = item
                                viewingItem = null
                                showVideoCallDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("modal_discuss_in_video_call")
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Discuss in Video Call", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row {
                            OutlinedButton(
                                onClick = {
                                    viewingItem = null
                                    itemToDelete = item
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthCriticalRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", fontSize = 11.5.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = { viewingItem = null },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Close", fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Live 3-Way Case Video Consultation Dialog
    if (showVideoCallDialog) {
        GalleryCaseVideoCallDialog(
            initialGalleryItem = videoCallTargetItem,
            allGalleryItems = galleryItems,
            viewModel = viewModel,
            onDismiss = {
                showVideoCallDialog = false
                videoCallTargetItem = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (itemToDelete != null) {
        val item = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Gallery Image?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${item.title}' from the patient's medical gallery?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGalleryImage(item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthCriticalRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuickStampChip(
    title: String,
    category: String,
    uriKey: String,
    notes: String,
    onClick: (uri: String, category: String, title: String, notes: String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF1F5F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = Modifier.clickable { onClick(uriKey, category, title.replace("^[\\p{So}\\p{Sk}\\p{Sm}\\p{Sc}\\p{P}]+\\s*".toRegex(), ""), notes) }
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = NavyDark,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun GalleryThumbnailCard(
    item: MedicalGalleryEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .width(135.dp)
            .clickable { onClick() }
            .testTag("gallery_item_${item.id}")
    ) {
        Column {
            // Thumbnail Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(getCategoryGradient(item.category)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUri.startsWith("/") || item.imageUri.startsWith("file://") || item.imageUri.startsWith("content://")) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(if (item.imageUri.startsWith("/")) File(item.imageUri) else item.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    ClinicalArtIllustration(
                        category = item.category,
                        title = item.title,
                        isCompact = true
                    )
                }

                // Zoom Icon Overlay
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.ZoomIn,
                            contentDescription = "Zoom",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = item.title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = getCategoryPillColor(item.category)
                ) {
                    Text(
                        text = item.category.split(" ").first(),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = getCategoryTextColor(item.category),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "By ${item.loggedByRole}",
                    fontSize = 9.5.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun ClinicalArtIllustration(
    category: String,
    title: String,
    isCompact: Boolean = false
) {
    val icon = when {
        category.contains("Rx") || category.contains("Prescription") -> Icons.Default.Medication
        category.contains("Wound") -> Icons.Default.Healing
        category.contains("Lab") -> Icons.Default.Science
        category.contains("Therapy") -> Icons.Default.FitnessCenter
        category.contains("Diet") -> Icons.Default.Restaurant
        else -> Icons.Default.Description
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(if (isCompact) 28.dp else 48.dp)
        )
        if (!isCompact) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Clinical Verification Record",
                fontSize = 11.sp,
                color = SkyLight,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getCategoryGradient(category: String): Brush {
    return when {
        category.contains("Rx") || category.contains("Prescription") -> Brush.linearGradient(
            listOf(Color(0xFF0284C7), Color(0xFF0369A1))
        )
        category.contains("Wound") -> Brush.linearGradient(
            listOf(Color(0xFFE11D48), Color(0xFF9F1239))
        )
        category.contains("Lab") -> Brush.linearGradient(
            listOf(Color(0xFF7C3AED), Color(0xFF5B21B6))
        )
        category.contains("Therapy") -> Brush.linearGradient(
            listOf(Color(0xFF0D9488), Color(0xFF0F766E))
        )
        category.contains("Diet") -> Brush.linearGradient(
            listOf(Color(0xFFD97706), Color(0xFFB45309))
        )
        else -> Brush.linearGradient(
            listOf(NavyPrimary, NavyDark)
        )
    }
}

private fun getCategoryPillColor(category: String): Color {
    return when {
        category.contains("Rx") || category.contains("Prescription") -> Color(0xFFE0F2FE)
        category.contains("Wound") -> Color(0xFFFEE2E2)
        category.contains("Lab") -> Color(0xFFEDE9FE)
        category.contains("Therapy") -> Color(0xFFCCFBF1)
        category.contains("Diet") -> Color(0xFFFEF3C7)
        else -> Color(0xFFF1F5F9)
    }
}

private fun getCategoryTextColor(category: String): Color {
    return when {
        category.contains("Rx") || category.contains("Prescription") -> Color(0xFF0369A1)
        category.contains("Wound") -> Color(0xFFBE123C)
        category.contains("Lab") -> Color(0xFF6D28D9)
        category.contains("Therapy") -> Color(0xFF0F766E)
        category.contains("Diet") -> Color(0xFF92400E)
        else -> NavyPrimary
    }
}
