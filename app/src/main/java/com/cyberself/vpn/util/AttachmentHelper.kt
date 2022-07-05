package com.cyberself.vpn.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.OffsetDateTime

fun provideAttachmentOutputFile(appContext: Context): File {
    val fileName = "${OffsetDateTime.now().toLocalDateTime()}.jpg"
    return File(appContext.filesDir, fileName)
}

suspend fun retrieveGalleryPhoto(appContext: Context, imageContentUri: Uri): File? = withContext(Dispatchers.IO) {
    return@withContext appContext.contentResolver.openInputStream(imageContentUri)?.use { imageStream ->
        val imageBitmap = BitmapFactory.decodeStream(imageStream)
        val outputFile: File = provideAttachmentOutputFile(appContext)
        FileOutputStream(outputFile).use { outputStream ->
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        imageBitmap.recycle()
        outputFile
    }
}
