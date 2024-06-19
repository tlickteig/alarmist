package com.alarmist.Alarmist.classes

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.alarmist.Alarmist.R

class CustomFonts {
    companion object {
        val FontAwesome = FontFamily(
            Font(R.font.fa_regular_400, FontWeight.Normal),
            Font(R.font.fa_solid_900, FontWeight.Bold),
            Font(R.font.fa_light_300, FontWeight.Light),
            Font(R.font.fa_thin_100, FontWeight.ExtraLight)
        )
    }
}