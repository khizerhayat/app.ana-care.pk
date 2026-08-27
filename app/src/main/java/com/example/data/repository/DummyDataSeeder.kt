package com.example.data.repository

import com.example.data.local.entities.AppointmentEntity
import com.example.data.local.entities.DailyActivityEntity
import com.example.data.local.entities.LabResultEntity
import com.example.data.local.entities.MedicationEntity
import com.example.data.local.entities.PatientAlertNoteEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.data.local.entities.VitalSignEntity
import com.example.data.security.SecurityManager

object DummyDataSeeder {

    fun generateDoctors(): List<UserAccountEntity> {
        val doctorsData = listOf(
            Triple("1001", "Dr. Sarah Jenkins, MD", "Internal Medicine & Geriatrics") to ("dr.jenkins@anacare.org" to "ANA Care Home Healthcare HQ"),
            Triple("1002", "Dr. Robert Chen, MD", "Cardiology & Vascular Health") to ("dr.chen@anacare.org" to "Metro Heart & Vascular Pavilion"),
            Triple("1003", "Dr. Maria Rodriguez, MD", "Endocrinology & Diabetes Care") to ("dr.rodriguez@anacare.org" to "Advanced Diabetes & Metabolic Institute"),
            Triple("1004", "Dr. Anthony Patel, MD", "Pulmonology & Respiratory Medicine") to ("dr.patel@anacare.org" to "Regional Pulmonary Associates"),
            Triple("1005", "Dr. Emily Watson, MD", "Neurology & Cognitive Health") to ("dr.watson@anacare.org" to "Neuroscience & Memory Clinic"),
            Triple("1006", "Dr. David Kim, MD", "Nephrology & Renal Medicine") to ("dr.kim@anacare.org" to "Kidney Care & Dialysis Core"),
            Triple("1007", "Dr. Olivia Taylor, MD", "Rheumatology & Autoimmune Care") to ("dr.taylor@anacare.org" to "Arthritis & Autoimmune Center"),
            Triple("1008", "Dr. James Wilson, MD", "Family & Preventive Medicine") to ("dr.wilson@anacare.org" to "Community Health Network"),
            Triple("1009", "Dr. Sophia Martinez, MD", "Gastroenterology & Hepatology") to ("dr.martinez@anacare.org" to "Digestive Disease Specialty Clinic"),
            Triple("1010", "Dr. William Chang, MD", "Orthopedics & Joint Rehabilitation") to ("dr.chang@anacare.org" to "Orthopedic & Spine Institute")
        )

        return doctorsData.mapIndexed { idx, (docInfo, clinicInfo) ->
            val (id, name, specialty) = docInfo
            val (email, clinic) = clinicInfo
            val initials = name.replace("Dr. ", "").split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
            UserAccountEntity(
                userId = id,
                name = name,
                email = email,
                role = "MEDICAL_PROFESSIONAL",
                phone = "+1 (555) 440-${1120 + idx}",
                dateOfBirth = "March ${10 + idx}, 1978",
                gender = if (idx % 2 == 0) "Female" else "Male",
                bloodGroup = listOf("A+", "O+", "B+", "AB+")[idx % 4],
                specialty = specialty,
                licenseNumber = "MED-LIC-NY-${884920 + idx}",
                hospitalClinic = clinic,
                avatarInitials = if (initials.isNotBlank()) initials else "DR",
                biometricEnabled = true,
                mfaEnabled = true,
                isCurrentActive = false,
                isPrimaryPatient = false,
                assignedDoctorId = id,
                relationship = "Attending Physician"
            )
        }
    }

