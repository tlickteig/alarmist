package com.alarmist.Alarmist.objects

import com.alarmist.Alarmist.classes.AlarmSchedule
import com.alarmist.Alarmist.classes.DateSerializer
import com.alarmist.Alarmist.classes.DaysOfWeek
import com.alarmist.Alarmist.classes.TimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Serializable
class Alarm {
    var id: Int = 0
    var name: String = ""
    var scheduleMode: AlarmSchedule = AlarmSchedule.ONE_TIME
    var isEnabled: Boolean = false
    var ringtones: List<String> = mutableListOf()
    var labels: HashSet<String> = hashSetOf()
    var category: String = ""
    var time: @Serializable(with = TimeSerializer::class) LocalTime = LocalTime.MIDNIGHT

    var specificDays: List<@Serializable(with = DateSerializer::class) LocalDate> = mutableListOf()
    var daysOfWeek: HashSet<DaysOfWeek> = hashSetOf()

    var subText: String = ""
        get() {
            return "Hello World!"
        }

    var title: String = ""
        get() {
            var output = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
            if (!name.isNullOrBlank()) {
                output = name
            }
            return output
        }
}