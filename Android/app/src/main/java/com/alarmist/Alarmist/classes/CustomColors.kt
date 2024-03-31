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

        val TextButtonColor: Color
            @Composable
            get() = Color.Blue

        val TitleBarColor: Color
            @Composable
            get() = if (isSystemInDarkTheme()) {
                Color(0xFF4A4A4A)
            } else {
                Color(0xFFEAEAEA)
            }

        val DialogBackgroundColor: Color
            @Composable
            get() = if (isSystemInDarkTheme()) {
                Color(0xFF535353)
            } else {
                Color.White
            }
    }
}