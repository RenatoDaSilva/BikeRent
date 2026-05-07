package com.bike.rent

import java.security.MessageDigest

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

fun String?.toSafeDouble(): Double = this?.toDoubleOrNull() ?: 0.0
fun String?.toSafeInt(): Int = this?.toIntOrNull() ?: 0

fun String?.formatAsDate(): String {
    if (this.isNullOrBlank()) return "-"
    return try {
        // Try ISO format from API
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = inputFormat.parse(this)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        date?.let { outputFormat.format(it) } ?: "-"
    } catch (e: Exception) {
        this // Return original if parsing fails
    }
}
