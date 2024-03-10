package com.alarmist.Alarmist.classes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class CustomColors {

    companion object {
        val BackgroundColor: Color
            @Composable
            get() = if (isSystemInDarkTheme()) {
                Color(0xFF4A4A4A)
            } else {
                Color(0xFFEDEDED)
            }

        val TextColor: Color
            @Composable
            get() = if (isSystemInDarkTheme()) {
                Color(0xFFEAEAEA)
            } else {
                Color(0xFF4A4A4A)
            }

        val TitleBarColor: Color
            @Composable
            get() = if (isSystemInDarkTheme()) {
                Color(0xFF4A4A4A)
            } else {
                Color(0xFFEAEAEA)
            }
    }
}