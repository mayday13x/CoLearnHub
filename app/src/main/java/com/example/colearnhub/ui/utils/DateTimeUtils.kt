package com.example.colearnhub.ui.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.util.Log

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


        val date: Date? = try {
            // Tentar com milissegundos
            val formatMilliseconds = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
            Log.d("DateTimeUtils", "Tentando parse com milissegundos...")
            val result = formatMilliseconds.parse(parsedDateString)
            Log.d("DateTimeUtils", "Sucesso com milissegundos: $result")
            result
        } catch (e1: ParseException) {
            Log.d("DateTimeUtils", "Falhou com milissegundos: ${e1.message}")
            try {
                // Tentar sem milissegundos
                val formatSeconds = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                Log.d("DateTimeUtils", "Tentando parse sem milissegundos...")
                val result = formatSeconds.parse(parsedDateString)
                Log.d("DateTimeUtils", "Sucesso sem milissegundos: $result")
                result
            } catch (e2: ParseException) {
                Log.d("DateTimeUtils", "Falhou sem milissegundos: ${e2.message}")
                try {
                    // Tentar só com data
                    val formatDateOnly = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    Log.d("DateTimeUtils", "Tentando parse só com data...")
                    val result = formatDateOnly.parse(parsedDateString)
                    Log.d("DateTimeUtils", "Sucesso só com data: $result")
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

        val now = Date()
        val diffInMillis = now.time - date.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return when {
            minutes < 1 -> "Agora mesmo"
            minutes < 60 -> "à $minutes min"
            hours < 24 -> "à ${hours}h"
            days == 1L -> "Ontem"
            days < 30 -> "à ${days}d"
            days < 365 -> "à ${days / 30} meses"
            else -> "à ${days / 365} anos"
        }
    }
}