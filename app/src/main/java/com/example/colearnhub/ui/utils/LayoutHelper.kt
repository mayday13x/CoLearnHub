package com.example.colearnhub.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Define os breakpoints manuais (como em CSS).
 */
enum class ScreenSize {
    SMALL, MEDIUM, LARGE
}

/**
 * Retorna a largura do ecrã em dp.
 */
@Composable
fun screenWidthDp(): Int {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp
}

/**
 * Determina a categoria de tamanho de ecrã com base em breakpoints.
 */
@Composable
fun getScreenSize(): ScreenSize {
    val width = screenWidthDp()
    return when {
        width < 600 -> ScreenSize.SMALL   // telemóveis
        width < 840 -> ScreenSize.MEDIUM  // tablets pequenos
        else -> ScreenSize.LARGE          // tablets grandes, desktops
    }
}

/**
 * Função utilitária para verificar se é tablet.
 */
@Composable
fun isTablet(): Boolean {
    return getScreenSize() != ScreenSize.SMALL
}

/**
 * Padding dinâmico com base na largura do ecrã (5% por defeito).
 */
@Composable
fun dynamicPadding(factor: Float = 0.05f): Dp {
    val width = screenWidthDp()
    return (width * factor).dp
}

/**
 * Largura dinâmica de elementos com limite máximo (ex: botões).
 */
@Composable
fun dynamicWidth(maxWidth: Dp = 300.dp, factor: Float = 0.8f): Dp {
    val width = screenWidthDp().dp
    val calculated = width * factor
    return if (calculated < maxWidth) calculated else maxWidth
}