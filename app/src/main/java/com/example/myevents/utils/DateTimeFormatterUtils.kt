package com.example.myevents.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun dateTimeFormatterFromDBstring(dateString: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val dateTime = LocalDateTime.parse(dateString, inputFormatter)

    val currentLocale = Locale.getDefault()
    val dateFormatter = if (currentLocale.language == "it") {
        DateTimeFormatter.ofPattern("d MMMM yyyy 'ore' HH:mm", currentLocale)
    } else {
        DateTimeFormatter.ofPattern("MMMM d'th', yyyy 'at' hh:mm a", currentLocale)
    }

    return dateTime.format(dateFormatter)
}

fun dateTimeFormatterFromLocalDateTime(dateTime: LocalDateTime): String {
    val currentLocale = Locale.getDefault()
    val dateFormatter = if (currentLocale.language == "it") {
        DateTimeFormatter.ofPattern("d MMMM yyyy 'ore' HH:mm", currentLocale)
    } else {
        DateTimeFormatter.ofPattern("MMMM d'th', yyyy 'at' hh:mm a", currentLocale)
    }

    return dateTime.format(dateFormatter)
}