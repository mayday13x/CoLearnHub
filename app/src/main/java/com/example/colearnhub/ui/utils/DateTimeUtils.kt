package com.example.colearnhub.ui.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    fun formatTimeAgo(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Desconhecido"

        try {
            // Formato da base de dados: "2025-06-03 18:42:25.615385"
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")

            val truncatedDateString = if (dateString.length > 23) {
                dateString.substring(0, 23)
            } else {
                dateString
            }

            val createdAt = format.parse(truncatedDateString)
            val now = Date()

            val diffInMillis = now.time - createdAt.time
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
            val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
            val months = days / 30
            val years = days / 365

            return when {
                minutes < 1 -> "Agora mesmo"
                hours < 1 -> "Há ${minutes}min"
                days < 1 -> "Há ${hours}h"
                days == 1L -> "Há 1 dia"
                days < 30 -> "Há ${days}d"
                months == 1L -> "Há 1 mês"
                months < 12 -> "Há ${months} meses"
                years == 1L -> "Há 1 ano"
                else -> "Há ${years} anos"
            }
        } catch (e: ParseException) {
            // Tentar formato alternativo se o primeiro falhar
            try {
                val fallbackFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val createdAt = fallbackFormat.parse(dateString)
                val now = Date()
                val diffInMillis = now.time - createdAt.time
                val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                val months = days / 30
                val years = days / 365

                return when {
                    days < 30 -> "Há ${days}d"
                    months < 12 -> "Há ${months} meses"
                    else -> "Há ${years} anos"
                }
            } catch (ex: Exception) {
                return "Data inválida"
            }
        } catch (e: Exception) {
            return "Data inválida"
        }
    }
}