    fun generatePatients(doctors: List<UserAccountEntity>): List<UserAccountEntity> {
        val patientNames = listOf(
            "Pt. Eleanor Vance" to ("Female" to "May 14, 1958"),
            "Pt. Arthur Vance" to ("Male" to "November 03, 1955"),
            "Pt. Harold Finch" to ("Male" to "July 19, 1950"),
            "Pt. Evelyn Wright" to ("Female" to "April 12, 1961"),
            "Pt. George Harrison" to ("Male" to "February 25, 1953"),
            "Pt. Clara Oswald" to ("Female" to "September 18, 1967"),
            "Pt. Walter White" to ("Male" to "September 07, 1962"),
            "Pt. Dorothy Gale" to ("Female" to "June 10, 1954"),
            "Pt. Henry Higgins" to ("Male" to "March 05, 1957"),
            "Pt. Margaret Thatcher" to ("Female" to "October 13, 1946"),
            "Pt. Thomas Anderson" to ("Male" to "August 20, 1966"),
            "Pt. Beatrice Smith" to ("Female" to "January 29, 1952"),
            "Pt. Samuel Jackson" to ("Male" to "December 21, 1956"),
            "Pt. Florence Nightingale" to ("Female" to "May 12, 1951"),
            "Pt. Charles Darwin" to ("Male" to "February 12, 1953"),
            "Pt. Abigail Adams" to ("Female" to "November 11, 1964"),
            "Pt. Benjamin Franklin" to ("Male" to "January 17, 1949"),
            "Pt. Charlotte Bronte" to ("Female" to "April 21, 1966"),
            "Pt. Daniel Defoe" to ("Male" to "September 13, 1958"),
            "Pt. Elizabeth Bennet" to ("Female" to "December 16, 1962"),
            "Pt. Francis Drake" to ("Male" to "July 13, 1954"),
            "Pt. Grace Hopper" to ("Female" to "December 09, 1944"),
            "Pt. Isaac Newton" to ("Male" to "January 04, 1948"),
            "Pt. Jane Austen" to ("Female" to "December 16, 1968"),
            "Pt. Leonardo da Vinci" to ("Male" to "April 15, 1951"),
            "Pt. Marie Curie" to ("Female" to "November 07, 1953"),
            "Pt. Nikola Tesla" to ("Male" to "July 10, 1956"),
            "Pt. Octavia Butler" to ("Female" to "June 22, 1963"),
            "Pt. Paul Revere" to ("Male" to "January 01, 1952"),
            "Pt. Queen Victoria" to ("Female" to "May 24, 1941"),
            "Pt. Rosa Parks" to ("Female" to "February 04, 1947"),
            "Pt. Stephen Hawking" to ("Male" to "January 08, 1950"),
            "Pt. Teresa of Calcutta" to ("Female" to "August 26, 1943"),
            "Pt. Ulysses Grant" to ("Male" to "April 27, 1957"),
            "Pt. Virginia Woolf" to ("Female" to "January 25, 1965"),
            "Pt. Winston Churchill" to ("Male" to "November 30, 1945"),
            "Pt. Xena Warrior" to ("Female" to "March 29, 1970"),
            "Pt. Yvonne Strahovski" to ("Female" to "July 30, 1966"),
            "Pt. Zachary Taylor" to ("Male" to "November 24, 1953"),
            "Pt. Amelia Earhart" to ("Female" to "July 24, 1960"),
            "Pt. Bruce Wayne" to ("Male" to "May 27, 1971"),
            "Pt. Catherine Great" to ("Female" to "May 02, 1949"),
            "Pt. Douglas Adams" to ("Male" to "March 11, 1962"),
            "Pt. Eleanor Roosevelt" to ("Female" to "October 11, 1946"),
            "Pt. Galileo Galilei" to ("Male" to "February 15, 1947"),
            "Pt. Harriet Tubman" to ("Female" to "March 10, 1942"),
            "Pt. Ian Fleming" to ("Male" to "May 28, 1959"),
            "Pt. Joan of Arc" to ("Female" to "January 06, 1968"),
            "Pt. Karl Marx" to ("Male" to "May 05, 1954"),
            "Pt. Louisa Alcott" to ("Female" to "November 29, 1961")
        )

        val bloodGroups = listOf("O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-")
        val allergiesList = listOf(
            "Penicillin, Shellfish", "Sulfa Drugs", "Aspirin, Codeine", "Latex, Peanuts",
            "None Known", "Amoxicillin, NSAIDs", "Iodine Contrast", "None", "Cephalosporins",
            "Morphine, Adhesive Tape", "None Known", "Ciprofloxacin"
        )
        val diagnoses = listOf(
            "Essential Hypertension, Type-2 Diabetes Mellitus",
            "Osteoarthritis (Bilateral Knees), Mild Hypertension",
            "Coronary Artery Disease, Dyslipidemia",
            "Atrial Fibrillation, Hypercholesterolemia",
            "Congestive Heart Failure (Class II), Hypertension",
            "Type-1 Diabetes, Hashimoto's Thyroiditis",
            "Diabetic Peripheral Neuropathy, Hyperlipidemia",
            "Chronic Obstructive Pulmonary Disease (COPD Stage II)",
            "Severe Persistent Asthma, Obstructive Sleep Apnea",
            "Parkinson's Disease (Early Stage), Mild Cognitive Impairment",
            "Chronic Kidney Disease Stage 3a, Hypertension",
            "Rheumatoid Arthritis, Osteopenia",
            "Crohn's Disease, Gastroesophageal Reflux Disease",
            "Lumbar Spinal Stenosis, Chronic Lower Back Pain",
            "Hypothyroidism, Essential Tremor"
        )

        val insurances = listOf(
            "BlueCross BlueShield Senior Care" to "BCBS-9948201",
            "Medicare Advantage Plan D" to "MED-771029-B",
            "United Healthcare Senior Option" to "UHC-8839102",
            "Aetna Medicare Elite" to "AET-992140",
            "Humana Gold Plus HMO" to "HUM-552019",
            "Cigna HealthSpring" to "CIG-330192",
            "Kaiser Permanente Senior" to "KP-882104"
        )

        return patientNames.mapIndexed { idx, (fullName, genderDob) ->
            val (gender, dob) = genderDob
            val patientId = "210010${String.format("%02d", idx + 1)}"
            // First 12 patients assigned to Dr. Sarah Jenkins (1001), others distributed across doctors 1002..1010
            val assignedDoctor = if (idx < 12) {
                doctors.first() // Dr. Sarah Jenkins (1001)
            } else {
                doctors[1 + ((idx - 12) % (doctors.size - 1))]
            }
            val (insName, insPrefix) = insurances[idx % insurances.size]
            val initials = fullName.replace("Pt. ", "").split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
            val ageVal = (2026 - dob.takeLast(4).toInt()).toString()

            UserAccountEntity(
                userId = patientId,
                name = fullName,
                email = "${fullName.replace("Pt. ", "").lowercase().replace(" ", ".")}@example.com",
                role = "PATIENT",
                phone = "+1 (555) 234-${String.format("%04d", 5678 + idx)}",
                age = ageVal,
                dateOfBirth = dob,
                gender = gender,
                bloodGroup = bloodGroups[idx % bloodGroups.size],
                allergies = allergiesList[idx % allergiesList.size],
                diagnosis = diagnoses[idx % diagnoses.size],
                emergencyContact = "Family Contact - (555) ${100 + idx}-${2000 + idx}",
                insuranceProvider = insName,
                insurancePolicyNo = "$insPrefix-${1000 + idx}",
                avatarInitials = if (initials.isNotBlank()) initials else "PT",
                biometricEnabled = true,
                mfaEnabled = idx % 2 == 0,
                isCurrentActive = (idx == 0),
                isPrimaryPatient = (idx == 0),
                assignedDoctorId = assignedDoctor.userId,
                relationship = if (idx == 0) "Self (Primary Patient)" else "Patient"
            )
        }
    }

