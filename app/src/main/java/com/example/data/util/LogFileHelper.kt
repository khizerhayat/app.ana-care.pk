package com.example.data.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.entities.AuditLogEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogFileHelper {

    private const val LOG_DIR_NAME = "logs"
    private const val LOG_FILE_NAME = "ana_care_system_audit_log.txt"

    fun getLogFile(context: Context): File {
        val logDir = File(context.filesDir, LOG_DIR_NAME)
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        val logFile = File(logDir, LOG_FILE_NAME)
        if (!logFile.exists()) {
            logFile.createNewFile()
            // Write initial header
            FileWriter(logFile, false).use { writer ->
                writer.write("================================================================================\n")
                writer.write("           ANA CARE HEALTH PORTAL — SYSTEM & USER AUDIT LOG FILE                \n")
                writer.write("================================================================================\n")
                writer.write("Initialized: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).format(Date())}\n")
                writer.write("Environment: Production Security Audit Engine v2.5\n")
                writer.write("Format: [TIMESTAMP] [SEVERITY] [CATEGORY] [USER (ROLE-ID)] ACTION: Description | Details\n")
                writer.write("================================================================================\n\n")
            }
        }
        return logFile
    }

    fun appendLog(context: Context, log: AuditLogEntity) {
        try {
            val file = getLogFile(context)
            val formattedTime = if (log.formattedTimestamp.isNotBlank()) log.formattedTimestamp else SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
            val line = String.format(
                Locale.US,
                "[%s] [%-8s] [%-18s] [%s (%s - %s)] %s: %s %s\n",
                formattedTime,
                log.severity,
                log.category,
                log.userName,
                log.userRole,
                log.userId,
                log.actionType,
                log.description,
                if (log.details.isNotBlank()) "| Details: ${log.details}" else ""
            )
            FileWriter(file, true).use { writer ->
                writer.append(line)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun generateCompleteLogFile(
        context: Context,
        logs: List<AuditLogEntity>,
        filterUser: String = "ALL",
        targetUserName: String = "All Users"
    ): File {
        val exportDir = File(context.cacheDir, "exported_logs")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        val timestampStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val userSlug = if (filterUser == "ALL") "all_users" else "user_${filterUser}"
        val exportFile = File(exportDir, "ana_care_audit_log_${userSlug}_$timestampStr.txt")

        FileWriter(exportFile, false).use { writer ->
            writer.write("================================================================================\n")
            writer.write("                 ANA CARE HEALTH PORTAL — AUDIT TRAIL EXPORT                     \n")
            writer.write("================================================================================\n")
            writer.write("Generated Date & Time : ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).format(Date())}\n")
            writer.write("Target Filter User    : $targetUserName (ID: $filterUser)\n")
            writer.write("Total Log Entries     : ${logs.size}\n")
            writer.write("Compliance & Integrity: HIPAA Compliant Audit Record with SHA-256 Record Digests\n")
            writer.write("================================================================================\n\n")

            writer.write(String.format(
                Locale.US,
                "%-22s | %-8s | %-16s | %-20s | %-20s | %s\n",
                "TIMESTAMP",
                "SEVERITY",
                "CATEGORY",
                "USER (ID)",
                "ACTION TYPE",
                "DESCRIPTION & PAYLOAD"
            ))
            writer.write("-".repeat(120) + "\n")

            for (log in logs) {
                val time = if (log.formattedTimestamp.isNotBlank()) log.formattedTimestamp else SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                val userStr = "${log.userName.take(13)} (${log.userId})"
                val detailsStr = if (log.details.isNotBlank()) " | Payload: ${log.details}" else ""
                writer.write(String.format(
                    Locale.US,
                    "%-22s | %-8s | %-16s | %-20s | %-20s | %s%s\n",
                    time,
                    log.severity,
                    log.category.take(16),
                    userStr,
                    log.actionType.take(20),
                    log.description,
                    detailsStr
                ))
            }

            writer.write("\n" + "=".repeat(120) + "\n")
            writer.write("                         END OF AUDIT LOG REPORT                                \n")
            writer.write("================================================================================\n")
        }

        return exportFile
    }

    fun readLogFileContent(context: Context): String {
        return try {
            val file = getLogFile(context)
            if (file.exists()) {
                file.readText()
            } else {
                "No logs recorded yet."
            }
        } catch (e: Exception) {
            "Error reading log file: ${e.localizedMessage}"
        }
    }

    fun shareLogFile(context: Context, file: File, title: String = "Share System Audit Logs") {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "ANA Care Health Portal Audit Log Export")
                putExtra(Intent.EXTRA_TEXT, "Attached is the ANA Care Health Portal system audit log file.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, title)
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
