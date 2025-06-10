package com.example.colearnhub.ui.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.util.Log
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

    fun formatTimeAgo(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Desconhecido"

        val parsedDateString = truncateToMillis(dateString)
        Log.d("DateTimeUtils", "Parsed Date String: $parsedDateString")

        val date: Date? = try {
            // Primeiro tentar o formato do Supabase (yyyy-MM-dd HH:mm:ss.SSS)
            val formatSupabase = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            formatSupabase.timeZone = TimeZone.getTimeZone("UTC") // Supabase armazena em UTC
            Log.d("DateTimeUtils", "Tentando parse com formato Supabase...")
            val result = formatSupabase.parse(parsedDateString)
            Log.d("DateTimeUtils", "Sucesso com formato Supabase: $result")
            result
        } catch (e1: ParseException) {
            Log.d("DateTimeUtils", "Falhou com formato Supabase: ${e1.message}")
            try {
                // Tentar formato ISO
                val formatISO = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                formatISO.timeZone = TimeZone.getTimeZone("UTC")
                Log.d("DateTimeUtils", "Tentando parse com formato ISO...")
                val result = formatISO.parse(parsedDateString)
                Log.d("DateTimeUtils", "Sucesso com formato ISO: $result")
                result
            } catch (e2: ParseException) {
                Log.d("DateTimeUtils", "Falhou com formato ISO: ${e2.message}")
                try {
                    // Tentar formato sem milissegundos
                    val formatNoMillis = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    formatNoMillis.timeZone = TimeZone.getTimeZone("UTC")
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
            return "Data inválida"
        }

        // Converter para o fuso horário local
        val localDate = Date(date.time + TimeZone.getDefault().getOffset(date.time))
        val now = Date()
        val diffInMillis = now.time - localDate.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        Log.d("DateTimeUtils", "Diferença em minutos: $minutes")
        Log.d("DateTimeUtils", "Diferença em horas: $hours")
        Log.d("DateTimeUtils", "Diferença em dias: $days")

        return when {
            minutes < 5 -> "Agora mesmo"
            minutes < 60 -> "à $minutes min"
            hours < 24 -> "à ${hours}h"
            days == 1L -> "Ontem"
            days < 30 -> "à ${days}d"
            days < 365 -> "à ${days / 30} meses"
            else -> "à ${days / 365} anos"
        }
    }
}