    /**
     * Generates caregivers randomly linked to patients:
     * - idx % 4 == 0 -> 0 caregivers
     * - idx % 4 == 1 -> 1 caregiver
     * - idx % 4 == 2 -> 2 caregivers
     * - idx % 4 == 3 -> 3 caregivers
     */
    fun generateCaregivers(patients: List<UserAccountEntity>, doctors: List<UserAccountEntity>): List<UserAccountEntity> {
        val caregivers = mutableListOf<UserAccountEntity>()
        var caregiverSeq = 3001

        // Guaranteed Primary Caregiver for Eleanor Vance (21001001)
        val primaryPatient = patients.firstOrNull { it.userId == "21001001" } ?: patients.firstOrNull()
        if (primaryPatient != null) {
            caregivers.add(
                UserAccountEntity(
                    userId = "3000",
                    name = "CG. James Vance",
                    email = "james.vance@example.com",
                    role = "CAREGIVER",
                    phone = "+1 (555) 987-6540",
                    dateOfBirth = "August 14, 1982",
                    gender = "Male",
                    bloodGroup = "O+",
                    allergies = "None",
                    emergencyContact = "${primaryPatient.name} - ${primaryPatient.phone}",
                    insuranceProvider = "Aetna Health Plus",
                    insurancePolicyNo = "AET-330192-3000",
                    avatarInitials = "JV",
                    biometricEnabled = true,
                    mfaEnabled = true,
                    isCurrentActive = false,
                    isPrimaryPatient = false,
                    caregiverPermissions = "FULL_ACCESS",
                    assignedPatientId = primaryPatient.userId,
                    assignedDoctorId = primaryPatient.assignedDoctorId,
                    relationship = "Son / Primary Caregiver"
                )
            )
        }

        val caregiverFirstNames = listOf(
            "James", "Mary", "Robert", "Patricia", "John", "Jennifer", "Michael", "Linda",
            "David", "Elizabeth", "William", "Barbara", "Richard", "Susan", "Joseph", "Jessica",
            "Thomas", "Sarah", "Charles", "Karen", "Christopher", "Nancy", "Daniel", "Lisa",
            "Matthew", "Betty", "Anthony", "Margaret", "Mark", "Sandra", "Donald", "Ashley",
            "Steven", "Kimberly", "Paul", "Emily", "Andrew", "Donna", "Joshua", "Michelle",
            "Kenneth", "Dorothy", "Kevin", "Carol", "Brian", "Amanda", "George", "Melissa",
            "Edward", "Deborah", "Ronald", "Stephanie", "Timothy", "Rebecca", "Jason", "Sharon"
        )

        val caregiverRoles = listOf(
            "Son / Primary Caregiver" to "FULL_ACCESS",
            "Daughter / Care Circle" to "FULL_ACCESS",
            "Spouse / Legal Guardian" to "FULL_ACCESS",
            "Authorized Home Health Aide" to "VIEW_ONLY",
            "Sister / Family Contact" to "VIEW_ONLY",
            "Emergency Caregiver Contact" to "EMERGENCY_ONLY"
        )

        patients.forEachIndexed { pIdx, patient ->
            val numCaregivers = pIdx % 4 // 0, 1, 2, or 3
            val patLastName = patient.name.replace("Pt. ", "").split(" ").lastOrNull() ?: "Family"

            for (cIdx in 0 until numCaregivers) {
                val cgId = (caregiverSeq++).toString()
                val cgFirstName = caregiverFirstNames[(pIdx * 3 + cIdx) % caregiverFirstNames.size]
                val cgName = "CG. $cgFirstName $patLastName"
                val (relation, perm) = caregiverRoles[(pIdx + cIdx) % caregiverRoles.size]
                val initials = "${cgFirstName.first()}${patLastName.first()}"

                caregivers.add(
                    UserAccountEntity(
                        userId = cgId,
                        name = cgName,
                        email = "${cgFirstName.lowercase()}.${patLastName.lowercase()}@caregiver.org",
                        role = "CAREGIVER",
                        phone = "+1 (555) 987-${String.format("%04d", 6540 + caregivers.size)}",
                        dateOfBirth = "August ${10 + (pIdx % 15)}, ${1980 + (cIdx * 3)}",
                        gender = if (cIdx % 2 == 0) "Male" else "Female",
                        bloodGroup = "O+",
                        allergies = "None",
                        emergencyContact = "${patient.name} - ${patient.phone}",
                        insuranceProvider = "Aetna Health Plus",
                        insurancePolicyNo = "AET-330192-$cgId",
                        avatarInitials = initials,
                        biometricEnabled = true,
                        mfaEnabled = true,
                        isCurrentActive = false,
                        isPrimaryPatient = false,
                        caregiverPermissions = perm,
                        assignedPatientId = patient.userId,
                        assignedDoctorId = patient.assignedDoctorId,
                        relationship = relation
                    )
                )
            }
        }

        return caregivers
    }

