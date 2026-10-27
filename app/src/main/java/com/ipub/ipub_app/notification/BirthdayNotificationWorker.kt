package com.ipub.ipub_app.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.ipub.ipub_app.data.Member
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BirthdayNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val todayKey = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM"))

            // Verifica se já enviamos a notificação hoje
            val prefs = context.getSharedPreferences("birthday_prefs", Context.MODE_PRIVATE)
            val lastNotifiedDate = prefs.getString("last_notified_date", "")

            if (lastNotifiedDate == todayKey) {
                // Já foi notificado hoje, encerra sem duplicar
                return Result.success()
            }

            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("members").get().await()

            val members = snapshot.documents.mapNotNull { it.toObject(Member::class.java) }
            val birthdayMembers = members.filter { member ->
                val parts = member.birthday.split("/")
                if (parts.size >= 2) {
                    val key = "${parts[0].padStart(2, '0')}/${parts[1].padStart(2, '0')}"
                    key == todayKey
                } else false
            }

            if (birthdayMembers.isNotEmpty()) {
                BirthdayNotificationHelper.sendBirthdayNotification(context, birthdayMembers)

                // Salva que já notificamos hoje
                prefs.edit().putString("last_notified_date", todayKey).apply()
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}