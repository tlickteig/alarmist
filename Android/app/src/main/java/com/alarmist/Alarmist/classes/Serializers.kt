package com.alarmist.Alarmist.classes

import com.alarmist.Alarmist.classes.ExtensionMethods.Companion.alarmScheduleEnumValue
import com.alarmist.Alarmist.objects.Alarm
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Serializer(forClass = AlarmSchedule::class)
class AlarmScheduleSerializer : KSerializer<AlarmSchedule> {

    override fun serialize(encoder: Encoder, value: AlarmSchedule) {
        value.toString()
    }

    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun deserialize(decoder: Decoder): AlarmSchedule {
        var stringValue = decoder.decodeString()
        return AlarmSchedule.valueOf(stringValue)
    }
}

@Serializer(forClass = LocalDate::class)
class DateSerializer : KSerializer<LocalDate> {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}

@Serializer(forClass = LocalTime::class)
class TimeSerializer : KSerializer<LocalTime> {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString(), formatter)
    }
}

class AlarmSerializer {
    companion object {

        fun serializeAlarm(input: Alarm): String {
            return Json.encodeToString(input);
        }

        fun deserializeAlarm(input: String): Alarm {
            return Json.decodeFromString<Alarm>(input)
        }
    }
}