package com.alarmist.Alarmist.classes

import androidx.compose.runtime.mutableStateListOf

class Utilities {
    companion object {

        fun returnAllAlarms(): List<Alarm> {

            var output: List<Alarm> = mutableListOf();
            output += listOf(
                Alarm().apply
                {
                    name = "Alarm 1"
                    isEnabled = true
                },
                Alarm().apply
                {
                    name = "Alarm 2"
                    isEnabled = false
                }
            )

            return output;
        }

        fun saveOrUpdateAlarm(alarm: Alarm) {

            var sharedPreferences = getPreferences(MODE_PRIVATE)
        }
    }
}