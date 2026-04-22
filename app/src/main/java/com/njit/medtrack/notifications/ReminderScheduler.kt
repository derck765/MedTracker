package com.njit.medtrack.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.njit.medtrack.Medication
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private val TIME_FORMATS = listOf(
        SimpleDateFormat("h:mm a", Locale.US),
        SimpleDateFormat("hh:mm a", Locale.US),
        SimpleDateFormat("H:mm", Locale.US),
        SimpleDateFormat("HH:mm", Locale.US),
    )

    fun schedule(context: Context, medication: Medication) {
        if (medication.frequency == "As Needed" || medication.scheduleTime.isBlank()) return
        if (!areNotificationsEnabled(context)) return

        val wm = WorkManager.getInstance(context)
        val data = Data.Builder()
            .putLong(ReminderWorker.KEY_MED_ID, medication.id)
            .putString(ReminderWorker.KEY_MED_NAME, medication.name)
            .putString(ReminderWorker.KEY_MED_DOSAGE, medication.dosage)
            .build()

        when (medication.frequency) {
            "Weekly" -> schedulePeriodic(wm, medication.id, data, 7 * 24L, medication.scheduleTime)
            "Twice Daily" -> {
                schedulePeriodic(wm, medication.id, data, 24L, medication.scheduleTime, suffix = "_a")
                val cal = parseTime(medication.scheduleTime) ?: return
                cal.add(Calendar.HOUR_OF_DAY, 12)
                val shifted = String.format(
                    Locale.US,
                    "%d:%02d",
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                )
                schedulePeriodic(wm, medication.id, data, 24L, shifted, suffix = "_b")
            }
            else -> schedulePeriodic(wm, medication.id, data, 24L, medication.scheduleTime)
        }
    }

    fun cancel(context: Context, medicationId: Long) {
        val wm = WorkManager.getInstance(context)
        wm.cancelUniqueWork("reminder_$medicationId")
        wm.cancelUniqueWork("reminder_${medicationId}_a")
        wm.cancelUniqueWork("reminder_${medicationId}_b")
    }

    fun cancelAll(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(ReminderWorker.CHANNEL_ID)
    }

    fun rescheduleAll(context: Context, medications: List<Medication>) {
        medications.forEach { schedule(context, it) }
    }

    fun areNotificationsEnabled(context: Context): Boolean =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getBoolean("notifications_enabled", true)

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit().putBoolean("notifications_enabled", enabled).apply()
    }

    private fun schedulePeriodic(
        wm: WorkManager,
        medId: Long,
        data: Data,
        periodHours: Long,
        scheduleTime: String,
        suffix: String = "",
    ) {
        val delay = calculateDelay(scheduleTime, periodHours)
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(periodHours, TimeUnit.HOURS)
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(ReminderWorker.CHANNEL_ID)
            .build()
        wm.enqueueUniquePeriodicWork(
            "reminder_$medId$suffix",
            ExistingPeriodicWorkPolicy.REPLACE,
            request,
        )
    }

    private fun parseTime(timeStr: String): Calendar? {
        for (fmt in TIME_FORMATS) {
            try {
                val date = fmt.parse(timeStr.trim()) ?: continue
                val parsed = Calendar.getInstance().apply { time = date }
                return Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, parsed.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, parsed.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            } catch (_: Exception) {
            }
        }
        return null
    }

    private fun calculateDelay(scheduleTime: String, periodHours: Long): Long {
        val target = parseTime(scheduleTime) ?: return 0L
        var delay = target.timeInMillis - System.currentTimeMillis()
        if (delay < 0) delay += TimeUnit.HOURS.toMillis(periodHours)
        return delay.coerceAtLeast(0L)
    }
}
