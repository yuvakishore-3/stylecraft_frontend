package com.example.stylecraft.core

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun copyUriToTempFile(
    context: Context,
    uri: Uri,
    prefix: String = "upload_",
    suffix: String = ".jpg"
): File? {
    return try {
        val tempFile = File.createTempFile(prefix, suffix, context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int
                while (true) {
                    bytesRead = input.read(buffer)
                    if (bytesRead == -1) break
                    output.write(buffer, 0, bytesRead)
                }
                output.flush()
            }
        }
        tempFile
    } catch (_: Exception) {
        null
    }
}
