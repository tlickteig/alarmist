package com.alarmist.Alarmist.objects

import com.alarmist.Alarmist.classes.AlarmSchedule
import com.alarmist.Alarmist.classes.AlarmScheduleSerializer
import com.alarmist.Alarmist.classes.DateSerializer
import com.alarmist.Alarmist.classes.DaysOfWeek
import com.alarmist.Alarmist.classes.TimeSerializer
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Serializable
class Alarm {
    var id: Int = 0
    var name: String = ""
    var scheduleMode: @Serializable(with = AlarmScheduleSerializer::class) AlarmSchedule = AlarmSchedule.ONE_TIME
    var isEnabled: Boolean = false
    var ringtones: List<String> = mutableListOf()
    var labels: HashSet<String> = hashSetOf()
    var category: String = ""
    var time: @Serializable(with = TimeSerializer::class) LocalTime = LocalTime.MIDNIGHT
    var snoozeMinutes: Int = 0

    var specificDays: List<@Serializable(with = DateSerializer::class) LocalDate> = mutableListOf()
    var daysOfWeek: HashSet<DaysOfWeek> = hashSetOf()

    var daysOfWeekString: List<String> = mutableListOf()
        get() {
            var output: List<String> = mutableListOf()
            for (dayEnum in daysOfWeek) {
                if (dayEnum == DaysOfWeek.SUNDAY) {
                    output += "Sunday"
                } else if (dayEnum == DaysOfWeek.MONDAY) {
                    output += "Monday"
                } else if (dayEnum == DaysOfWeek.TUESDAY) {
                    output += "Tuesday"
                } else if (dayEnum == DaysOfWeek.WEDNESDAY) {
                    output += "Wednesday"
                } else if (dayEnum == DaysOfWeek.THURSDAY) {
                    output += "Thursday"
                } else if (dayEnum == DaysOfWeek.FRIDAY) {
                    output += "Friday"
                } else if (dayEnum == DaysOfWeek.SATURDAY) {
                    output += "Saturday"
                }
            }

            return output
        }

    var subText: String = ""
        get() {
            var output = ""
            if (snoozeMinutes > 0) {
                var willGoOff = this.time
                willGoOff = willGoOff.plusMinutes(snoozeMinutes.toLong())
                output = "Alarm snoozed until ${willGoOff.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))}"
            } else if (title != null && title != "") {
                output = title
            }

            return output
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