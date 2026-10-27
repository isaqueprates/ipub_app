package com.ipub.ipub_app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ipub.ipub_app.MainActivity
import com.ipub.ipub_app.R
import com.ipub.ipub_app.data.Member
import java.time.LocalDate

object BirthdayNotificationHelper {

    private const val CHANNEL_ID = "birthday_notifications_channel"
    private const val CHANNEL_NAME = "Aniversários do Dia"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificações diárias sobre os aniversariantes do dia"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendBirthdayNotification(context: Context, birthdayMembers: List<Member>) {
        createNotificationChannel(context)

        if (birthdayMembers.isEmpty()) return

        val todayStr = LocalDate.now().toString()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "birthday_detail")
            putExtra("target_date", todayStr)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val names = birthdayMembers.joinToString(", ") { it.name }
        val title = if (birthdayMembers.size == 1) {
            "🎉 Hoje é aniversário de ${birthdayMembers.first().name}!"
        } else {
            "🎉 Hoje temos ${birthdayMembers.size} aniversariantes!"
        }

        val text = "Aniversariante(s) de hoje: $names. Clique para ver no aplicativo."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
