package com.example.colearnhub.ui.utils

import android.content.Context
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.util.Log
import com.example.colearnhub.R
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeUtils {

    private fun truncateToMillis(dateString: String): String {
        Log.d("DateTimeUtils", "Raw Date: $dateString")

        val parts = dateString.split(".")
        return if (parts.size > 1) {
            val secondsPart = parts[0]
            val fractionalPart = parts[1]
            if (fractionalPart.length > 3) {
                "$secondsPart.${fractionalPart.substring(0, 3)}"
            } else {
                dateString
            }
        } else {
            dateString
        }
    }

    fun formatTimeAgo(dateString: String?, context: Context): String {
        if (dateString.isNullOrEmpty()) return context.getString(R.string.unknown_time)

        val parsedDateString = truncateToMillis(dateString)
        Log.d("DateTimeUtils", "Parsed Date String: $parsedDateString")

        val date: Date? = try {
            // Primeiro tentar o formato do Supabase (yyyy-MM-dd HH:mm:ss.SSS)
            val formatSupabase = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            formatSupabase.timeZone = TimeZone.getTimeZone("Europe/Lisbon") // UTC+1 (Portugal)
            Log.d("DateTimeUtils", "Tentando parse com formato Supabase...")
            val result = formatSupabase.parse(parsedDateString)
            Log.d("DateTimeUtils", "Sucesso com formato Supabase: $result")
            result
        } catch (e1: ParseException) {
            Log.d("DateTimeUtils", "Falhou com formato Supabase: ${e1.message}")
            try {
                // Tentar formato ISO
                val formatISO = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                formatISO.timeZone = TimeZone.getTimeZone("Europe/Lisbon")
                Log.d("DateTimeUtils", "Tentando parse com formato ISO...")
                val result = formatISO.parse(parsedDateString)
                Log.d("DateTimeUtils", "Sucesso com formato ISO: $result")
                result
            } catch (e2: ParseException) {
                Log.d("DateTimeUtils", "Falhou com formato ISO: ${e2.message}")
                try {
                    // Tentar formato sem milissegundos
                    val formatNoMillis = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    formatNoMillis.timeZone = TimeZone.getTimeZone("Europe/Lisbon")
                    Log.d("DateTimeUtils", "Tentando parse sem milissegundos...")
                    val result = formatNoMillis.parse(parsedDateString)
                    Log.d("DateTimeUtils", "Sucesso sem milissegundos: $result")
                    result
                } catch (e3: ParseException) {
                    Log.e("DateTimeUtils", "Falhou em todos os formatos: ${e3.message}")
                    null
                }
            }
        }

        if (date == null) {
            Log.e("DateTimeUtils", "Não foi possível fazer parse da data!")
            return context.getString(R.string.invalid_date)
        }

        // Não precisamos mais converter o fuso horário pois já está em UTC+1
        val now = Date()
        val diffInMillis = now.time - date.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        Log.d("DateTimeUtils", "Diferença em minutos: $minutes")
        Log.d("DateTimeUtils", "Diferença em horas: $hours")
        Log.d("DateTimeUtils", "Diferença em dias: $days")

        return when {
            minutes < 5 -> context.getString(R.string.just_now)
            minutes < 60 -> context.getString(R.string.minutes_ago, minutes)
            hours < 24 -> context.getString(R.string.hours_ago, hours)
            days == 1L -> context.getString(R.string.yesterday)
            days < 30 -> context.getString(R.string.days_ago, days)
            days < 365 -> context.getString(R.string.months_ago, days / 30)
            else -> context.getString(R.string.years_ago, days / 365)
        }
    }
}