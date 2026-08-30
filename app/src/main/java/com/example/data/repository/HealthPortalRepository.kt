package com.example.data.repository

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
import com.example.data.security.SecurityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HealthPortalRepository(private val database: AppDatabase) {

    private val userAccountDao = database.userAccountDao()
    private val vitalSignDao = database.vitalSignDao()
    private val medicationDao = database.medicationDao()
    private val medicationAdministrationLogDao = database.medicationAdministrationLogDao()
    private val dailyActivityDao = database.dailyActivityDao()
    private val labResultDao = database.labResultDao()
    private val appointmentDao = database.appointmentDao()
    private val encryptedMessageDao = database.encryptedMessageDao()
    private val patientAlertNoteDao = database.patientAlertNoteDao()
    private val appConfigDao = database.appConfigDao()
    private val medicalGalleryDao = database.medicalGalleryDao()
    private val auditLogDao = database.auditLogDao()

    // Flow Getters
    val allAccounts: Flow<List<UserAccountEntity>> = userAccountDao.getAllAccounts()
    val activeAccount: Flow<UserAccountEntity?> = userAccountDao.getActiveAccount()
    val appConfig: Flow<AppConfigEntity?> = appConfigDao.getAppConfigFlow()
    val allAlertNotes: Flow<List<PatientAlertNoteEntity>> = patientAlertNoteDao.getAllAlerts()
    val allVitals: Flow<List<VitalSignEntity>> = vitalSignDao.getAllVitals()
    val allGallery: Flow<List<MedicalGalleryEntity>> = medicalGalleryDao.getAllGallery()
    val allAuditLogs: Flow<List<AuditLogEntity>> = auditLogDao.getAllLogs()

    fun getAuditLogsForUser(userId: String): Flow<List<AuditLogEntity>> =
        if (userId == "ALL") auditLogDao.getAllLogs() else auditLogDao.getLogsForUser(userId)

    suspend fun logUserAction(
        userId: String,
        userName: String,
        userRole: String,
        actionType: String,
        category: String,
        description: String,
        details: String = "",
        severity: String = "INFO",
        ipAddress: String = "127.0.0.1 (Local Session)"
    ): Long {
        val now = System.currentTimeMillis()
        val formatted = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now))
        val entry = AuditLogEntity(
            timestamp = now,
            formattedTimestamp = formatted,
            userId = userId,
            userName = userName,
            userRole = userRole,
            actionType = actionType,
            category = category,
            description = description,
            details = details,
            severity = severity,
            ipAddress = ipAddress
        )
        return auditLogDao.insertLog(entry)
    }

    suspend fun clearAuditLogs() {
        auditLogDao.clearAllLogs()
    }

    suspend fun getAllAuditLogsDirect(): List<AuditLogEntity> {
        return auditLogDao.getAllLogsDirect()
    }

    suspend fun getAuditLogsForUserDirect(userId: String): List<AuditLogEntity> {
        return if (userId == "ALL") auditLogDao.getAllLogsDirect() else auditLogDao.getLogsForUserDirect(userId)
    }

    fun getVitalsForPatient(patientId: String): Flow<List<VitalSignEntity>> =
        vitalSignDao.getVitalsForPatient(patientId)

    fun getGalleryForPatient(patientId: String): Flow<List<MedicalGalleryEntity>> =
        medicalGalleryDao.getGalleryForPatient(patientId)

    fun getMedicationsForPatient(patientId: String): Flow<List<MedicationEntity>> =
        medicationDao.getMedicationsForPatient(patientId)

    fun getRunningMedicationsForPatient(patientId: String): Flow<List<MedicationEntity>> =
        medicationDao.getRunningMedicationsForPatient(patientId)

    fun getMedicationLogsForPatient(patientId: String): Flow<List<MedicationAdministrationLogEntity>> =
        medicationAdministrationLogDao.getLogsForPatient(patientId)

    fun getActivitiesForPatient(patientId: String): Flow<List<DailyActivityEntity>> =
        dailyActivityDao.getActivitiesForPatient(patientId)

    fun getLabResultsForPatient(patientId: String): Flow<List<LabResultEntity>> =
        labResultDao.getLabResultsForPatient(patientId)

    fun getAppointmentsForPatient(patientId: String): Flow<List<AppointmentEntity>> =
        appointmentDao.getAppointmentsForPatient(patientId)

    fun getMessagesBetween(userId: String, peerId: String): Flow<List<EncryptedMessageEntity>> =
        encryptedMessageDao.getMessagesBetween(userId, peerId)

    fun getAllMessagesForUser(userId: String): Flow<List<EncryptedMessageEntity>> =
        encryptedMessageDao.getAllMessagesForUser(userId)

    fun getAlertsForPatient(patientId: String): Flow<List<PatientAlertNoteEntity>> =
        patientAlertNoteDao.getAlertsForPatient(patientId)

    fun getUnacknowledgedAlertsForPatient(patientId: String): Flow<List<PatientAlertNoteEntity>> =
        patientAlertNoteDao.getUnacknowledgedAlertsForPatient(patientId)

    // Alert Notes Operations
    suspend fun sendPatientAlertNote(alert: PatientAlertNoteEntity): Long =
        patientAlertNoteDao.insertAlert(alert)

    suspend fun acknowledgeAlertNote(alertId: Long) {
        patientAlertNoteDao.acknowledgeAlert(alertId, System.currentTimeMillis())
    }

    suspend fun deleteAlertNote(alertId: Long) {
        patientAlertNoteDao.deleteAlert(alertId)
    }

    // Live App Configuration & Layout Operations
    suspend fun saveAppConfig(config: AppConfigEntity) {
        appConfigDao.insertOrUpdateConfig(config)
    }

    suspend fun getAppConfigDirect(): AppConfigEntity {
        return appConfigDao.getAppConfig() ?: AppConfigEntity()
    }

    // User Operations
    suspend fun switchActiveAccount(userId: String) {
        database.userAccountDao().clearActiveAccounts()
        database.userAccountDao().setActiveAccount(userId)
    }

    suspend fun saveAccount(account: UserAccountEntity) {
        userAccountDao.insertAccount(account)
    }

    suspend fun updateAccount(account: UserAccountEntity) {
        userAccountDao.updateAccount(account)
    }

    suspend fun deleteAccount(userId: String) {
        userAccountDao.deleteAccount(userId)
    }

    // Vitals Operations
    suspend fun addVitalSign(
        patientId: String,
        systolicBp: Int,
        diastolicBp: Int,
        heartRate: Int,
        oxygenSaturation: Int,
        temperatureF: Float,
        bloodGlucose: Int,
        respiratoryRate: Int,
        weightLbs: Float,
        notes: String,
        measuredBy: String
    ): Long {
        val status = classifyVitalStatus(systolicBp, diastolicBp, heartRate, oxygenSaturation, bloodGlucose, temperatureF)
        val hash = SecurityManager.generateRecordDigest("$patientId-$systolicBp/$diastolicBp-$heartRate-${System.currentTimeMillis()}")
        val entity = VitalSignEntity(
            patientId = patientId,
            timestamp = System.currentTimeMillis(),
            systolicBp = systolicBp,
            diastolicBp = diastolicBp,
            heartRate = heartRate,
            oxygenSaturation = oxygenSaturation,
            temperatureF = temperatureF,
            bloodGlucose = bloodGlucose,
            respiratoryRate = respiratoryRate,
            weightLbs = weightLbs,
            notes = notes,
            status = status,
            measuredBy = measuredBy,
            encryptionHash = hash
        )
        return vitalSignDao.insertVital(entity)
    }

    suspend fun deleteVital(id: Long) {
        vitalSignDao.deleteVitalById(id)
    }

    // Medication Operations
    suspend fun addMedication(
        patientId: String,
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
    ): Long {
        val sDateFormatted = if (startDateFormatted.isNotBlank()) startDateFormatted else SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(startDate))
        val entity = MedicationEntity(
            patientId = patientId,
            name = name,
            dosage = dosage,
            frequency = frequency,
            route = route,
            scheduledTime = scheduledTime,
            instructions = instructions,
            prescribedBy = prescribedBy,
            startDate = startDate,
            startDateFormatted = sDateFormatted,
            endDate = endDate,
            endDateFormatted = endDateFormatted,
            isTakenToday = false,
            reminderEnabled = true,
            category = category,
            refillsRemaining = refills,
            status = status,
            lastAction = "",
            lastActionTimestamp = 0L,
            lastActionDateFormatted = ""
        )
        return medicationDao.insertMedication(entity)
    }

    suspend fun updateMedication(medication: MedicationEntity) {
        medicationDao.updateMedication(medication)
    }

    suspend fun updateMedicationStatus(id: Long, status: String) {
        medicationDao.updateMedicationStatus(id, status)
    }

    suspend fun logMedicationAdministration(
        patientId: String,
        medicationId: Long,
        medicationName: String,
        dosage: String,
        status: String = "TAKEN",
        administeredBy: String = "Self (Patient)",
        notes: String = ""
    ): Long {
        val timestamp = System.currentTimeMillis()
        val formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(timestamp))
        val log = MedicationAdministrationLogEntity(
            patientId = patientId,
            medicationId = medicationId,
            medicationName = medicationName,
            dosage = dosage,
            administeredTimestamp = timestamp,
            administeredDateFormatted = formattedDate,
            status = status,
            administeredBy = administeredBy,
            notes = notes
        )
        return medicationAdministrationLogDao.insertLog(log)
    }

    suspend fun markMedicationAction(
        id: Long,
        action: String,
        patientId: String = "",
        medName: String = "",
        dosage: String = "",
        administeredBy: String = "Self (Patient)"
    ) {
        val isTaken = action == "TAKEN"
        val timestamp = System.currentTimeMillis()
        val formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
        medicationDao.recordMedicationAction(
            id = id,
            taken = isTaken,
            action = action,
            timestamp = timestamp,
            formattedDate = formattedDate
        )

        if (patientId.isNotBlank() && medName.isNotBlank()) {
            val log = MedicationAdministrationLogEntity(
                patientId = patientId,
                medicationId = id,
                medicationName = medName,
                dosage = dosage,
                administeredTimestamp = timestamp,
                administeredDateFormatted = formattedDate,
                status = if (isTaken) "TAKEN" else "MISSED",
                administeredBy = administeredBy,
                notes = if (isTaken) "Regular dose administered on schedule." else "Dose recorded as missed/skipped."
            )
            medicationAdministrationLogDao.insertLog(log)
        }
    }

    suspend fun toggleMedicationTaken(id: Long, isTaken: Boolean, patientId: String = "", medName: String = "", dosage: String = "") {
        if (isTaken) {
            markMedicationAction(id, "TAKEN", patientId, medName, dosage)
        } else {
            medicationDao.recordMedicationAction(id, false, "", 0L, "")
        }
    }

    suspend fun deleteMedication(id: Long) {
        medicationDao.deleteMedicationById(id)
    }


    // Activity Operations
    suspend fun addDailyActivity(
        patientId: String,
        activityType: String,
        durationMinutes: Int,
        metricValue: String,
        painScore: Int,
        mood: String,
        notes: String,
        loggedBy: String
    ): Long {
        val entity = DailyActivityEntity(
            patientId = patientId,
            timestamp = System.currentTimeMillis(),
            activityType = activityType,
            durationMinutes = durationMinutes,
            metricValue = metricValue,
            painScore = painScore,
            mood = mood,
            notes = notes,
            loggedBy = loggedBy
        )
        return dailyActivityDao.insertActivity(entity)
    }

    suspend fun deleteActivity(id: Long) {
        dailyActivityDao.deleteActivityById(id)
    }

    // Medical Gallery Operations
    suspend fun addGalleryImage(
        patientId: String,
        title: String,
        category: String,
        imageUri: String,
        notes: String,
        loggedByRole: String,
        loggedByName: String
    ): Long {
        val timestamp = System.currentTimeMillis()
        val formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(timestamp))
        val entity = MedicalGalleryEntity(
            patientId = patientId,
            timestamp = timestamp,
            title = title,
            category = category,
            imageUri = imageUri,
            notes = notes,
            loggedByRole = loggedByRole,
            loggedByName = loggedByName,
            formattedDate = formattedDate
        )
        return medicalGalleryDao.insertImage(entity)
    }

    suspend fun deleteGalleryImage(id: Long) {
        medicalGalleryDao.deleteImageById(id)
    }

    // Appointments Operations
    suspend fun scheduleAppointment(
        patientId: String,
        doctorName: String,
        specialty: String,
        appointmentType: String,
        dateTimeEpoch: Long,
        timeSlot: String,
        durationMinutes: Int,
        reason: String,
        locationOrLink: String
    ): Long {
        val entity = AppointmentEntity(
            patientId = patientId,
            doctorName = doctorName,
            specialty = specialty,
            appointmentType = appointmentType,
            scheduledEpochMillis = dateTimeEpoch,
            timeSlotString = timeSlot,
            durationMinutes = durationMinutes,
            status = "CONFIRMED",
            reason = reason,
            locationOrLink = locationOrLink,
            doctorNotes = "Pre-consultation notes submitted by patient."
        )
        return appointmentDao.insertAppointment(entity)
    }

    suspend fun updateAppointment(appointment: AppointmentEntity) {
        appointmentDao.updateAppointment(appointment)
    }

    suspend fun cancelAppointment(id: Long) {
        appointmentDao.deleteAppointmentById(id)
    }

    // Encrypted Messages Operations
    suspend fun sendEncryptedMessage(
        senderId: String,
        senderName: String,
        senderRole: String,
        receiverId: String,
        receiverName: String,
        messageText: String,
        attachmentName: String? = null,
        attachmentType: String? = null,
        attachmentSize: String? = null
    ): Long {
        val cipherDigest = SecurityManager.encrypt(messageText)
        val entity = EncryptedMessageEntity(
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            receiverId = receiverId,
            receiverName = receiverName,
            timestamp = System.currentTimeMillis(),
            messageText = messageText,
            cipherTextDigest = cipherDigest,
            isEncrypted = true,
            attachmentName = attachmentName,
            attachmentType = attachmentType,
            attachmentSize = attachmentSize,
            isRead = false
        )
        return encryptedMessageDao.insertMessage(entity)
    }

    suspend fun markMessagesAsRead(userId: String, peerId: String) {
        encryptedMessageDao.markAsRead(userId, peerId)
    }

    // Lab Results Operations
    suspend fun addLabResult(result: LabResultEntity): Long {
        return labResultDao.insertLabResult(result)
    }

    suspend fun updateLabResult(result: LabResultEntity) {
        labResultDao.insertLabResult(result)
    }

    suspend fun deleteLabResult(id: Long) {
        labResultDao.deleteLabResultById(id)
    }

    fun getCaregiversForPatient(patientId: String): Flow<List<UserAccountEntity>> =
        userAccountDao.getCaregiversForPatient(patientId)

    fun getPatientsForDoctor(doctorId: String): Flow<List<UserAccountEntity>> =
        userAccountDao.getPatientsForDoctor(doctorId)

    // Initial Data Seeding
    suspend fun seedInitialDataIfEmpty() {
        val existing = userAccountDao.getAllAccounts().firstOrNull()
        val needsFullSeeding = existing.isNullOrEmpty() ||
                existing.count { it.role == "PATIENT" } < 50 ||
                existing.count { it.role == "MEDICAL_PROFESSIONAL" } < 10

        if (needsFullSeeding) {
            val doctors = DummyDataSeeder.generateDoctors()
            val patients = DummyDataSeeder.generatePatients(doctors)
            val caregivers = DummyDataSeeder.generateCaregivers(patients, doctors)
            val admin = DummyDataSeeder.generateAdminProfile()

            val allAccountsToInsert = mutableListOf<UserAccountEntity>()
            allAccountsToInsert.addAll(doctors)
            allAccountsToInsert.addAll(patients)
            allAccountsToInsert.addAll(caregivers)
            allAccountsToInsert.add(admin)

            userAccountDao.insertAll(allAccountsToInsert)

            // Seed Vitals for all patients
            val now = System.currentTimeMillis()
            val hour = 3600 * 1000L
            val day = 24 * hour

            val vitals = DummyDataSeeder.generateClinicalSeedVitals(now, patients)
            vitalSignDao.insertAll(vitals)

            // Seed Medications
            val meds = listOf(
                MedicationEntity(
                    patientId = "21001001",
                    name = "Lisinopril",
                    dosage = "10 mg",
                    frequency = "Once daily",
                    route = "Oral",
                    scheduledTime = "08:00 AM",
                    instructions = "Take with a full glass of water every morning.",
                    prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    category = "Cardiovascular / BP",
                    isTakenToday = true,
                    refillsRemaining = 3,
                    status = "RUNNING",
                    lastAction = "TAKEN",
                    lastActionTimestamp = now - 2 * hour,
                    lastActionDateFormatted = "Today • 08:05 AM"
                ),
                MedicationEntity(
                    patientId = "21001001",
                    name = "Metformin HCl",
                    dosage = "500 mg",
                    frequency = "Twice daily",
                    route = "Oral",
                    scheduledTime = "08:00 AM & 07:00 PM",
                    instructions = "Take with meals to prevent stomach upset.",
                    prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    category = "Endocrine / Diabetes",
                    isTakenToday = true,
                    refillsRemaining = 2,
                    status = "RUNNING",
                    lastAction = "TAKEN",
                    lastActionTimestamp = now - 2 * hour,
                    lastActionDateFormatted = "Today • 08:10 AM"
                ),
                MedicationEntity(
                    patientId = "21001001",
                    name = "Atorvastatin Calcium",
                    dosage = "20 mg",
                    frequency = "Once daily at bedtime",
                    route = "Oral",
                    scheduledTime = "09:30 PM",
                    instructions = "Take in evening. Avoid grapefruit juice.",
                    prescribedBy = "Dr. Robert Chen, Cardiologist",
                    category = "Cholesterol",
                    isTakenToday = false,
                    refillsRemaining = 4,
                    status = "RUNNING",
                    lastAction = "",
                    lastActionTimestamp = 0L,
                    lastActionDateFormatted = ""
                ),
                MedicationEntity(
                    patientId = "21001001",
                    name = "Vitamin D3 (Cholecalciferol)",
                    dosage = "2,000 IU",
                    frequency = "Once daily",
                    route = "Oral",
                    scheduledTime = "12:00 PM",
                    instructions = "Take with lunch or healthy dietary fats.",
                    prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    category = "Supplements",
                    isTakenToday = false,
                    refillsRemaining = 5,
                    status = "RUNNING",
                    lastAction = "",
                    lastActionTimestamp = 0L,
                    lastActionDateFormatted = ""
                ),
                MedicationEntity(
                    patientId = "21001001",
                    name = "Amoxicillin (Completed Course)",
                    dosage = "500 mg",
                    frequency = "Three times daily",
                    route = "Oral",
                    scheduledTime = "08:00 AM, 02:00 PM, 08:00 PM",
                    instructions = "Course finished. Prescribed for seasonal bronchitis.",
                    prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    category = "Antibiotic",
                    isTakenToday = false,
                    refillsRemaining = 0,
                    status = "STOPPED",
                    lastAction = "SKIPPED",
                    lastActionTimestamp = now - 10 * day,
                    lastActionDateFormatted = "Discontinued by MD"
                ),
                // Medications for Pt. Arthur Vance (21001002)
                MedicationEntity(
                    patientId = "21001002",
                    name = "Amlodipine Besylate",
                    dosage = "5 mg",
                    frequency = "Once daily",
                    route = "Oral",
                    scheduledTime = "08:30 AM",
                    instructions = "Take in morning for hypertension control.",
                    prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    category = "Cardiovascular / BP",
                    isTakenToday = true,
                    refillsRemaining = 3,
                    status = "RUNNING",
                    lastAction = "TAKEN",
                    lastActionTimestamp = now - 1 * hour,
                    lastActionDateFormatted = "Today • 08:30 AM"
                ),
                MedicationEntity(
                    patientId = "21001002",
                    name = "Glucosamine Chondroitin",
                    dosage = "1,500 mg",
                    frequency = "Once daily",
                    route = "Oral",
                    scheduledTime = "12:00 PM",
                    instructions = "Take with lunch for joint support.",
                    prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    category = "Orthopedic / Joint Health",
                    isTakenToday = false,
                    refillsRemaining = 4,
                    status = "RUNNING",
                    lastAction = "",
                    lastActionTimestamp = 0L,
                    lastActionDateFormatted = ""
                ),
                // Medications for Pt. Harold Finch (21001003)
                MedicationEntity(
                    patientId = "21001003",
                    name = "Metoprolol Succinate ER",
                    dosage = "50 mg",
                    frequency = "Once daily",
                    route = "Oral",
                    scheduledTime = "08:00 AM",
                    instructions = "Take every morning with food. Do not crush.",
                    prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    category = "Cardiovascular / Beta Blocker",
                    isTakenToday = true,
                    refillsRemaining = 2,
                    status = "RUNNING",
                    lastAction = "TAKEN",
                    lastActionTimestamp = now - 3 * hour,
                    lastActionDateFormatted = "Today • 08:15 AM"
                ),
                MedicationEntity(
                    patientId = "21001003",
                    name = "Glipizide XL",
                    dosage = "10 mg",
                    frequency = "Once daily",
                    route = "Oral",
                    scheduledTime = "07:30 AM",
                    instructions = "Take 30 minutes before breakfast.",
                    prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    category = "Endocrine / Diabetes",
                    isTakenToday = true,
                    refillsRemaining = 3,
                    status = "RUNNING",
                    lastAction = "TAKEN",
                    lastActionTimestamp = now - 3 * hour,
                    lastActionDateFormatted = "Today • 07:35 AM"
                )
            )
            medicationDao.insertAll(meds)

            // Seed Initial Medication Administration Logs
            val initialMedLogs = listOf(
                MedicationAdministrationLogEntity(
                    patientId = "21001001",
                    medicationName = "Lisinopril",
                    dosage = "10 mg",
                    administeredTimestamp = now - 2 * hour,
                    administeredDateFormatted = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now - 2 * hour)),
                    status = "TAKEN",
                    administeredBy = "Self (Patient)",
                    notes = "Taken with breakfast and water."
                ),
                MedicationAdministrationLogEntity(
                    patientId = "21001001",
                    medicationName = "Metformin HCl",
                    dosage = "500 mg",
                    administeredTimestamp = now - 2 * hour,
                    administeredDateFormatted = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now - 2 * hour)),
                    status = "TAKEN",
                    administeredBy = "Self (Patient)",
                    notes = "Morning dose with food."
                ),
                MedicationAdministrationLogEntity(
                    patientId = "21001001",
                    medicationName = "Atorvastatin Calcium",
                    dosage = "20 mg",
                    administeredTimestamp = now - 1 * day - 3 * hour,
                    administeredDateFormatted = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now - 1 * day - 3 * hour)),
                    status = "TAKEN",
                    administeredBy = "James Vance (Caregiver)",
                    notes = "Evening dose administered."
                ),
                MedicationAdministrationLogEntity(
                    patientId = "21001001",
                    medicationName = "Lisinopril",
                    dosage = "10 mg",
                    administeredTimestamp = now - 1 * day - 14 * hour,
                    administeredDateFormatted = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now - 1 * day - 14 * hour)),
                    status = "TAKEN",
                    administeredBy = "Self (Patient)",
                    notes = "Morning routine."
                ),
                MedicationAdministrationLogEntity(
                    patientId = "21001001",
                    medicationName = "Metformin HCl",
                    dosage = "500 mg",
                    administeredTimestamp = now - 2 * day - 4 * hour,
                    administeredDateFormatted = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now - 2 * day - 4 * hour)),
                    status = "MISSED",
                    administeredBy = "Self (Patient)",
                    notes = "Missed evening dose due to doctor visit."
                )
            )
            medicationAdministrationLogDao.insertAllLogs(initialMedLogs)

            // Seed Activities
            val activities = listOf(
                DailyActivityEntity(
                    patientId = "21001001",
                    timestamp = now - 3 * hour,
                    activityType = "Walking / Steps",
                    durationMinutes = 25,
                    metricValue = "3,450 steps",
                    painScore = 1,
                    mood = "Great",
                    notes = "Morning neighborhood stroll in the sun.",
                    loggedBy = "Self"
                ),
                DailyActivityEntity(
                    patientId = "21001001",
                    timestamp = now - 1 * day,
                    activityType = "Physical Therapy",
                    durationMinutes = 35,
                    metricValue = "Knee & Hip Mobility Routine",
                    painScore = 2,
                    mood = "Good",
                    notes = "Resistance band stretching with therapist guidance.",
                    loggedBy = "James Vance (Caregiver)"
                ),
                DailyActivityEntity(
                    patientId = "21001001",
                    timestamp = now - 1 * day - 8 * hour,
                    activityType = "Sleep & Rest",
                    durationMinutes = 480,
                    metricValue = "8.0 hours uninterrupted",
                    painScore = 0,
                    mood = "Great",
                    notes = "Woke up refreshed without joint stiffness.",
                    loggedBy = "Self"
                ),
                DailyActivityEntity(
                    patientId = "21001001",
                    timestamp = now - 2 * day,
                    activityType = "Water Intake & Hydration",
                    durationMinutes = 0,
                    metricValue = "2,200 mL hydration",
                    painScore = 0,
                    mood = "Good",
                    notes = "Met daily hydration target goal.",
                    loggedBy = "Self"
                ),
                // Activities for Pt. Arthur Vance (21001002)
                DailyActivityEntity(
                    patientId = "21001002",
                    timestamp = now - 2 * hour,
                    activityType = "Walking / Steps",
                    durationMinutes = 20,
                    metricValue = "2,100 steps",
                    painScore = 2,
                    mood = "Good",
                    notes = "Slow garden walk with cane support.",
                    loggedBy = "CG. James Vance"
                ),
                DailyActivityEntity(
                    patientId = "21001002",
                    timestamp = now - 1 * day,
                    activityType = "Sleep & Rest",
                    durationMinutes = 450,
                    metricValue = "7.5 hours rest",
                    painScore = 1,
                    mood = "Fair",
                    notes = "Restful evening.",
                    loggedBy = "Self"
                ),
                // Activities for Pt. Harold Finch (21001003)
                DailyActivityEntity(
                    patientId = "21001003",
                    timestamp = now - 5 * hour,
                    activityType = "Walking / Steps",
                    durationMinutes = 30,
                    metricValue = "4,200 steps",
                    painScore = 1,
                    mood = "Energetic",
                    notes = "Brisk morning walk around park.",
                    loggedBy = "Self"
                ),
                DailyActivityEntity(
                    patientId = "21001003",
                    timestamp = now - 1 * day,
                    activityType = "Physical Therapy",
                    durationMinutes = 40,
                    metricValue = "Cardiorespiratory Conditioning",
                    painScore = 2,
                    mood = "Good",
                    notes = "Stationary cycling and breathing exercises.",
                    loggedBy = "Therapist Mark"
                )
            )
            dailyActivityDao.insertAll(activities)

            // Seed Lab Results
            val labs = listOf(
                LabResultEntity(
                    patientId = "21001001",
                    testName = "Comprehensive Metabolic Panel (CMP)",
                    category = "Blood Chemistry",
                    datePerformed = now - 5 * day,
                    orderedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    facility = "ANA Care Regional Diagnostic Core",
                    status = "NORMAL",
                    summary = "All liver and renal function markers are within target baseline.",
                    keyParameters = "• Glucose (Fasting): 96 mg/dL (Normal 70-99)\n• BUN: 14 mg/dL (Normal 7-20)\n• Creatinine: 0.85 mg/dL (Normal 0.5-1.1)\n• eGFR: >60 mL/min/1.73m² (Normal >60)\n• Sodium: 140 mEq/L (Normal 135-145)\n• Potassium: 4.2 mEq/L (Normal 3.5-5.0)\n• Calcium: 9.4 mg/dL (Normal 8.5-10.2)",
                    doctorNotes = "Electrolyte balance and renal parameters look exceptional. Maintain current medication dosage."
                ),
                LabResultEntity(
                    patientId = "21001001",
                    testName = "Hemoglobin A1c (HbA1c)",
                    category = "Endocrine / Glycemic Control",
                    datePerformed = now - 12 * day,
                    orderedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    facility = "ANA Care Regional Diagnostic Core",
                    status = "NORMAL",
                    summary = "Glycated hemoglobin indicates stable 3-month glycemic control.",
                    keyParameters = "• HbA1c: 6.1% (Target < 6.5% for management)\n• Estimated Average Glucose (eAG): 128 mg/dL\n• Previous Value (3 mos ago): 6.4%",
                    doctorNotes = "Trending down favorably from 6.4%. Excellent adherence to diet and Metformin."
                ),
                LabResultEntity(
                    patientId = "21001001",
                    testName = "Lipid Profile Panel",
                    category = "Cardiovascular Risk Assessment",
                    datePerformed = now - 20 * day,
                    orderedBy = "Dr. Robert Chen, Cardiologist",
                    facility = "ANA Care Regional Diagnostic Core",
                    status = "ELEVATED",
                    summary = "Mild elevation in LDL cholesterol; HDL and Triglycerides optimal.",
                    keyParameters = "• Total Cholesterol: 198 mg/dL (Desirable < 200)\n• HDL Cholesterol: 58 mg/dL (Optimal > 50)\n• LDL Cholesterol: 118 mg/dL (Elevated > 100)\n• Triglycerides: 110 mg/dL (Normal < 150)",
                    doctorNotes = "Atorvastatin 20mg continuing. Recommended low-saturated-fat home meal plans."
                ),
                // Lab Results for Pt. Arthur Vance (21001002)
                LabResultEntity(
                    patientId = "21001002",
                    testName = "Complete Blood Count (CBC) with Differential",
                    category = "Hematology",
                    datePerformed = now - 8 * day,
                    orderedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    facility = "ANA Care Regional Diagnostic Core",
                    status = "NORMAL",
                    summary = "WBC, Hemoglobin, Hematocrit, and Platelets all within normal reference ranges.",
                    keyParameters = "• WBC: 6.8 K/uL (Normal 4.0-11.0)\n• RBC: 4.82 M/uL (Normal 4.3-5.9)\n• Hemoglobin: 14.8 g/dL (Normal 13.5-17.5)\n• Hematocrit: 44.2% (Normal 38.8-50.0)\n• Platelets: 245 K/uL (Normal 150-450)",
                    doctorNotes = "Normal hematologic panel. No signs of infection or anemia."
                ),
                // Lab Results for Pt. Harold Finch (21001003)
                LabResultEntity(
                    patientId = "21001003",
                    testName = "HbA1c & Fasting Insulin Panel",
                    category = "Endocrine / Diabetes",
                    datePerformed = now - 3 * day,
                    orderedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    facility = "ANA Care Regional Diagnostic Core",
                    status = "ELEVATED",
                    summary = "Elevated HbA1c 7.6%. Glycemic control requires titration of Glipizide.",
                    keyParameters = "• HbA1c: 7.6% (Target < 7.0%)\n• Fasting Glucose: 154 mg/dL (Normal 70-99)\n• C-Peptide: 2.1 ng/mL (Normal 1.1-4.4)",
                    doctorNotes = "Recommended adjustment of antidiabetic regimen and nutritional counseling."
                )
            )
            labResultDao.insertAll(labs)

            // Seed Appointments
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_MONTH, 2)
            cal.set(Calendar.HOUR_OF_DAY, 10)
            cal.set(Calendar.MINUTE, 0)

            val cal2 = Calendar.getInstance()
            cal2.add(Calendar.DAY_OF_MONTH, 7)
            cal2.set(Calendar.HOUR_OF_DAY, 14)
            cal2.set(Calendar.MINUTE, 30)

            val calPast = Calendar.getInstance()
            calPast.add(Calendar.DAY_OF_MONTH, -10)
            calPast.set(Calendar.HOUR_OF_DAY, 11)
            calPast.set(Calendar.MINUTE, 0)

            val appointments = listOf(
                AppointmentEntity(
                    patientId = "21001001",
                    doctorName = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    specialty = "Internal Medicine & Home Health",
                    appointmentType = "HOME_VISIT",
                    scheduledEpochMillis = cal.timeInMillis,
                    timeSlotString = "10:00 AM - 10:45 AM",
                    durationMinutes = 45,
                    status = "CONFIRMED",
                    reason = "Routine Home Checkup, Blood Pressure Assessment & Medication Review",
                    locationOrLink = "Home Visit - 742 Evergreen Way, Suite 4B",
                    doctorNotes = "Nursing team will bring portable EKG and vitals kit."
                ),
                AppointmentEntity(
                    patientId = "21001001",
                    doctorName = "Dr. Robert Chen, Cardiologist",
                    specialty = "Cardiovascular Medicine",
                    appointmentType = "VIDEO_CONSULTATION",
                    scheduledEpochMillis = cal2.timeInMillis,
                    timeSlotString = "02:30 PM - 03:00 PM",
                    durationMinutes = 30,
                    status = "CONFIRMED",
                    reason = "Follow-up review of Lipid panel & Atorvastatin dosage",
                    locationOrLink = "ANA Encrypted Telehealth Portal Room #882",
                    doctorNotes = "Please have past 7-day blood pressure logs ready."
                ),
                AppointmentEntity(
                    patientId = "21001001",
                    doctorName = "Nurse Emily Watson, RN",
                    specialty = "Home Nursing & Wound Care",
                    appointmentType = "HOME_VISIT",
                    scheduledEpochMillis = calPast.timeInMillis,
                    timeSlotString = "11:00 AM - 11:30 AM",
                    durationMinutes = 30,
                    status = "COMPLETED",
                    reason = "Post-procedure dressing check and vitals baseline",
                    locationOrLink = "Home Visit",
                    doctorNotes = "Patient healing smoothly. Normal vitals documented."
                )
            )
            appointmentDao.insertAll(appointments)

            // Seed Encrypted Messages
            val messages = listOf(
                EncryptedMessageEntity(
                    senderId = "1001",
                    senderName = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    senderRole = "DOCTOR",
                    receiverId = "21001001",
                    receiverName = "Eleanor Vance",
                    timestamp = now - 2 * day,
                    messageText = "Hello Eleanor, I reviewed your latest fasting metabolic panel. Your glucose and renal markers look very stable. Keep up the great daily walks!",
                    cipherTextDigest = SecurityManager.encrypt("Hello Eleanor, I reviewed your latest fasting metabolic panel."),
                    isEncrypted = true,
                    attachmentName = "CMP_Lab_Summary_Signed.pdf",
                    attachmentType = "PDF_REPORT",
                    attachmentSize = "420 KB",
                    isRead = true
                ),
                EncryptedMessageEntity(
                    senderId = "21001001",
                    senderName = "Eleanor Vance (ID: 21001001)",
                    senderRole = "PATIENT",
                    receiverId = "1001",
                    receiverName = "Dr. Sarah Jenkins, MD",
                    timestamp = now - 1 * day - 4 * hour,
                    messageText = "Thank you Dr. Jenkins! I had a slight headache on Wednesday after doing yard work, but blood pressure returned to 122/78 today after resting.",
                    cipherTextDigest = SecurityManager.encrypt("Thank you Dr. Jenkins! I had a slight headache on Wednesday..."),
                    isEncrypted = true,
                    attachmentName = null,
                    isRead = true
                ),
                EncryptedMessageEntity(
                    senderId = "1001",
                    senderName = "Dr. Sarah Jenkins, MD (ID: 1001)",
                    senderRole = "DOCTOR",
                    receiverId = "21001001",
                    receiverName = "Eleanor Vance",
                    timestamp = now - 5 * hour,
                    messageText = "Glad to hear that. I've scheduled our home healthcare checkup for this Friday at 10:00 AM. If any dizziness occurs beforehand, please message or call the hotline immediately.",
                    cipherTextDigest = SecurityManager.encrypt("Glad to hear that. I've scheduled our home healthcare checkup..."),
                    isEncrypted = true,
                    attachmentName = "CarePlan_Checkup_Guide.pdf",
                    attachmentType = "PDF_REPORT",
                    attachmentSize = "1.1 MB",
                    isRead = false
                )
            )
            encryptedMessageDao.insertAll(messages)

            // Seed Initial App Configuration (Admin layout & settings)
            val initialConfig = AppConfigEntity(
                configKey = "SYSTEM_CONFIG",
                showVitalsSummary = true,
                showMedicationSection = true,
                showDailyActivities = true,
                showLabResults = true,
                showEmergencyBanner = true,
                patientLayoutDensity = "CARD_EXPANDED",
                patientGreetingMessage = "Welcome to your ANA Care Health Portal",
                doctorCompactMode = false,
                doctorAutoExpandCriticals = true,
                doctorPrescriptionQuickBar = true,
                doctorHighlightCriticalVitals = true,
                appVersionName = "2.5.0-PROD",
                appBuildNumber = 250,
                updateReleaseNotes = "ANA Care Telemetry 2.5: Real-time clinical vital alert pop-ups and live admin layout engine.",
                isUpdateBannerVisible = true,
                isMaintenanceMode = false,
                maintenanceAnnouncement = "",
                systemThemeAccent = "TEAL_EMERALD",
                lastUpdatedTimestamp = now
            )
            appConfigDao.insertOrUpdateConfig(initialConfig)

            // Seed Initial Doctor & Admin Alert Notes (To demonstrate Pop-up feature and Telemetry Alerts)
            val initialAlertNotes = mutableListOf<PatientAlertNoteEntity>()
            initialAlertNotes.addAll(DummyDataSeeder.generateClinicalSeedAlerts(now))
            initialAlertNotes.add(
                PatientAlertNoteEntity(
                    targetPatientId = "21001001",
                    targetPatientName = "Eleanor Vance",
                    senderId = "1001",
                    senderName = "Dr. Sarah Jenkins, MD",
                    senderRole = "DOCTOR",
                    title = "Important: Fasting Notice for Tomorrow's Morning Labs",
                    message = "Please avoid eating or drinking (water only) after 10:00 PM tonight for the scheduled fasting lipid and metabolic panel. Keep taking your Lisinopril as usual with a sip of water.",
                    severity = "MEDICATION_ALERT",
                    timestamp = now - 30 * 60 * 1000L, // 30 mins ago
                    isAcknowledged = false,
                    actionLink = "LABS"
                )
            )
            initialAlertNotes.add(
                PatientAlertNoteEntity(
                    targetPatientId = "ALL",
                    targetPatientName = "All Patients",
                    senderId = "9001",
                    senderName = "Marcus Vance (System Admin)",
                    senderRole = "ADMIN",
                    title = "System Update: 24/7 Home Health Emergency Line Active",
                    message = "We have updated the ANA Care Emergency Dispatch network. The red Emergency Hotline button at the top header connects directly to local triage responders.",
                    severity = "INFO",
                    timestamp = now - 2 * hour,
                    isAcknowledged = false,
                    actionLink = "NONE"
                )
            )
            initialAlertNotes.forEach { patientAlertNoteDao.insertAlert(it) }

            // Seed Initial Clinical & Medical Gallery Items
            val initialGallery = listOf(
                MedicalGalleryEntity(
                    patientId = "21001001",
                    timestamp = now - 1 * day - 2 * hour,
                    title = "Lisinopril 10mg Prescription Bottle Label",
                    category = "Prescription / Rx",
                    imageUri = "sample_rx_label",
                    notes = "Pharmacy label scanned by caregiver verifying 30-day refill quantity and dosing instructions.",
                    loggedByRole = "CAREGIVER",
                    loggedByName = "James Vance (Caregiver)",
                    formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now - 1 * day - 2 * hour))
                ),
                MedicalGalleryEntity(
                    patientId = "21001001",
                    timestamp = now - 3 * day,
                    title = "Post-Op Knee Dressing Healing Progress",
                    category = "Wound & Clinical Photo",
                    imageUri = "sample_wound_healing",
                    notes = "Incision clean, dry, and intact with no erythema or exudate observed during morning dressing change.",
                    loggedByRole = "DOCTOR",
                    loggedByName = "Dr. Sarah Jenkins, MD",
                    formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now - 3 * day))
                ),
                MedicalGalleryEntity(
                    patientId = "21001001",
                    timestamp = now - 5 * day,
                    title = "Diagnostic Lipid & Metabolic Panel Printout",
                    category = "Lab & Diagnostic Report",
                    imageUri = "sample_lab_scan",
                    notes = "Official diagnostic laboratory scan from regional testing center for review.",
                    loggedByRole = "PATIENT",
                    loggedByName = "Eleanor Vance (Patient)",
                    formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now - 5 * day))
                ),
                MedicalGalleryEntity(
                    patientId = "21001001",
                    timestamp = now - 6 * day,
                    title = "Therapy Resistance Band Exercise Form",
                    category = "Physical Therapy / Mobility",
                    imageUri = "sample_therapy_form",
                    notes = "Mobility posture captured during home physical therapy session.",
                    loggedByRole = "CAREGIVER",
                    loggedByName = "James Vance (Caregiver)",
                    formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(now - 6 * day))
                )
            )
            medicalGalleryDao.insertAll(initialGallery)

            // Seed Initial Audit Logs
            val initialAuditLogs = DummyDataSeeder.generateInitialAuditLogs(now)
            auditLogDao.insertAll(initialAuditLogs)
        }
    }

    companion object {
        fun generateDoctorId(sequence: Int): String {
            return "1" + String.format(Locale.US, "%03d", sequence)
        }

        fun generatePatientId(doctorId: String, patientSequence: Int): String {
            val docDigits = doctorId.filter { it.isDigit() }.padStart(4, '0').takeLast(4)
            val patSeq = String.format(Locale.US, "%03d", patientSequence)
            return "2$docDigits$patSeq"
        }

        fun generateCaregiverId(sequence: Int): String {
            return "3" + String.format(Locale.US, "%03d", sequence)
        }
    }

    private fun classifyVitalStatus(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        glucose: Int,
        tempF: Float
    ): String {
        if (systolic >= 160 || diastolic >= 100 || spo2 < 92 || heartRate > 120 || tempF >= 101.5f || glucose > 220) {
            return "CRITICAL"
        }
        if (systolic >= 130 || diastolic >= 85 || spo2 < 95 || heartRate > 100 || tempF >= 99.5f || glucose > 140) {
            return "ELEVATED"
        }
        return "NORMAL"
    }
}
