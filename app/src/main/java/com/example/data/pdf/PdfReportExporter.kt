package com.example.data.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.entities.DailyActivityEntity
import com.example.data.local.entities.MedicationEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.data.local.entities.VitalSignEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportExporter {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    fun exportVitalsPdf(
        context: Context,
        patient: UserAccountEntity,
        vitalsList: List<VitalSignEntity>
    ): File? {
        return try {
            val doc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (595 x 842 pt)
            val page = doc.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint().apply { isAntiAlias = true }

            // Header Background Bar
            paint.color = Color.parseColor("#0E2246")
            canvas.drawRect(0f, 0f, 595f, 90f, paint)

            // Header Brand Title
            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            paint.textSize = 20f
            canvas.drawText("ANA CARE", 30f, 42f, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 10f
            paint.color = Color.parseColor("#93C5FD")
            canvas.drawText("HOME HEALTHCARE SERVICE PROVIDER • PATIENT PORTAL", 30f, 60f, paint)

            paint.textSize = 12f
            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("VITAL SIGNS REPORT", 565f, 45f, paint)
            paint.textSize = 9f
            canvas.drawText("Generated: ${dateOnlyFormat.format(Date())}", 565f, 62f, paint)
            paint.textAlign = Paint.Align.LEFT

            // Patient Info Box
            var yPos = 120f
            paint.color = Color.parseColor("#F1F5F9")
            canvas.drawRoundRect(30f, yPos - 15f, 565f, yPos + 55f, 8f, 8f, paint)

            paint.color = Color.parseColor("#0F172A")
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 12f
            canvas.drawText("Patient: ${patient.name}", 45f, yPos + 8f, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 10f
            paint.color = Color.parseColor("#475569")
            canvas.drawText("DOB: ${patient.dateOfBirth.ifEmpty { "N/A" }}  |  Gender: ${patient.gender.ifEmpty { "N/A" }}  |  Blood: ${patient.bloodGroup.ifEmpty { "N/A" }}", 45f, yPos + 26f, paint)
            canvas.drawText("Primary Care / Caregiver: ${patient.relationship.ifEmpty { "Self Managed" }}  |  Emergency: ${patient.emergencyContact.ifEmpty { "N/A" }}", 45f, yPos + 42f, paint)

            // Table Header
            yPos += 85f
            paint.color = Color.parseColor("#1E3A5F")
            canvas.drawRect(30f, yPos - 12f, 565f, yPos + 16f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 9f
            canvas.drawText("DATE & TIME", 36f, yPos + 6f, paint)
            canvas.drawText("BLOOD PRESSURE", 145f, yPos + 6f, paint)
            canvas.drawText("HEART RATE", 250f, yPos + 6f, paint)
            canvas.drawText("SpO2", 325f, yPos + 6f, paint)
            canvas.drawText("TEMP", 370f, yPos + 6f, paint)
            canvas.drawText("GLUCOSE", 420f, yPos + 6f, paint)
            canvas.drawText("STATUS", 495f, yPos + 6f, paint)

            // Rows
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 8.5f
            val maxRows = minOf(vitalsList.size, 18)

            for (i in 0 until maxRows) {
                val v = vitalsList[i]
                yPos += 24f

                // Row alternate background
                if (i % 2 == 0) {
                    paint.color = Color.parseColor("#F8FAFC")
                    canvas.drawRect(30f, yPos - 12f, 565f, yPos + 12f, paint)
                }

                paint.color = Color.parseColor("#334155")
                canvas.drawText(dateFormat.format(Date(v.timestamp)), 36f, yPos + 2f, paint)
                canvas.drawText("${v.systolicBp}/${v.diastolicBp} mmHg", 145f, yPos + 2f, paint)
                canvas.drawText("${v.heartRate} bpm", 250f, yPos + 2f, paint)
                canvas.drawText("${v.oxygenSaturation}%", 325f, yPos + 2f, paint)
                canvas.drawText("${v.temperatureF}°F", 370f, yPos + 2f, paint)
                canvas.drawText("${v.bloodGlucose} mg/dL", 420f, yPos + 2f, paint)

                if (v.status == "NORMAL") {
                    paint.color = Color.parseColor("#059669")
                    canvas.drawText("Normal", 495f, yPos + 2f, paint)
                } else if (v.status == "ELEVATED") {
                    paint.color = Color.parseColor("#D97706")
                    canvas.drawText("Elevated", 495f, yPos + 2f, paint)
                } else {
                    paint.color = Color.parseColor("#DC2626")
                    canvas.drawText("Critical", 495f, yPos + 2f, paint)
                }
            }

            // Footer & Verification
            paint.color = Color.parseColor("#94A3B8")
            paint.textSize = 8f
            canvas.drawLine(30f, 770f, 565f, 770f, paint)
            canvas.drawText("ANA Care Encrypted Record • Confidential Medical Document • End-to-End Verified", 30f, 788f, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Page 1 of 1", 565f, 788f, paint)

            doc.finishPage(page)

            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()
            val file = File(reportsDir, "ANA_Care_Vitals_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(file)
            doc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            doc.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportMedicationsPdf(
        context: Context,
        patient: UserAccountEntity,
        medsList: List<MedicationEntity>
    ): File? {
        return try {
            val doc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = doc.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint().apply { isAntiAlias = true }

            // Header
            paint.color = Color.parseColor("#0E2246")
            canvas.drawRect(0f, 0f, 595f, 90f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            paint.textSize = 20f
            canvas.drawText("ANA CARE", 30f, 42f, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 10f
            paint.color = Color.parseColor("#93C5FD")
            canvas.drawText("HOME HEALTHCARE SERVICE PROVIDER • PRESCRIPTION SCHEDULE", 30f, 60f, paint)

            paint.textSize = 12f
            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("DAILY MEDICATION RECORDS", 565f, 45f, paint)
            paint.textSize = 9f
            canvas.drawText("Generated: ${dateOnlyFormat.format(Date())}", 565f, 62f, paint)
            paint.textAlign = Paint.Align.LEFT

            // Patient Info Box
            var yPos = 120f
            paint.color = Color.parseColor("#F1F5F9")
            canvas.drawRoundRect(30f, yPos - 15f, 565f, yPos + 55f, 8f, 8f, paint)

            paint.color = Color.parseColor("#0F172A")
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 12f
            canvas.drawText("Patient: ${patient.name}", 45f, yPos + 8f, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 10f
            paint.color = Color.parseColor("#475569")
            canvas.drawText("Allergies: ${patient.allergies.ifEmpty { "No known drug allergies (NKDA)" }}  |  Insurance: ${patient.insuranceProvider.ifEmpty { "N/A" }}", 45f, yPos + 26f, paint)
            canvas.drawText("Active Prescriptions: ${medsList.size}  |  Emergency Contact: ${patient.emergencyContact.ifEmpty { "N/A" }}", 45f, yPos + 42f, paint)

            // Table Header
            yPos += 85f
            paint.color = Color.parseColor("#1E3A5F")
            canvas.drawRect(30f, yPos - 12f, 565f, yPos + 16f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 9f
            canvas.drawText("MEDICATION & DOSAGE", 36f, yPos + 6f, paint)
            canvas.drawText("FREQUENCY & ROUTE", 185f, yPos + 6f, paint)
            canvas.drawText("TIME", 310f, yPos + 6f, paint)
            canvas.drawText("PRESCRIBED BY", 370f, yPos + 6f, paint)
            canvas.drawText("INSTRUCTIONS", 465f, yPos + 6f, paint)

            // Rows
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 8.5f
            val maxRows = minOf(medsList.size, 16)

            for (i in 0 until maxRows) {
                val med = medsList[i]
                yPos += 28f

                if (i % 2 == 0) {
                    paint.color = Color.parseColor("#F8FAFC")
                    canvas.drawRect(30f, yPos - 14f, 565f, yPos + 14f, paint)
                }

                paint.color = Color.parseColor("#0F172A")
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                canvas.drawText(med.name, 36f, yPos - 2f, paint)
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                paint.color = Color.parseColor("#64748B")
                paint.textSize = 7.5f
                canvas.drawText(med.dosage, 36f, yPos + 8f, paint)

                paint.textSize = 8.5f
                paint.color = Color.parseColor("#334155")
                canvas.drawText("${med.frequency} (${med.route})", 185f, yPos + 2f, paint)
                canvas.drawText(med.scheduledTime, 310f, yPos + 2f, paint)
                canvas.drawText(med.prescribedBy.ifEmpty { "ANA Care MD" }, 370f, yPos + 2f, paint)
                
                val instr = if (med.instructions.length > 20) med.instructions.take(20) + "..." else med.instructions.ifEmpty { "Take as directed" }
                canvas.drawText(instr, 465f, yPos + 2f, paint)
            }

            // Footer
            paint.color = Color.parseColor("#94A3B8")
            paint.textSize = 8f
            canvas.drawLine(30f, 770f, 565f, 770f, paint)
            canvas.drawText("ANA Care Encrypted Medication Protocol • Confidential Medical Document", 30f, 788f, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Page 1 of 1", 565f, 788f, paint)

            doc.finishPage(page)

            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()
            val file = File(reportsDir, "ANA_Care_Medications_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(file)
            doc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            doc.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportActivitiesPdf(
        context: Context,
        patient: UserAccountEntity,
        activitiesList: List<DailyActivityEntity>
    ): File? {
        return try {
            val doc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = doc.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint().apply { isAntiAlias = true }

            // Header
            paint.color = Color.parseColor("#0E2246")
            canvas.drawRect(0f, 0f, 595f, 90f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            paint.textSize = 20f
            canvas.drawText("ANA CARE", 30f, 42f, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 10f
            paint.color = Color.parseColor("#93C5FD")
            canvas.drawText("HOME HEALTHCARE SERVICE PROVIDER • DAILY WELLNESS LOG", 30f, 60f, paint)

            paint.textSize = 12f
            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("DAILY ACTIVITIES REPORT", 565f, 45f, paint)
            paint.textSize = 9f
            canvas.drawText("Generated: ${dateOnlyFormat.format(Date())}", 565f, 62f, paint)
            paint.textAlign = Paint.Align.LEFT

            // Patient Info Box
            var yPos = 120f
            paint.color = Color.parseColor("#F1F5F9")
            canvas.drawRoundRect(30f, yPos - 15f, 565f, yPos + 55f, 8f, 8f, paint)

            paint.color = Color.parseColor("#0F172A")
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 12f
            canvas.drawText("Patient: ${patient.name}", 45f, yPos + 8f, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 10f
            paint.color = Color.parseColor("#475569")
            canvas.drawText("Logged Activities: ${activitiesList.size} sessions  |  Caregiver: ${patient.relationship.ifEmpty { "Self Logged" }}", 45f, yPos + 26f, paint)
            canvas.drawText("Emergency Contact: ${patient.emergencyContact.ifEmpty { "N/A" }}  |  Portal Account ID: ${patient.userId}", 45f, yPos + 42f, paint)

            // Table Header
            yPos += 85f
            paint.color = Color.parseColor("#1E3A5F")
            canvas.drawRect(30f, yPos - 12f, 565f, yPos + 16f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 9f
            canvas.drawText("DATE & TIME", 36f, yPos + 6f, paint)
            canvas.drawText("ACTIVITY TYPE", 145f, yPos + 6f, paint)
            canvas.drawText("METRIC / DURATION", 260f, yPos + 6f, paint)
            canvas.drawText("PAIN (0-10)", 380f, yPos + 6f, paint)
            canvas.drawText("MOOD", 445f, yPos + 6f, paint)
            canvas.drawText("NOTES", 500f, yPos + 6f, paint)

            // Rows
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 8.5f
            val maxRows = minOf(activitiesList.size, 18)

            for (i in 0 until maxRows) {
                val act = activitiesList[i]
                yPos += 24f

                if (i % 2 == 0) {
                    paint.color = Color.parseColor("#F8FAFC")
                    canvas.drawRect(30f, yPos - 12f, 565f, yPos + 12f, paint)
                }

                paint.color = Color.parseColor("#334155")
                canvas.drawText(dateFormat.format(Date(act.timestamp)), 36f, yPos + 2f, paint)
                canvas.drawText(act.activityType, 145f, yPos + 2f, paint)
                canvas.drawText("${act.metricValue} (${act.durationMinutes}m)", 260f, yPos + 2f, paint)
                canvas.drawText("${act.painScore}/10", 380f, yPos + 2f, paint)
                canvas.drawText(act.mood, 445f, yPos + 2f, paint)

                val note = if (act.notes.length > 15) act.notes.take(15) + "..." else act.notes.ifEmpty { "-" }
                canvas.drawText(note, 500f, yPos + 2f, paint)
            }

            // Footer
            paint.color = Color.parseColor("#94A3B8")
            paint.textSize = 8f
            canvas.drawLine(30f, 770f, 565f, 770f, paint)
            canvas.drawText("ANA Care Activity & Physical Therapy Log • Confidential Document", 30f, 788f, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Page 1 of 1", 565f, 788f, paint)

            doc.finishPage(page)

            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()
            val file = File(reportsDir, "ANA_Care_Activities_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(file)
            doc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            doc.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun sharePdf(context: Context, file: File, title: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Here is the exported health record from ANA Care Patient Portal.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Export / Share PDF Report"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
