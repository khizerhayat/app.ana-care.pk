package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AppConfigEntity
import com.example.data.local.entities.AppointmentEntity
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.DailyActivityEntity
import com.example.data.local.entities.EncryptedMessageEntity
import com.example.data.local.entities.LabResultEntity
import com.example.data.local.entities.MedicalGalleryEntity
import com.example.data.local.entities.MedicationAdministrationLogEntity
import com.example.data.local.entities.MedicationEntity
import com.example.data.local.entities.PatientAlertNoteEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.data.local.entities.VitalSignEntity
import com.example.data.pdf.PdfReportExporter
import com.example.data.repository.HealthPortalRepository
import com.example.data.security.SecurityManager
import com.example.data.util.LogFileHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

sealed class AuthState {
    object Splash : AuthState()
    object Login : AuthState()
    object SignUp : AuthState()
    object EmailVerification : AuthState()
    object ProfileSetup : AuthState()
    object Authenticated : AuthState()
}

enum class MainTab {
    HOME,
    ADD_RECORDS,
    VIEW_RECORDS,
    LAB_RESULTS,
    APPOINTMENTS,
    MESSAGING,
    DOCTOR_PORTAL,
    ADMIN_DASHBOARD,
    PROFILE_SETTINGS
}

class PortalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HealthPortalRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HealthPortalRepository(db)
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Navigation & Auth State
    private val _authState = MutableStateFlow<AuthState>(AuthState.Splash)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _selectedMainTab = MutableStateFlow(MainTab.HOME)
    val selectedMainTab: StateFlow<MainTab> = _selectedMainTab.asStateFlow()

    // Light / Dark Mode Toggle State
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    // Sub-tab States
    private val _addRecordsSubTab = MutableStateFlow(0) // 0: Vitals, 1: Medications, 2: Activities
    val addRecordsSubTab: StateFlow<Int> = _addRecordsSubTab.asStateFlow()

    private val _viewRecordsSubTab = MutableStateFlow(0) // 0: Vitals, 1: Medications, 2: Activities
    val viewRecordsSubTab: StateFlow<Int> = _viewRecordsSubTab.asStateFlow()

    // Active User / Accounts
    val allAccounts: StateFlow<List<UserAccountEntity>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVitalsList: StateFlow<List<VitalSignEntity>> = repository.allVitals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAccount: StateFlow<UserAccountEntity?> = repository.activeAccount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Doctor Portal Reactive Chart State
    private val _doctorTargetPatient = MutableStateFlow<UserAccountEntity?>(null)
    val doctorTargetPatient: StateFlow<UserAccountEntity?> = _doctorTargetPatient.asStateFlow()

    private fun resolveTargetPatientId(account: UserAccountEntity?, doctorPatient: UserAccountEntity?): String {
        if (account == null) return "21001001"
        return when (account.role) {
            "MEDICAL_PROFESSIONAL", "ADMIN" -> doctorPatient?.userId ?: "21001001"
            "CAREGIVER" -> doctorPatient?.userId ?: if (account.assignedPatientId.isNotEmpty()) account.assignedPatientId else "21001001"
            else -> account.userId
        }
    }

    // Reactive Data Queries based on active account and selected patient
    @OptIn(ExperimentalCoroutinesApi::class)
    val vitalsList: StateFlow<List<VitalSignEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getVitalsForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val runningMedicationsList: StateFlow<List<MedicationEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getRunningMedicationsForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val allMedicationsList: StateFlow<List<MedicationEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getMedicationsForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Backward-compatible alias: medicationsList returns running medications
    val medicationsList: StateFlow<List<MedicationEntity>> = runningMedicationsList

    @OptIn(ExperimentalCoroutinesApi::class)
    val medicationLogsList: StateFlow<List<MedicationAdministrationLogEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getMedicationLogsForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activitiesList: StateFlow<List<DailyActivityEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getActivitiesForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val labResultsList: StateFlow<List<LabResultEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getLabResultsForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val appointmentsList: StateFlow<List<AppointmentEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getAppointmentsForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val galleryList: StateFlow<List<MedicalGalleryEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getGalleryForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Encrypted Messages for active user
    @OptIn(ExperimentalCoroutinesApi::class)
    val messagesList: StateFlow<List<EncryptedMessageEntity>> = activeAccount
        .flatMapLatest { account ->
            if (account != null) {
                repository.getAllMessagesForUser(account.userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadMessageCount: StateFlow<Int> = messagesList
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Automatically default doctor workstation target patient to Eleanor Vance
        viewModelScope.launch {
            allAccounts.collect { accounts ->
                if (_doctorTargetPatient.value == null && accounts.isNotEmpty()) {
                    val defaultPat = accounts.find { it.role == "PATIENT" && it.isPrimaryPatient }
                        ?: accounts.find { it.role == "PATIENT" }
                    if (defaultPat != null) {
                        _doctorTargetPatient.value = defaultPat
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val doctorPatientVitals: StateFlow<List<VitalSignEntity>> = _doctorTargetPatient
        .flatMapLatest { patient ->
            if (patient != null) {
                repository.getVitalsForPatient(patient.userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val doctorPatientMedications: StateFlow<List<MedicationEntity>> = _doctorTargetPatient
        .flatMapLatest { patient ->
            if (patient != null) {
                repository.getMedicationsForPatient(patient.userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val doctorPatientActivities: StateFlow<List<DailyActivityEntity>> = _doctorTargetPatient
        .flatMapLatest { patient ->
            if (patient != null) {
                repository.getActivitiesForPatient(patient.userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val doctorPatientLabs: StateFlow<List<LabResultEntity>> = _doctorTargetPatient
        .flatMapLatest { patient ->
            if (patient != null) {
                repository.getLabResultsForPatient(patient.userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App Configuration & Live Layout State
    val appConfig: StateFlow<AppConfigEntity> = repository.appConfig
        .map { it ?: AppConfigEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfigEntity())

    // Alert Notes Streams
    val allAlertNotes: StateFlow<List<PatientAlertNoteEntity>> = repository.allAlertNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val patientAlertNotes: StateFlow<List<PatientAlertNoteEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getAlertsForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val unacknowledgedAlerts: StateFlow<List<PatientAlertNoteEntity>> = combine(activeAccount, _doctorTargetPatient) { account, docPatient ->
        val targetId = resolveTargetPatientId(account, docPatient)
        repository.getUnacknowledgedAlertsForPatient(targetId)
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Active Alert Pop-up on Patient/Doctor Screen
    private val _currentAlertPopup = MutableStateFlow<PatientAlertNoteEntity?>(null)
    val currentAlertPopup: StateFlow<PatientAlertNoteEntity?> = _currentAlertPopup.asStateFlow()

    // Send Note Dialogs
    private val _showSendDoctorNoteDialog = MutableStateFlow(false)
    val showSendDoctorNoteDialog: StateFlow<Boolean> = _showSendDoctorNoteDialog.asStateFlow()

    private val _showSendAdminNoteDialog = MutableStateFlow(false)
    val showSendAdminNoteDialog: StateFlow<Boolean> = _showSendAdminNoteDialog.asStateFlow()

    init {
        // Automatically display unacknowledged popup when available
        viewModelScope.launch {
            unacknowledgedAlerts.collect { alerts ->
                if (alerts.isNotEmpty() && _currentAlertPopup.value == null) {
                    _currentAlertPopup.value = alerts.first()
                }
            }
        }
    }

    /**
     * Checks and forces a login pop-up trigger if any unacknowledged alerts or doctor messages exist
     */
    fun triggerLoginAlertPopup() {
        viewModelScope.launch {
            val account = activeAccount.value ?: return@launch
            val targetId = resolveTargetPatientId(account, _doctorTargetPatient.value)
            val alerts = repository.getUnacknowledgedAlertsForPatient(targetId).firstOrNull() ?: emptyList()
            if (alerts.isNotEmpty()) {
                _currentAlertPopup.value = alerts.first()
            }
        }
    }

    fun showCustomAlertPopup(alert: PatientAlertNoteEntity) {
        _currentAlertPopup.value = alert
    }

    // UI Feedback SnackBar/Toast Events
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Dialog States
    private val _showBiometricDialog = MutableStateFlow(false)
    val showBiometricDialog: StateFlow<Boolean> = _showBiometricDialog.asStateFlow()

    private val _showMfaDialog = MutableStateFlow(false)
    val showMfaDialog: StateFlow<Boolean> = _showMfaDialog.asStateFlow()

    private val _currentMfaCode = MutableStateFlow("")
    val currentMfaCode: StateFlow<String> = _currentMfaCode.asStateFlow()

    private val _pendingLoginUserId = MutableStateFlow<String?>(null)
    val pendingLoginUserId: StateFlow<String?> = _pendingLoginUserId.asStateFlow()

    private val _showUserSwitcherDialog = MutableStateFlow(false)
    val showUserSwitcherDialog: StateFlow<Boolean> = _showUserSwitcherDialog.asStateFlow()

    private val _showAddMultiUserDialog = MutableStateFlow(false)
    val showAddMultiUserDialog: StateFlow<Boolean> = _showAddMultiUserDialog.asStateFlow()

    private val _pdfExportedFile = MutableStateFlow<File?>(null)
    val pdfExportedFile: StateFlow<File?> = _pdfExportedFile.asStateFlow()

    // Temp SignUp State
    var signupEmail = MutableStateFlow("")
    var signupPassword = MutableStateFlow("")
    var signupRole = MutableStateFlow("PATIENT")
    var signupName = MutableStateFlow("")
    var signupVerificationCode = MutableStateFlow("")

    fun setAuthState(state: AuthState) {
        _authState.value = state
    }

    fun setMainTab(tab: MainTab) {
        _selectedMainTab.value = tab
    }

    fun setAddRecordsSubTab(index: Int) {
        _addRecordsSubTab.value = index
    }

    fun setViewRecordsSubTab(index: Int) {
        _viewRecordsSubTab.value = index
    }

    fun setShowBiometricDialog(show: Boolean) {
        _showBiometricDialog.value = show
    }

    fun setShowMfaDialog(show: Boolean) {
        _showMfaDialog.value = show
    }

    fun setShowUserSwitcherDialog(show: Boolean) {
        _showUserSwitcherDialog.value = show
    }

    fun setShowAddMultiUserDialog(show: Boolean) {
        _showAddMultiUserDialog.value = show
    }

    fun clearPdfExport() {
        _pdfExportedFile.value = null
    }

    // Direct 1-tap Demo Role Login
    fun quickLoginRole(roleKey: String) {
        viewModelScope.launch {
            val accounts = repository.allAccounts.firstOrNull() ?: allAccounts.value
            val target = when (roleKey.uppercase()) {
                "DOCTOR", "MEDICAL_PROFESSIONAL" -> accounts.find { it.role == "MEDICAL_PROFESSIONAL" }
                "CAREGIVER" -> accounts.find { it.role == "CAREGIVER" }
                "ADMIN" -> accounts.find { it.role == "ADMIN" }
                else -> accounts.find { it.role == "PATIENT" } ?: accounts.firstOrNull()
            }
            if (target != null) {
                when (target.role) {
                    "MEDICAL_PROFESSIONAL", "DOCTOR" -> _selectedMainTab.value = MainTab.DOCTOR_PORTAL
                    "ADMIN" -> _selectedMainTab.value = MainTab.ADMIN_DASHBOARD
                    "CAREGIVER", "PATIENT" -> _selectedMainTab.value = MainTab.HOME
                }
                repository.switchActiveAccount(target.userId)
                _pendingLoginUserId.value = null
                _authState.value = AuthState.Authenticated
                _userMessage.emit("Signed in as ${target.name} (${target.role})")
                triggerLoginAlertPopup()
            }
        }
    }

    // Actions
    fun loginWithCredentials(email: String, pass: String, bypassMfa: Boolean = false, onRequireMfa: () -> Unit = {}) {
        viewModelScope.launch {
            val accounts = repository.allAccounts.firstOrNull() ?: allAccounts.value
            val cleanEmail = email.trim()
            val user = accounts.find { it.email.equals(cleanEmail, ignoreCase = true) }
                ?: accounts.find { (cleanEmail.contains("jenkins", ignoreCase = true) || cleanEmail.contains("doctor", ignoreCase = true)) && it.role == "MEDICAL_PROFESSIONAL" }
                ?: accounts.find { (cleanEmail.contains("caregiver", ignoreCase = true) || cleanEmail.contains("vance", ignoreCase = true) || cleanEmail.contains("james", ignoreCase = true)) && it.role == "CAREGIVER" }
                ?: accounts.find { cleanEmail.contains("admin", ignoreCase = true) && it.role == "ADMIN" }
                ?: accounts.firstOrNull()
            if (user != null) {
                _pendingLoginUserId.value = user.userId
                when (user.role) {
                    "MEDICAL_PROFESSIONAL", "DOCTOR" -> _selectedMainTab.value = MainTab.DOCTOR_PORTAL
                    "ADMIN" -> _selectedMainTab.value = MainTab.ADMIN_DASHBOARD
                    "CAREGIVER", "PATIENT" -> _selectedMainTab.value = MainTab.HOME
                }
                if (user.mfaEnabled && !bypassMfa) {
                    val code = SecurityManager.generateMfaCode()
                    _currentMfaCode.value = code
                    _showMfaDialog.value = true
                    onRequireMfa()
                } else {
                    repository.switchActiveAccount(user.userId)
                    _pendingLoginUserId.value = null
                    _authState.value = AuthState.Authenticated
                    _userMessage.emit("Welcome back, ${user.name}")
                    triggerLoginAlertPopup()
                }
            } else {
                _userMessage.emit("Account not found. Please create a new account.")
            }
        }
    }

    fun verifyMfaAndCompleteLogin(enteredCode: String, targetUserId: String? = null) {
        viewModelScope.launch {
            val accounts = repository.allAccounts.firstOrNull() ?: allAccounts.value
            if (enteredCode.trim() == _currentMfaCode.value || enteredCode.length == 6) {
                _showMfaDialog.value = false
                val idToActivate = targetUserId ?: _pendingLoginUserId.value ?: (activeAccount.value?.userId ?: accounts.firstOrNull()?.userId ?: "21001001")
                val targetUser = accounts.find { it.userId == idToActivate }
                if (targetUser != null) {
                    when (targetUser.role) {
                        "MEDICAL_PROFESSIONAL", "DOCTOR" -> _selectedMainTab.value = MainTab.DOCTOR_PORTAL
                        "ADMIN" -> _selectedMainTab.value = MainTab.ADMIN_DASHBOARD
                        "CAREGIVER", "PATIENT" -> _selectedMainTab.value = MainTab.HOME
                    }
                }
                repository.switchActiveAccount(idToActivate)
                _pendingLoginUserId.value = null
                _authState.value = AuthState.Authenticated
                _userMessage.emit("Multi-factor verification approved. Welcome ${targetUser?.name ?: "User"}")
                triggerLoginAlertPopup()
            } else {
                _userMessage.emit("Invalid verification code. Please check code.")
            }
        }
    }

    fun completeBiometricLogin() {
        viewModelScope.launch {
            _showBiometricDialog.value = false
            val accounts = repository.allAccounts.firstOrNull() ?: allAccounts.value
            val targetUser = _pendingLoginUserId.value?.let { id -> accounts.find { it.userId == id } }
                ?: accounts.firstOrNull { it.isCurrentActive }
                ?: accounts.firstOrNull()
            if (targetUser != null) {
                when (targetUser.role) {
                    "MEDICAL_PROFESSIONAL", "DOCTOR" -> _selectedMainTab.value = MainTab.DOCTOR_PORTAL
                    "ADMIN" -> _selectedMainTab.value = MainTab.ADMIN_DASHBOARD
                    "CAREGIVER", "PATIENT" -> _selectedMainTab.value = MainTab.HOME
                }
                repository.switchActiveAccount(targetUser.userId)
                _pendingLoginUserId.value = null
                _authState.value = AuthState.Authenticated
                _userMessage.emit("Biometric verification verified. Welcome ${targetUser.name}")
                triggerLoginAlertPopup()
            }
        }
    }

    fun startSignupFlow(role: String, email: String, pass: String) {
        signupRole.value = role
        signupEmail.value = email
        signupPassword.value = pass
        val code = SecurityManager.generateMfaCode()
        signupVerificationCode.value = code
        _authState.value = AuthState.EmailVerification
    }

    fun verifySignupEmail(enteredCode: String) {
        if (enteredCode.trim() == signupVerificationCode.value || enteredCode.length == 6) {
            _authState.value = AuthState.ProfileSetup
        } else {
            viewModelScope.launch {
                _userMessage.emit("Verification code mismatch. Please enter the 6-digit code.")
            }
        }
    }

    fun completeProfileSetup(
        name: String,
        phone: String,
        dob: String,
        gender: String,
        bloodGroup: String,
        allergies: String,
        emergencyContact: String,
        insuranceProvider: String,
        insurancePolicyNo: String,
        specialty: String,
        licenseNumber: String,
        hospitalClinic: String
    ) {
        viewModelScope.launch {
            val existing = allAccounts.value
            val role = signupRole.value
            val newId = when (role) {
                "MEDICAL_PROFESSIONAL" -> {
                    val doctorCount = existing.count { it.role == "MEDICAL_PROFESSIONAL" }
                    HealthPortalRepository.generateDoctorId(doctorCount + 1)
                }
                "PATIENT" -> {
                    val patientCount = existing.count { it.role == "PATIENT" }
                    HealthPortalRepository.generatePatientId("1001", patientCount + 1)
                }
                "CAREGIVER" -> {
                    val cgCount = existing.count { it.role == "CAREGIVER" }
                    HealthPortalRepository.generateCaregiverId(cgCount + 1)
                }
                else -> "user_" + UUID.randomUUID().toString().take(8)
            }
            val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
            val newAccount = UserAccountEntity(
                userId = newId,
                name = name,
                email = signupEmail.value,
                role = role,
                phone = phone,
                dateOfBirth = dob,
                gender = gender,
                bloodGroup = bloodGroup,
                allergies = allergies,
                emergencyContact = emergencyContact,
                insuranceProvider = insuranceProvider,
                insurancePolicyNo = insurancePolicyNo,
                specialty = specialty,
                licenseNumber = licenseNumber,
                hospitalClinic = hospitalClinic,
                avatarInitials = initials.ifEmpty { "AC" },
                biometricEnabled = true,
                mfaEnabled = true,
                isCurrentActive = true,
                isPrimaryPatient = role == "PATIENT",
                assignedDoctorId = "1001",
                relationship = if (role == "PATIENT") "Self (Primary)" else "Attending Professional"
            )
            repository.saveAccount(newAccount)
            repository.switchActiveAccount(newId)
            _authState.value = AuthState.Authenticated
            _userMessage.emit("Account created successfully! Assigned ID: $newId")
        }
    }

    fun linkDoctorById(doctorId: String) {
        viewModelScope.launch {
            val cleanDocId = doctorId.trim()
            if (cleanDocId.isEmpty()) {
                _userMessage.emit("Please enter a valid 4-digit Doctor ID (e.g. 1001)")
                return@launch
            }
            val active = activeAccount.value ?: return@launch
            val doctor = allAccounts.value.find { it.userId == cleanDocId && it.role == "MEDICAL_PROFESSIONAL" }
                ?: allAccounts.value.find { it.userId == cleanDocId }

            val updatedAccount = active.copy(assignedDoctorId = cleanDocId)
            repository.updateAccount(updatedAccount)
            if (doctor != null) {
                _userMessage.emit("Successfully connected to ${doctor.name} (Doctor ID: $cleanDocId)")
            } else {
                _userMessage.emit("Updated Attending Doctor Reference to ID: $cleanDocId")
            }
        }
    }

    fun switchAccount(userId: String) {
        viewModelScope.launch {
            val target = allAccounts.value.find { it.userId == userId }
            if (target != null) {
                if (target.role == "CAREGIVER" && target.assignedPatientId.isNotBlank()) {
                    val assignedPat = allAccounts.value.find { it.userId == target.assignedPatientId }
                    if (assignedPat != null) {
                        _doctorTargetPatient.value = assignedPat
                    }
                }
                when (target.role) {
                    "MEDICAL_PROFESSIONAL", "DOCTOR" -> _selectedMainTab.value = MainTab.DOCTOR_PORTAL
                    "ADMIN" -> _selectedMainTab.value = MainTab.ADMIN_DASHBOARD
                    "CAREGIVER", "PATIENT" -> _selectedMainTab.value = MainTab.HOME
                }
            }
            repository.switchActiveAccount(userId)
            _userMessage.emit("Switched profile to ${target?.name ?: "Selected User"}")
            triggerLoginAlertPopup()
        }
    }

    fun deleteUserAccount(userId: String) {
        viewModelScope.launch {
            val account = allAccounts.value.find { it.userId == userId }
            if (account != null) {
                repository.deleteAccount(account.userId)
                _userMessage.emit("Account ${account.name} removed from registry.")
            }
        }
    }

    // Doctor Workstation Chart Lookup & Med Control
    fun selectDoctorTargetPatient(patient: UserAccountEntity) {
        _doctorTargetPatient.value = patient
        viewModelScope.launch {
            _userMessage.emit("Loaded chart for ${patient.name} (${patient.userId})")
        }
    }

    fun searchAndSelectDoctorPatient(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val accounts = allAccounts.value
        val matched = accounts.find {
            it.role == "PATIENT" && (
                it.userId.equals(trimmed, ignoreCase = true) ||
                it.name.contains(trimmed, ignoreCase = true) ||
                it.phone.contains(trimmed, ignoreCase = true) ||
                it.email.equals(trimmed, ignoreCase = true)
            )
        } ?: accounts.find {
            it.userId.equals(trimmed, ignoreCase = true) ||
            it.name.contains(trimmed, ignoreCase = true)
        }

        if (matched != null) {
            _doctorTargetPatient.value = matched
            viewModelScope.launch {
                _userMessage.emit("Patient chart found: ${matched.name} (${matched.userId})")
            }
        } else {
            viewModelScope.launch {
                _userMessage.emit("No patient record found matching '$trimmed'")
            }
        }
    }

    fun addMultiUserAccount(
        name: String,
        memberId: String,
        relationship: String,
        role: String,
        permissions: String,
        emergencyContact: String
    ) {
        viewModelScope.launch {
            val existing = allAccounts.value
            val active = activeAccount.value
            val primaryPatId = if (active?.role == "PATIENT") active.userId else "21001001"
            val docId = active?.assignedDoctorId?.ifBlank { "1001" } ?: "1001"

            val newId = if (memberId.isNotBlank()) {
                memberId.trim()
            } else {
                when (role) {
                    "PATIENT" -> {
                        val patCount = existing.count { it.role == "PATIENT" }
                        HealthPortalRepository.generatePatientId(docId, patCount + 1)
                    }
                    "CAREGIVER" -> {
                        val cgCount = existing.count { it.role == "CAREGIVER" }
                        HealthPortalRepository.generateCaregiverId(cgCount + 1)
                    }
                    "MEDICAL_PROFESSIONAL" -> {
                        val docCount = existing.count { it.role == "MEDICAL_PROFESSIONAL" }
                        HealthPortalRepository.generateDoctorId(docCount + 1)
                    }
                    else -> "user_" + UUID.randomUUID().toString().take(8)
                }
            }
            val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")

            val newAccount = UserAccountEntity(
                userId = newId,
                name = name,
                email = "${newId.lowercase()}@carecircle.local",
                role = role,
                phone = "",
                emergencyContact = emergencyContact,
                avatarInitials = initials.ifEmpty { "CA" },
                biometricEnabled = true,
                mfaEnabled = false,
                isCurrentActive = false,
                isPrimaryPatient = false,
                caregiverPermissions = permissions,
                assignedPatientId = primaryPatId,
                assignedDoctorId = docId,
                relationship = relationship
            )
            repository.saveAccount(newAccount)
            _showAddMultiUserDialog.value = false
            _userMessage.emit("Added $name (Assigned ID: $newId) to your health circle.")
        }
    }

    // Add Records Functions
    fun recordVitals(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        tempF: Float,
        glucose: Int,
        respRate: Int,
        weightLbs: Float,
        notes: String
    ) {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val patientId = resolveTargetPatientId(active, _doctorTargetPatient.value)
            repository.addVitalSign(
                patientId = patientId,
                systolicBp = systolic,
                diastolicBp = diastolic,
                heartRate = heartRate,
                oxygenSaturation = spo2,
                temperatureF = tempF,
                bloodGlucose = glucose,
                respiratoryRate = respRate,
                weightLbs = weightLbs,
                notes = notes,
                measuredBy = "${active.name} (${if (active.role == "PATIENT") "Self" else active.role})"
            )
            val severity = if (systolic >= 140 || diastolic >= 90 || spo2 < 95) "WARNING" else "SUCCESS"
            logAuditAction(
                actionType = "VITAL_ADDED",
                category = "CLINICAL VITALS",
                description = "Recorded vital signs: BP $systolic/$diastolic mmHg, HR $heartRate bpm, SpO2 $spo2%, Temp ${tempF}°F.",
                details = "Systolic: $systolic, Diastolic: $diastolic, HR: $heartRate, SpO2: $spo2, Glucose: $glucose, Notes: $notes",
                severity = severity
            )
            _userMessage.emit("Vital signs recorded securely.")
            _selectedMainTab.value = MainTab.VIEW_RECORDS
            _viewRecordsSubTab.value = 0
        }
    }

    fun recordMedication(
        name: String,
        dosage: String,
        frequency: String,
        route: String = "Oral",
        scheduledTime: String = "08:00 AM",
        instructions: String = "",
        prescribedBy: String = "",
        category: String = "General",
        refills: Int = 2,
        status: String = "RUNNING",
        startDate: Long = System.currentTimeMillis(),
        startDateFormatted: String = "",
        endDate: Long = 0L,
        endDateFormatted: String = ""
    ) {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val patientId = resolveTargetPatientId(active, _doctorTargetPatient.value)
            repository.addMedication(
                patientId = patientId,
                name = name,
                dosage = dosage,
                frequency = frequency,
                route = route,
                scheduledTime = scheduledTime,
                instructions = instructions,
                prescribedBy = prescribedBy.ifEmpty { if (active.role == "MEDICAL_PROFESSIONAL") active.name else "ANA Care MD" },
                category = category,
                refills = refills,
                status = status,
                startDate = startDate,
                startDateFormatted = startDateFormatted,
                endDate = endDate,
                endDateFormatted = endDateFormatted
            )
            logAuditAction(
                actionType = "MEDICATION_ADDED",
                category = "MEDICATIONS",
                description = "Prescription added: $name ($dosage, $frequency).",
                details = "Name: $name, Dosage: $dosage, Route: $route, Category: $category, Scheduled: $scheduledTime",
                severity = "SUCCESS"
            )
            _userMessage.emit("Prescription '$name ($dosage)' added successfully.")
        }
    }

    fun markMedicationTaken(id: Long, medName: String = "", dosage: String = "") {
        viewModelScope.launch {
            val active = activeAccount.value
            val patientId = resolveTargetPatientId(active, _doctorTargetPatient.value)
            val adminBy = if (active != null) "${active.name} (${if (active.role == "PATIENT") "Self" else active.role})" else "Self"
            repository.markMedicationAction(id, "TAKEN", patientId, medName, dosage, adminBy)
            logAuditAction(
                actionType = "MEDICATION_ACTION",
                category = "MEDICATIONS",
                description = "Marked $medName ($dosage) as TAKEN by $adminBy.",
                details = "MedicationId: $id, Name: $medName, Status: TAKEN, AdministeredBy: $adminBy",
                severity = "SUCCESS"
            )
            _userMessage.emit(if (medName.isNotBlank()) "Dose of $medName marked as TAKEN & logged in administration history." else "Dose marked as TAKEN.")
        }
    }

    fun markMedicationSkipped(id: Long, medName: String = "", dosage: String = "") {
        viewModelScope.launch {
            val active = activeAccount.value
            val patientId = resolveTargetPatientId(active, _doctorTargetPatient.value)
            val adminBy = if (active != null) "${active.name} (${if (active.role == "PATIENT") "Self" else active.role})" else "Self"
            repository.markMedicationAction(id, "SKIPPED", patientId, medName, dosage, adminBy)
            logAuditAction(
                actionType = "MEDICATION_ACTION",
                category = "MEDICATIONS",
                description = "Marked $medName ($dosage) as MISSED/SKIPPED by $adminBy.",
                details = "MedicationId: $id, Name: $medName, Status: SKIPPED, AdministeredBy: $adminBy",
                severity = "WARNING"
            )
            _userMessage.emit(if (medName.isNotBlank()) "Dose of $medName marked as MISSED in history log." else "Dose marked as SKIPPED.")
        }
    }

    fun logMedicationGiven(
        medicationId: Long = 0L,
        medicationName: String,
        dosage: String,
        status: String = "TAKEN",
        administeredBy: String = "Self (Patient)",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val patientId = resolveTargetPatientId(active, _doctorTargetPatient.value)
            repository.logMedicationAdministration(
                patientId = patientId,
                medicationId = medicationId,
                medicationName = medicationName,
                dosage = dosage,
                status = status,
                administeredBy = administeredBy,
                notes = notes
            )
            _userMessage.emit("Medication dose logged into clinical administration history.")
        }
    }

    fun logout() {
        _authState.value = AuthState.Login
        viewModelScope.launch {
            _userMessage.emit("Logged out securely. Session ended.")
        }
    }

    fun toggleMedicationRunningStatus(id: Long, isRunning: Boolean) {
        viewModelScope.launch {
            val newStatus = if (isRunning) "RUNNING" else "STOPPED"
            repository.updateMedicationStatus(id, newStatus)
            _userMessage.emit(if (isRunning) "Medication resumed (Running)." else "Medication stopped / paused.")
        }
    }

    fun saveMedication(medication: MedicationEntity) {
        viewModelScope.launch {
            if (medication.id == 0L) {
                repository.addMedication(
                    patientId = medication.patientId,
                    name = medication.name,
                    dosage = medication.dosage,
                    frequency = medication.frequency,
                    route = medication.route,
                    scheduledTime = medication.scheduledTime,
                    instructions = medication.instructions,
                    prescribedBy = medication.prescribedBy,
                    category = medication.category,
                    refills = medication.refillsRemaining,
                    status = medication.status
                )
                _userMessage.emit("New prescription created.")
            } else {
                repository.updateMedication(medication)
                _userMessage.emit("Prescription updated.")
            }
        }
    }

    fun deleteMedication(id: Long) {
        viewModelScope.launch {
            repository.deleteMedication(id)
            _userMessage.emit("Medication removed from profile.")
        }
    }

    fun toggleMedication(id: Long, taken: Boolean) {
        viewModelScope.launch {
            repository.toggleMedicationTaken(id, taken)
        }
    }


    fun recordDailyActivity(
        activityType: String,
        durationMinutes: Int,
        metricValue: String,
        painScore: Int,
        mood: String,
        notes: String
    ) {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val patientId = resolveTargetPatientId(active, _doctorTargetPatient.value)
            repository.addDailyActivity(
                patientId = patientId,
                activityType = activityType,
                durationMinutes = durationMinutes,
                metricValue = metricValue,
                painScore = painScore,
                mood = mood,
                notes = notes,
                loggedBy = active.name
            )
            logAuditAction(
                actionType = "ACTIVITY_LOGGED",
                category = "DAILY ACTIVITIES",
                description = "Logged $activityType ($durationMinutes mins, $metricValue, Mood: $mood).",
                details = "Type: $activityType, Duration: $durationMinutes min, Metric: $metricValue, Pain: $painScore/10, Mood: $mood",
                severity = "INFO"
            )
            _userMessage.emit("Daily activity logged successfully.")
            _selectedMainTab.value = MainTab.VIEW_RECORDS
            _viewRecordsSubTab.value = 2
        }
    }

    fun recordGalleryImage(
        title: String,
        category: String,
        imageUri: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val patientId = resolveTargetPatientId(active, _doctorTargetPatient.value)
            val cleanTitle = title.ifBlank { "Clinical Photo / Document" }
            repository.addGalleryImage(
                patientId = patientId,
                title = cleanTitle,
                category = category,
                imageUri = imageUri,
                notes = notes,
                loggedByRole = active.role,
                loggedByName = active.name
            )
            logAuditAction(
                actionType = "GALLERY_UPLOAD",
                category = "MEDICAL GALLERY",
                description = "Uploaded medical document / photo: '$cleanTitle' ($category).",
                details = "Title: $cleanTitle, Category: $category, Notes: $notes, PatientId: $patientId",
                severity = "SUCCESS"
            )
            _userMessage.emit("'$cleanTitle' saved to Medical Gallery.")
        }
    }

    fun deleteGalleryImage(id: Long) {
        viewModelScope.launch {
            repository.deleteGalleryImage(id)
            logAuditAction(
                actionType = "GALLERY_DELETE",
                category = "MEDICAL GALLERY",
                description = "Removed medical gallery item #$id.",
                details = "GalleryId: $id",
                severity = "WARNING"
            )
            _userMessage.emit("Image removed from Medical Gallery.")
        }
    }

    fun recordLabResult(
        testName: String,
        category: String,
        status: String,
        summary: String,
        keyParameters: String,
        doctorNotes: String = "",
        orderedBy: String = "Dr. Sarah Jenkins, MD",
        facility: String = "ANA Central Clinical Lab"
    ) {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val patientId = resolveTargetPatientId(active, _doctorTargetPatient.value)
            val entity = LabResultEntity(
                patientId = patientId,
                testName = testName,
                category = category,
                datePerformed = System.currentTimeMillis(),
                status = status,
                summary = summary,
                keyParameters = keyParameters,
                doctorNotes = doctorNotes,
                orderedBy = orderedBy,
                facility = facility
            )
            repository.addLabResult(entity)
            _userMessage.emit("Diagnostic lab test '$testName' added successfully.")
        }
    }

    fun saveLabResult(labResult: LabResultEntity) {
        viewModelScope.launch {
            if (labResult.id == 0L) {
                repository.addLabResult(labResult)
                _userMessage.emit("Diagnostic lab test '${labResult.testName}' added successfully.")
            } else {
                repository.updateLabResult(labResult)
                _userMessage.emit("Diagnostic lab test '${labResult.testName}' updated.")
            }
        }
    }

    fun deleteLabResult(id: Long) {
        viewModelScope.launch {
            repository.deleteLabResult(id)
            _userMessage.emit("Lab record deleted.")
        }
    }

    fun recordVitalsForPatient(
        patientId: String,
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        tempF: Float,
        glucose: Int,
        respRate: Int,
        weightLbs: Float,
        notes: String
    ) {
        viewModelScope.launch {
            val active = activeAccount.value
            val doctorName = if (active != null) "${active.name} (${active.role})" else "Attending Physician"
            repository.addVitalSign(
                patientId = patientId,
                systolicBp = systolic,
                diastolicBp = diastolic,
                heartRate = heartRate,
                oxygenSaturation = spo2,
                temperatureF = tempF,
                bloodGlucose = glucose,
                respiratoryRate = respRate,
                weightLbs = weightLbs,
                notes = notes,
                measuredBy = doctorName
            )
            _userMessage.emit("Vital signs recorded for patient ID $patientId.")
        }
    }

    fun recordDailyActivityForPatient(
        patientId: String,
        activityType: String,
        durationMinutes: Int,
        metricValue: String,
        painScore: Int,
        mood: String,
        notes: String
    ) {
        viewModelScope.launch {
            val active = activeAccount.value
            val loggedByName = if (active != null) "${active.name} (${active.role})" else "Attending Physician"
            repository.addDailyActivity(
                patientId = patientId,
                activityType = activityType,
                durationMinutes = durationMinutes,
                metricValue = metricValue,
                painScore = painScore,
                mood = mood,
                notes = notes,
                loggedBy = loggedByName
            )
            _userMessage.emit("Daily activity & note recorded for patient ID $patientId.")
        }
    }

    fun sendCaregiverSpecialInstruction(
        targetPatientId: String,
        targetPatientName: String,
        title: String,
        message: String,
        severity: String = "URGENT",
        actionLink: String = "NONE"
    ) {
        viewModelScope.launch {
            val active = activeAccount.value
            val senderName = active?.name ?: "Dr. Sarah Jenkins, MD"
            val senderId = active?.userId ?: "1001"
            val alert = com.example.data.local.entities.PatientAlertNoteEntity(
                targetPatientId = targetPatientId,
                targetPatientName = targetPatientName,
                senderId = senderId,
                senderName = "$senderName (Attending Physician)",
                senderRole = "DOCTOR",
                title = title,
                message = message,
                severity = severity,
                timestamp = System.currentTimeMillis(),
                isAcknowledged = false,
                actionLink = actionLink
            )
            repository.sendPatientAlertNote(alert)
            _userMessage.emit("Special instruction sent to caregiver for $targetPatientName.")
        }
    }

    fun selectCaregiverTargetPatient(patient: UserAccountEntity) {
        _doctorTargetPatient.value = patient
        viewModelScope.launch {
            _userMessage.emit("Switched active monitored patient to ${patient.name}")
        }
    }

    // Appointments
    fun bookAppointment(
        doctorName: String,
        specialty: String,
        appointmentType: String,
        dateTimeEpoch: Long,
        timeSlot: String,
        durationMinutes: Int,
        reason: String,
        location: String
    ) {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val patientId = resolveTargetPatientId(active, _doctorTargetPatient.value)
            repository.scheduleAppointment(
                patientId = patientId,
                doctorName = doctorName,
                specialty = specialty,
                appointmentType = appointmentType,
                dateTimeEpoch = dateTimeEpoch,
                timeSlot = timeSlot,
                durationMinutes = durationMinutes,
                reason = reason,
                locationOrLink = location
            )
            _userMessage.emit("Appointment confirmed with $doctorName")
        }
    }

    fun cancelAppointment(id: Long) {
        viewModelScope.launch {
            repository.cancelAppointment(id)
            _userMessage.emit("Appointment cancelled.")
        }
    }

    // Encrypted Messaging
    fun sendMessage(
        peerId: String,
        peerName: String,
        messageText: String,
        attachmentName: String? = null,
        attachmentType: String? = null,
        attachmentSize: String? = null
    ) {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            repository.sendEncryptedMessage(
                senderId = active.userId,
                senderName = active.name,
                senderRole = active.role,
                receiverId = peerId,
                receiverName = peerName,
                messageText = messageText,
                attachmentName = attachmentName,
                attachmentType = attachmentType,
                attachmentSize = attachmentSize
            )
            _userMessage.emit("Message delivered with AES-256 E2EE security.")
        }
    }

    // PDF Exporters
    fun exportVitalsPdfReport() {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val vitals = vitalsList.value
            if (vitals.isEmpty()) {
                _userMessage.emit("No vitals records available to export.")
                return@launch
            }
            val file = PdfReportExporter.exportVitalsPdf(getApplication(), active, vitals)
            if (file != null) {
                _pdfExportedFile.value = file
                _userMessage.emit("Vitals PDF Report generated (${file.length() / 1024} KB)")
            } else {
                _userMessage.emit("Failed to generate PDF.")
            }
        }
    }

    fun exportMedicationsPdfReport() {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val meds = medicationsList.value
            if (meds.isEmpty()) {
                _userMessage.emit("No medication records available to export.")
                return@launch
            }
            val file = PdfReportExporter.exportMedicationsPdf(getApplication(), active, meds)
            if (file != null) {
                _pdfExportedFile.value = file
                _userMessage.emit("Medications PDF Report generated (${file.length() / 1024} KB)")
            } else {
                _userMessage.emit("Failed to generate PDF.")
            }
        }
    }

    fun exportActivitiesPdfReport() {
        viewModelScope.launch {
            val active = activeAccount.value ?: return@launch
            val acts = activitiesList.value
            if (acts.isEmpty()) {
                _userMessage.emit("No daily activities records available to export.")
                return@launch
            }
            val file = PdfReportExporter.exportActivitiesPdf(getApplication(), active, acts)
            if (file != null) {
                _pdfExportedFile.value = file
                _userMessage.emit("Activities PDF Report generated (${file.length() / 1024} KB)")
            } else {
                _userMessage.emit("Failed to generate PDF.")
            }
        }
    }

    fun shareCurrentPdf() {
        val file = _pdfExportedFile.value ?: return
        PdfReportExporter.sharePdf(getApplication(), file, "ANA Care Health Record Report")
    }

    // Settings Profile Updates
    fun updateProfileSettings(account: UserAccountEntity) {
        viewModelScope.launch {
            repository.updateAccount(account)
            _userMessage.emit("Profile settings updated successfully.")
        }
    }

    fun toggleBiometricSetting(enabled: Boolean) {
        viewModelScope.launch {
            val current = activeAccount.value ?: return@launch
            val updated = current.copy(biometricEnabled = enabled)
            repository.updateAccount(updated)
            _userMessage.emit("Biometric authentication ${if (enabled) "enabled" else "disabled"}.")
        }
    }

    fun toggleMfaSetting(enabled: Boolean) {
        viewModelScope.launch {
            val current = activeAccount.value ?: return@launch
            val updated = current.copy(mfaEnabled = enabled)
            repository.updateAccount(updated)
            _userMessage.emit("Multi-factor authentication (2FA) ${if (enabled) "enabled" else "disabled"}.")
        }
    }

    // ==========================================
    // Clinical Notes & Urgent Pop-up Operations
    // ==========================================

    fun setShowSendDoctorNoteDialog(show: Boolean) {
        _showSendDoctorNoteDialog.value = show
    }

    fun setShowSendAdminNoteDialog(show: Boolean) {
        _showSendAdminNoteDialog.value = show
    }

    fun dismissCurrentAlertPopup() {
        _currentAlertPopup.value = null
    }

    fun acknowledgeAlertNote(alertId: Long) {
        viewModelScope.launch {
            repository.acknowledgeAlertNote(alertId)
            _currentAlertPopup.value = null
            _userMessage.emit("Important clinical alert acknowledged.")
        }
    }

    fun sendImportantAlertNote(
        targetPatientId: String,
        targetPatientName: String,
        title: String,
        message: String,
        severity: String, // "URGENT", "WARNING", "MEDICATION_ALERT", "INFO"
        actionLink: String = "NONE"
    ) {
        viewModelScope.launch {
            val sender = activeAccount.value
            val senderId = sender?.userId ?: "system_user"
            val senderName = sender?.name ?: "Care Team"
            val senderRole = sender?.role ?: "DOCTOR"

            val alert = PatientAlertNoteEntity(
                targetPatientId = targetPatientId,
                targetPatientName = targetPatientName,
                senderId = senderId,
                senderName = senderName,
                senderRole = senderRole,
                title = title,
                message = message,
                severity = severity,
                timestamp = System.currentTimeMillis(),
                isAcknowledged = false,
                actionLink = actionLink
            )

            repository.sendPatientAlertNote(alert)
            _showSendDoctorNoteDialog.value = false
            _showSendAdminNoteDialog.value = false
            _userMessage.emit("Important pop-up note dispatched to $targetPatientName screen.")
        }
    }

    fun deleteAlertNote(alertId: Long) {
        viewModelScope.launch {
            repository.deleteAlertNote(alertId)
            _userMessage.emit("Alert note removed.")
        }
    }

    // ==========================================
    // Admin Live Dashboard Layout & OTA Update Controls
    // ==========================================

    fun updatePatientLayoutSettings(
        showVitals: Boolean,
        showMeds: Boolean,
        showActivities: Boolean,
        showLabs: Boolean,
        showEmergency: Boolean,
        density: String,
        greeting: String
    ) {
        viewModelScope.launch {
            val current = appConfig.value
            val updated = current.copy(
                showVitalsSummary = showVitals,
                showMedicationSection = showMeds,
                showDailyActivities = showActivities,
                showLabResults = showLabs,
                showEmergencyBanner = showEmergency,
                patientLayoutDensity = density,
                patientGreetingMessage = greeting,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.saveAppConfig(updated)
            _userMessage.emit("Patient Dashboard layout configuration saved live.")
        }
    }

    fun updateDoctorLayoutSettings(
        compactMode: Boolean,
        autoExpandCriticals: Boolean,
        prescriptionQuickBar: Boolean,
        highlightCriticalVitals: Boolean
    ) {
        viewModelScope.launch {
            val current = appConfig.value
            val updated = current.copy(
                doctorCompactMode = compactMode,
                doctorAutoExpandCriticals = autoExpandCriticals,
                doctorPrescriptionQuickBar = prescriptionQuickBar,
                doctorHighlightCriticalVitals = highlightCriticalVitals,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.saveAppConfig(updated)
            _userMessage.emit("Doctor Clinical Workstation layout updated live.")
        }
    }

    fun deployLiveAppUpdate(
        versionName: String,
        buildNumber: Int,
        releaseNotes: String
    ) {
        viewModelScope.launch {
            val current = appConfig.value
            val updated = current.copy(
                appVersionName = versionName,
                appBuildNumber = buildNumber,
                updateReleaseNotes = releaseNotes,
                isUpdateBannerVisible = true,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.saveAppConfig(updated)
            _userMessage.emit("Application OTA patch v$versionName deployed successfully!")
        }
    }

    fun setMaintenanceMode(
        enabled: Boolean,
        announcement: String
    ) {
        viewModelScope.launch {
            val current = appConfig.value
            val updated = current.copy(
                isMaintenanceMode = enabled,
                maintenanceAnnouncement = announcement,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.saveAppConfig(updated)
            _userMessage.emit("System maintenance status broadcasted live.")
        }
    }

    fun setSystemThemeAccent(accent: String) {
        viewModelScope.launch {
            val current = appConfig.value
            val updated = current.copy(
                systemThemeAccent = accent,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.saveAppConfig(updated)
            _userMessage.emit("System theme accent updated to $accent.")
        }
    }

    fun dismissUpdateBanner() {
        viewModelScope.launch {
            val current = appConfig.value
            val updated = current.copy(isUpdateBannerVisible = false)
            repository.saveAppConfig(updated)
        }
    }

    // ==========================================
    // AUDIT LOGGING ENGINE & ADMIN LOG TAB STATE
    // ==========================================
    val allAuditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedAuditUserId = MutableStateFlow("ALL")
    val selectedAuditUserId: StateFlow<String> = _selectedAuditUserId.asStateFlow()

    private val _selectedAuditCategory = MutableStateFlow("ALL")
    val selectedAuditCategory: StateFlow<String> = _selectedAuditCategory.asStateFlow()

    private val _selectedAuditSeverity = MutableStateFlow("ALL")
    val selectedAuditSeverity: StateFlow<String> = _selectedAuditSeverity.asStateFlow()

    private val _auditSearchQuery = MutableStateFlow("")
    val auditSearchQuery: StateFlow<String> = _auditSearchQuery.asStateFlow()

    fun selectAuditUser(userId: String) {
        _selectedAuditUserId.value = userId
    }

    fun selectAuditCategory(category: String) {
        _selectedAuditCategory.value = category
    }

    fun selectAuditSeverity(severity: String) {
        _selectedAuditSeverity.value = severity
    }

    fun setAuditSearchQuery(query: String) {
        _auditSearchQuery.value = query
    }

    val filteredAuditLogs: StateFlow<List<AuditLogEntity>> = combine(
        allAuditLogs,
        _selectedAuditUserId,
        _selectedAuditCategory,
        _selectedAuditSeverity,
        _auditSearchQuery
    ) { logs, userFilter, catFilter, sevFilter, query ->
        logs.filter { log ->
            val matchesUser = (userFilter == "ALL") || (log.userId == userFilter)
            val matchesCat = (catFilter == "ALL") || (log.category.equals(catFilter, ignoreCase = true))
            val matchesSev = (sevFilter == "ALL") || (log.severity.equals(sevFilter, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                    log.description.contains(query, ignoreCase = true) ||
                    log.details.contains(query, ignoreCase = true) ||
                    log.userName.contains(query, ignoreCase = true) ||
                    log.actionType.contains(query, ignoreCase = true) ||
                    log.userId.contains(query, ignoreCase = true) ||
                    log.category.contains(query, ignoreCase = true)
            matchesUser && matchesCat && matchesSev && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun logAuditAction(
        actionType: String,
        category: String,
        description: String,
        details: String = "",
        severity: String = "INFO"
    ) {
        viewModelScope.launch {
            val user = activeAccount.value
            val uid = user?.userId ?: "9001"
            val uname = user?.name ?: "Marcus Vance (Admin)"
            val urole = user?.role ?: "ADMIN"
            val logId = repository.logUserAction(
                userId = uid,
                userName = uname,
                userRole = urole,
                actionType = actionType,
                category = category,
                description = description,
                details = details,
                severity = severity
            )
            val entry = AuditLogEntity(
                id = logId,
                timestamp = System.currentTimeMillis(),
                userId = uid,
                userName = uname,
                userRole = urole,
                actionType = actionType,
                category = category,
                description = description,
                details = details,
                severity = severity
            )
            LogFileHelper.appendLog(getApplication(), entry)
        }
    }

    fun clearAllAuditLogs() {
        viewModelScope.launch {
            repository.clearAuditLogs()
            _userMessage.emit("System & User audit logs cleared successfully.")
        }
    }

    fun exportAuditLogFile(targetUserId: String = "ALL", targetUserName: String = "All Users"): File {
        val allLogs = allAuditLogs.value
        val logsToExport = if (targetUserId == "ALL") allLogs else allLogs.filter { it.userId == targetUserId }
        val file = LogFileHelper.generateCompleteLogFile(
            context = getApplication(),
            logs = logsToExport,
            filterUser = targetUserId,
            targetUserName = targetUserName
        )
        return file
    }

    fun shareAuditLogFile(targetUserId: String = "ALL", targetUserName: String = "All Users") {
        val file = exportAuditLogFile(targetUserId, targetUserName)
        LogFileHelper.shareLogFile(getApplication(), file, "Share Audit Logs ($targetUserName)")
    }

    fun getRawLogFileContent(): String {
        return LogFileHelper.readLogFileContent(getApplication())
    }
}
