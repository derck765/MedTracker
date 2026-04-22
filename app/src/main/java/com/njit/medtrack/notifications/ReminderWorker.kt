package com.njit.medtrack.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.njit.medtrack.MainActivity
import com.njit.medtrack.R

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val name = inputData.getString(KEY_MED_NAME) ?: return Result.success()
        val dosage = inputData.getString(KEY_MED_DOSAGE).orEmpty()
        val medId = inputData.getLong(KEY_MED_ID, -1L)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            medId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val body = if (dosage.isNotEmpty()) "Dosage: $dosage" else "Don't forget your medication"
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_24)
            .setContentTitle("Time to take $name")
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(medId.toInt(), notification)

        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "medication_reminders"
        const val KEY_MED_ID = "med_id"
        const val KEY_MED_NAME = "med_name"
        const val KEY_MED_DOSAGE = "med_dosage"
    }
}