    fun generateAdminProfile(): UserAccountEntity {
        return UserAccountEntity(
            userId = "9001",
            name = "Admin. Marcus Vance",
            email = "admin@anacare.org",
            role = "ADMIN",
            phone = "+1 (555) 019-2831",
            dateOfBirth = "January 20, 1985",
            gender = "Male",
            bloodGroup = "O+",
            avatarInitials = "MV",
            biometricEnabled = true,
            mfaEnabled = true,
            isCurrentActive = false,
            isPrimaryPatient = false,
            relationship = "System Security Administrator"
        )
    }

    fun generateClinicalSeedVitals(now: Long, patients: List<UserAccountEntity>): List<VitalSignEntity> {
        val hour = 3600 * 1000L
        val day = 24 * hour
        val vitalsList = mutableListOf<VitalSignEntity>()

        // Specific tailored baseline for Eleanor Vance (21001001)
        vitalsList.add(
            VitalSignEntity(
                patientId = "21001001",
                timestamp = now - 2 * hour,
                systolicBp = 122,
                diastolicBp = 78,
                heartRate = 72,
                oxygenSaturation = 98,
                temperatureF = 98.4f,
                bloodGlucose = 108,
                respiratoryRate = 16,
                weightLbs = 142.5f,
                notes = "Morning routine after breakfast. Feeling energetic.",
                status = "NORMAL",
                measuredBy = "Self",
                encryptionHash = SecurityManager.generateRecordDigest("21001001-122/78-72")
            )
        )
        // Specific tailored baseline for Arthur Vance (21001002) - with elevated BP alert
        vitalsList.add(
            VitalSignEntity(
                patientId = "21001002",
                timestamp = now - 1 * hour,
                systolicBp = 138,
                diastolicBp = 88,
                heartRate = 84,
                oxygenSaturation = 96,
                temperatureF = 98.7f,
                bloodGlucose = 118,
                respiratoryRate = 18,
                weightLbs = 175.4f,
                notes = "Afternoon check. Fluctuation recorded after mild exertion.",
                status = "ELEVATED",
                measuredBy = "CG. Mary Vance",
                encryptionHash = SecurityManager.generateRecordDigest("21001002-138/88-84")
            )
        )

        // Generate baseline for all other patients (from index 2 to end)
        patients.drop(2).forEachIndexed { idx, pat ->
            val isElevated = idx % 5 == 0
            val isCritical = idx % 19 == 0
            val sys = if (isCritical) 162 else if (isElevated) 142 else (118 + (idx % 14))
            val dia = if (isCritical) 98 else if (isElevated) 90 else (74 + (idx % 10))
            val glucose = if (isCritical) 195 else if (isElevated) 148 else (95 + (idx % 28))
            val hr = 68 + (idx % 22)
            val o2 = 95 + (idx % 5)
            val temp = 98.2f + ((idx % 8) * 0.1f)
            val status = if (isCritical) "CRITICAL" else if (isElevated) "ELEVATED" else "NORMAL"

            vitalsList.add(
                VitalSignEntity(
                    patientId = pat.userId,
                    timestamp = now - (idx + 1) * 3 * hour,
                    systolicBp = sys,
                    diastolicBp = dia,
                    heartRate = hr,
                    oxygenSaturation = o2,
                    temperatureF = temp,
                    bloodGlucose = glucose,
                    respiratoryRate = 16 + (idx % 4),
                    weightLbs = 135f + (idx % 50),
                    notes = "Routine telemetry vital sync for ${pat.name}.",
                    status = status,
                    measuredBy = "ANA Care Telemetry Hub",
                    encryptionHash = SecurityManager.generateRecordDigest("${pat.userId}-$sys/$dia-$hr")
                )
            )
        }

        return vitalsList
    }

