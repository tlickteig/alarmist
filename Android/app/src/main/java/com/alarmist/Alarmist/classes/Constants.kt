package com.alarmist.Alarmist.classes

class Constants {
    companion object {
        const val SHARED_PREFERENCES_FOR_ALL_ALARMS = "alarmListSharedPreferences"
        const val ALARM_PREFIX_FOR_SHARED_PREFERENCES = "sharedPreferencesAlarm_"

        const val SHARED_PREFERENCES_FOR_ALL_CATEGORIES = "categoryListSharedPreferences"
        const val CATEGORY_PREFIX_FOR_SHARED_PREFERENCES = "sharedPreferencesCategory_"

        // Must be 60 seconds to fire alarms at the proper interval
        const val BACKGROUND_THREAD_INTERVAL_MS = 60_000

        const val NOTIFICATION_CHANNEL_ID = "AlarmistNotifications"
        const val NOTIFICATION_GOING_OFF_ID = 15433
    }
}

class FontAwesomeConstants {
    companion object {
        const val MENU_ICON = "\uf0c9"
    }
}