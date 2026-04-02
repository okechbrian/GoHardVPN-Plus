package com.gohardvpn.plus.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.gohardvpn.plus.AppConfig
import java.util.Calendar

object TimeManager {
    private const val PREF_TIME_MANAGER_ENABLED = "pref_time_manager_enabled"
    private const val PREF_TIME_MANAGER_START_HOUR = "pref_time_manager_start_hour"
    private const val PREF_TIME_MANAGER_START_MINUTE = "pref_time_manager_start_minute"
    private const val PREF_TIME_MANAGER_END_HOUR = "pref_time_manager_end_hour"
    private const val PREF_TIME_MANAGER_END_MINUTE = "pref_time_manager_end_minute"
    private const val PREF_TIME_MANAGER_DAYS = "pref_time_manager_days"

    private const val REQUEST_CODE_START = 1001
    private const val REQUEST_CODE_STOP = 1002

    fun isTimeManagerEnabled(): Boolean {
        return MmkvManager.decodeSettingsBool(PREF_TIME_MANAGER_ENABLED, false)
    }

    fun setTimeManagerEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(PREF_TIME_MANAGER_ENABLED, enabled)
        if (enabled) {
            updateSchedule()
        } else {
            cancelSchedule()
        }
    }

    fun getStartTime(): Pair<Int, Int> {
        val hour = MmkvManager.decodeSettingsInt(PREF_TIME_MANAGER_START_HOUR, 8)
        val minute = MmkvManager.decodeSettingsInt(PREF_TIME_MANAGER_START_MINUTE, 0)
        return Pair(hour, minute)
    }

    fun setStartTime(hour: Int, minute: Int) {
        MmkvManager.encodeSettings(PREF_TIME_MANAGER_START_HOUR, hour)
        MmkvManager.encodeSettings(PREF_TIME_MANAGER_START_MINUTE, minute)
        if (isTimeManagerEnabled()) {
            updateSchedule()
        }
    }

    fun getEndTime(): Pair<Int, Int> {
        val hour = MmkvManager.decodeSettingsInt(PREF_TIME_MANAGER_END_HOUR, 18)
        val minute = MmkvManager.decodeSettingsInt(PREF_TIME_MANAGER_END_MINUTE, 0)
        return Pair(hour, minute)
    }

    fun setEndTime(hour: Int, minute: Int) {
        MmkvManager.encodeSettings(PREF_TIME_MANAGER_END_HOUR, hour)
        MmkvManager.encodeSettings(PREF_TIME_MANAGER_END_MINUTE, minute)
        if (isTimeManagerEnabled()) {
            updateSchedule()
        }
    }

    fun getActiveDays(): Set<Int> {
        val daysStr = MmkvManager.decodeSettingsString(PREF_TIME_MANAGER_DAYS) ?: "2,3,4,5,6"
        return daysStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun setActiveDays(days: Set<Int>) {
        MmkvManager.encodeSettings(PREF_TIME_MANAGER_DAYS, days.joinToString(","))
        if (isTimeManagerEnabled()) {
            updateSchedule()
        }
    }

    fun updateSchedule() {
        val context = AngApplication.application
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        cancelSchedule()

        val (startHour, startMinute) = getStartTime()
        val (endHour, endMinute) = getEndTime()
        val activeDays = getActiveDays()

        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_WEEK)

        if (currentDay !in activeDays.map { it + 1 }) {
            return
        }

        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
            set(Calendar.SECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val endTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMinute)
            set(Calendar.SECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        scheduleAlarm(context, alarmManager, startTime.timeInMillis, REQUEST_CODE_START, true)
        scheduleAlarm(context, alarmManager, endTime.timeInMillis, REQUEST_CODE_STOP, false)
    }

    private fun scheduleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        triggerTime: Long,
        requestCode: Int,
        isStart: Boolean
    ) {
        val intent = Intent(context, TimeActionReceiver::class.java).apply {
            action = if (isStart) "com.gohardvpn.plus.action.TIME_START" else "com.gohardvpn.plus.action.TIME_STOP"
            putExtra("action", if (isStart) "start" else "stop")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelSchedule() {
        val context = AngApplication.application
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        listOf(REQUEST_CODE_START, REQUEST_CODE_STOP).forEach { requestCode ->
            val intent = Intent(context, TimeActionReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
    }

    fun getTimeSummary(): String {
        if (!isTimeManagerEnabled()) {
            return "Disabled"
        }
        val (startHour, startMinute) = getStartTime()
        val (endHour, endMinute) = getEndTime()
        val days = getActiveDays()
        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val activeDays = days.map { dayNames.getOrElse(it) { "?" } }.joinToString(", ")
        return String.format("%02d:%02d - %02d:%02d (%s)", startHour, startMinute, endHour, endMinute, activeDays)
    }
}
