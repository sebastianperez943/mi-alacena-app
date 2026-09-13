package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class ExpiryStatus {
    EXPIRED,        // Caducado (rojo)
    EXPIRING_SOON,  // Por caducar en <= 5 días (amarillo/naranja)
    GOOD,           // En buen estado (> 5 días) (verde/neutro)
    NONE            // Sin fecha asignada
}

data class ExpiryInfo(
    val status: ExpiryStatus,
    val label: String,
    val daysRemaining: Long? = null
)

object ExpirationHelper {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val shortDateFormatter = SimpleDateFormat("d MMM yyyy", Locale("es", "ES"))

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return "Sin fecha"
        return dateFormatter.format(Date(timestamp))
    }

    fun formatDisplayDate(timestamp: Long?): String {
        if (timestamp == null) return "Sin fecha"
        return shortDateFormatter.format(Date(timestamp))
    }

    fun evaluateExpiry(expirationDate: Long?): ExpiryInfo {
        if (expirationDate == null) {
            return ExpiryInfo(ExpiryStatus.NONE, "Sin caducidad")
        }

        val todayStart = getStartOfDay(System.currentTimeMillis())
        val expiryStart = getStartOfDay(expirationDate)

        val diffMillis = expiryStart - todayStart
        val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

        return when {
            days < 0 -> {
                val pastDays = kotlin.math.abs(days)
                val label = if (pastDays == 1L) "Caducó ayer" else "Caducó hace $pastDays días"
                ExpiryInfo(ExpiryStatus.EXPIRED, label, days)
            }
            days == 0L -> {
                ExpiryInfo(ExpiryStatus.EXPIRING_SOON, "¡Caduca hoy!", 0)
            }
            days == 1L -> {
                ExpiryInfo(ExpiryStatus.EXPIRING_SOON, "¡Caduca mañana!", 1)
            }
            days in 2..5 -> {
                ExpiryInfo(ExpiryStatus.EXPIRING_SOON, "Caduca en $days días", days)
            }
            days in 6..14 -> {
                ExpiryInfo(ExpiryStatus.GOOD, "Caduca en $days días", days)
            }
            else -> {
                ExpiryInfo(ExpiryStatus.GOOD, "Caduca: ${formatDisplayDate(expirationDate)}", days)
            }
        }
    }

    fun addDaysToToday(days: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return getStartOfDay(cal.timeInMillis)
    }

    fun addMonthsToToday(months: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, months)
        return getStartOfDay(cal.timeInMillis)
    }

    fun addYearsToToday(years: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, years)
        return getStartOfDay(cal.timeInMillis)
    }
}
