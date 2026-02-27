package com.example.stylecraft.core

import java.text.SimpleDateFormat
import java.util.*

object FaceShapeUtils {

    fun getFaceShapeEmoji(shape: String): String {
        return when (shape.uppercase()) {
            "OVAL" -> "🥚"
            "ROUND" -> "⭕"
            "SQUARE" -> "⬜"
            "HEART" -> "💝"
            "DIAMOND" -> "💎"
            else -> "👤"
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            diff < 604800_000 -> "${diff / 86400_000}d ago"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
