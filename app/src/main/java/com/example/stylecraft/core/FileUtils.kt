package com.example.stylecraft.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
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
                input.copyTo(output)
            }
        }
        tempFile
    } catch (_: Exception) {
        null
    }
}

/**
 * Reads an image from Uri and returns a resized JPG file (max side 1024px).
 * Also handles EXIF rotation correctly.
 */
fun getResizedImageFile(context: Context, uri: Uri): File? {
    return try {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        }

        // Calculate sample size
        val maxSide = 1024
        var inSampleSize = 1
        if (options.outHeight > maxSide || options.outWidth > maxSide) {
            val halfHeight: Int = options.outHeight / 2
            val halfWidth: Int = options.outWidth / 2
            while (halfHeight / inSampleSize >= maxSide && halfWidth / inSampleSize >= maxSide) {
                inSampleSize *= 2
            }
        }

        // Decode with inSampleSize
        val decodeOptions = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
        var bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, decodeOptions)
        } ?: return null

        // Handle rotation from EXIF
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }
                if (!matrix.isIdentity) {
                    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    bitmap.recycle()
                    bitmap = rotatedBitmap
                }
            }
        } catch (e: Exception) { e.printStackTrace() }

        // Final resize if still too big (to exactly 1024 on the longest side)
        if (bitmap.width > maxSide || bitmap.height > maxSide) {
            val scale = maxSide.toFloat() / Math.max(bitmap.width, bitmap.height)
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            bitmap.recycle()
            bitmap = scaledBitmap
        }

        // Save to temp file
        val tempFile = File.createTempFile("resized_", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        bitmap.recycle()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