    fun generateClinicalSeedMedications(now: Long): List<MedicationEntity> {
        val hour = 3600 * 1000L
        val day = 24 * hour

        return listOf(
            // Eleanor Vance (21001001)
            MedicationEntity(
                patientId = "21001001",
                name = "Lisinopril",
                dosage = "20 mg",
                frequency = "Once daily",
                route = "Oral",
                scheduledTime = "08:00 AM",
                instructions = "Take in morning with a full glass of water. Monitor blood pressure.",
                prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                category = "Cardiovascular / Blood Pressure",
                isTakenToday = true,
                refillsRemaining = 4,
                status = "RUNNING",
                lastAction = "TAKEN",
                lastActionTimestamp = now - 2 * hour,
                lastActionDateFormatted = "Today • 08:05 AM"
            ),
            MedicationEntity(
                patientId = "21001001",
                name = "Metformin HCl ER",
                dosage = "500 mg",
                frequency = "Twice daily",
                route = "Oral",
                scheduledTime = "08:00 AM & 08:00 PM",
                instructions = "Take with meals to prevent GI upset.",
                prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                category = "Endocrine / Diabetes",
                isTakenToday = true,
                refillsRemaining = 2,
                status = "RUNNING",
                lastAction = "TAKEN",
                lastActionTimestamp = now - 2 * hour,
                lastActionDateFormatted = "Today • 08:06 AM"
            ),
            MedicationEntity(
                patientId = "21001001",
                name = "Atorvastatin Calcium",
                dosage = "20 mg",
                frequency = "Once daily at bedtime",
                route = "Oral",
                scheduledTime = "09:00 PM",
                instructions = "Take in evening. Avoid grapefruit juice.",
                prescribedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                category = "Lipid Lowering / Cholesterol",
                isTakenToday = false,
                refillsRemaining = 5,
                status = "RUNNING",
                lastAction = "",
                lastActionTimestamp = 0L,
                lastActionDateFormatted = ""
            ),
            // Arthur Vance (21001002)
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
            // Harold Finch (21001003)
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
            )
        )
    }

    fun generateClinicalSeedActivities(now: Long): List<DailyActivityEntity> {
        val hour = 3600 * 1000L
        val day = 24 * hour

        return listOf(
            DailyActivityEntity(
                patientId = "21001001",
                timestamp = now - 3 * hour,
                activityType = "Walking / Steps",
                durationMinutes = 25,
                metricValue = "2,840 steps",
                painScore = 1,
                mood = "Energetic",
                notes = "Morning neighborhood stroll with walking poles. No dyspnea.",
                loggedBy = "Self"
            ),
            DailyActivityEntity(
                patientId = "21001001",
                timestamp = now - 1 * day,
                activityType = "Sleep & Rest",
                durationMinutes = 480,
                metricValue = "8.0 hours sleep",
                painScore = 0,
                mood = "Refreshed",
                notes = "Uninterrupted deep sleep.",
                loggedBy = "Self"
            ),
            DailyActivityEntity(
                patientId = "21001002",
                timestamp = now - 2 * hour,
                activityType = "Walking / Steps",
                durationMinutes = 20,
                metricValue = "2,100 steps",
                painScore = 2,
                mood = "Good",
                notes = "Slow garden walk with cane support.",
                loggedBy = "CG. Mary Vance"
            ),
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
            )
        )
    }

    fun generateClinicalSeedLabs(now: Long): List<LabResultEntity> {
        val day = 24 * 3600 * 1000L

        return listOf(
            LabResultEntity(
                patientId = "21001001",
                testName = "Comprehensive Metabolic Panel (CMP-14)",
                category = "Biochemistry",
                datePerformed = now - 5 * day,
                orderedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                facility = "ANA Care Regional Diagnostic Core",
                status = "NORMAL",
                summary = "All liver enzymes, electrolytes, and kidney markers within normal standard clinical range.",
                keyParameters = "• Sodium: 140 mEq/L (Normal 135-145)\n• Potassium: 4.2 mEq/L (Normal 3.5-5.0)\n• eGFR: 88 mL/min/1.73m² (Normal > 60)\n• BUN: 15 mg/dL (Normal 7-20)\n• Creatinine: 0.85 mg/dL (Normal 0.6-1.2)",
                doctorNotes = "Metabolic parameters are stable. Continue current Lisinopril therapy."
            ),
            LabResultEntity(
                patientId = "21001001",
                testName = "Lipid & Cardiovascular Panel",
                category = "Cardiovascular",
                datePerformed = now - 12 * day,
                orderedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                facility = "ANA Care Regional Diagnostic Core",
                status = "NORMAL",
                summary = "Mild elevation in LDL cholesterol; HDL and Triglycerides optimal.",
                keyParameters = "• Total Cholesterol: 198 mg/dL (Desirable < 200)\n• HDL Cholesterol: 58 mg/dL (Optimal > 50)\n• LDL Cholesterol: 118 mg/dL (Elevated > 100)\n• Triglycerides: 110 mg/dL (Normal < 150)",
                doctorNotes = "Atorvastatin 20mg continuing. Recommended low-saturated-fat home meal plans."
            ),
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
            LabResultEntity(
                patientId = "21001003",
                testName = "HbA1c & Fasting Insulin Panel",
                category = "Endocrine / Diabetes",
                datePerformed = now - 3 * day,
                orderedBy = "Dr. Sarah Jenkins, MD (ID: 1001)",
                facility = "ANA Care Regional Diagnostic Core",
                status = "ELEVATED",
                summary = "Elevated HbA1c 7.6%. Glycemic control requires titration of medication.",
                keyParameters = "• HbA1c: 7.6% (Target < 7.0%)\n• Fasting Glucose: 154 mg/dL (Normal 70-99)\n• C-Peptide: 2.1 ng/mL (Normal 1.1-4.4)",
                doctorNotes = "Recommended adjustment of antidiabetic regimen and nutritional counseling."
            )
        )
    }

    fun generateClinicalSeedAppointments(now: Long, doctors: List<UserAccountEntity>, patients: List<UserAccountEntity>): List<AppointmentEntity> {
        val day = 24 * 3600 * 1000L
        val appointments = mutableListOf<AppointmentEntity>()

        // Seed appointments for first few patients with their assigned doctors
        patients.take(15).forEachIndexed { idx, pat ->
            val doc = doctors.find { it.userId == pat.assignedDoctorId } ?: doctors.first()
            appointments.add(
                AppointmentEntity(
                    patientId = pat.userId,
                    doctorName = doc.name,
                    specialty = doc.specialty,
                    appointmentType = if (idx % 2 == 0) "CLINIC_VISIT" else "HOME_VISIT",
                    scheduledEpochMillis = now + (idx + 1) * day,
                    timeSlotString = if (idx % 2 == 0) "10:30 AM - 11:00 AM" else "02:15 PM - 02:45 PM",
                    durationMinutes = 30,
                    status = if (idx % 3 == 0) "CONFIRMED" else "SCHEDULED",
                    reason = "Routine clinical review & medication titration",
                    locationOrLink = doc.hospitalClinic,
                    doctorNotes = "Review vital logs with ${doc.name}."
                )
            )
        }

        return appointments
    }

    fun generateClinicalSeedAlerts(now: Long): List<PatientAlertNoteEntity> {
        val hour = 3600 * 1000L
        return listOf(
            PatientAlertNoteEntity(
                senderRole = "SYSTEM_TELEMETRY",
                senderName = "ANA Care Clinical Telemetry Core",
                senderId = "1001",
                targetPatientId = "21001002",
                targetPatientName = "Pt. Arthur Vance",
                title = "Telemetry Alert: BP Fluctuation Detected",
                message = "BP fluctuation on Pt. Arthur (ID: 21001002). Systolic blood pressure registered 138/88 mmHg. Physician review recommended.",
                severity = "WARNING",
                actionLink = "VITALS",
                timestamp = now - 1 * hour,
                isAcknowledged = false
            ),
            PatientAlertNoteEntity(
                senderRole = "DOCTOR",
                senderName = "Dr. Sarah Jenkins, MD",
                senderId = "1001",
                targetPatientId = "21001001",
                targetPatientName = "Pt. Eleanor Vance",
                title = "Clinical Guidance: Morning Hydration & Lisinopril",
                message = "Please remember to take Lisinopril with a full glass of water and log your blood pressure 30 minutes post-dose.",
                severity = "INFO",
                actionLink = "MEDICATIONS",
                timestamp = now - 2 * hour,
                isAcknowledged = true
            )
        )
    }
}
