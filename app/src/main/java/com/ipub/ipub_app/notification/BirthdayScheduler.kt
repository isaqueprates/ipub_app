package com.ipub.ipub_app.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object BirthdayScheduler {

    private const val WORK_NAME = "DailyBirthdayCheckWorker"

    fun scheduleDailyCheck(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<BirthdayNotificationWorker>(
            24, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        // Verificação imediata ao iniciar o app
        val immediateRequest = OneTimeWorkRequestBuilder<BirthdayNotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(immediateRequest)
    }
}
