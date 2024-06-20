package com.alarmist.Alarmist.classes

import kotlinx.serialization.Serializable
import java.sql.Date

@Serializable
class Alarm {
    var id: Int = 0
    var name: String = ""
    var scheduleMode: AlarmSchedule = AlarmSchedule.ONE_TIME
    var isEnabled: Boolean = false
    var ringtones: List<String> = mutableListOf()
    var labels: HashSet<String> = hashSetOf()
    var categoryId: Int = 0

    //@Serializable(with = DateSerializer::class)
    var specificDays: List<Date> = mutableListOf()
    var daysOfWeek: HashSet<DaysOfWeek> = hashSetOf()

    var willGoOff: String = ""
        get() {
            return "Hello World!"
        }
}