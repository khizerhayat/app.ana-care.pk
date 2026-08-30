package com.example.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageStorageHelper {

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, prefix: String = "medical_img"): String {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "${prefix}_${timeStamp}_${System.currentTimeMillis()}.jpg"
            val storageDir = File(context.filesDir, "medical_gallery")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val imageFile = File(storageDir, imageFileName)
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            imageFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun saveUriToInternalStorage(context: Context, sourceUri: Uri, prefix: String = "medical_upload"): String {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "${prefix}_${timeStamp}_${System.currentTimeMillis()}.jpg"
            val storageDir = File(context.filesDir, "medical_gallery")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val destinationFile = File(storageDir, imageFileName)
            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val outputStream = FileOutputStream(destinationFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            sourceUri.toString()
        }
    }
}
