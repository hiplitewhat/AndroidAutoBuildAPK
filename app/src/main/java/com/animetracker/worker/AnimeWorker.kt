package com.animetracker.worker
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat

class AnimeWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            showNotification("AnimeTracker", "Checking for new episodes...")
            Result.success()
        } catch (e: Exception) { Result.retry() }
    }
    private fun showNotification(title: String, msg: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(NotificationChannel("updates", "Anime Updates", NotificationManager.IMPORTANCE_DEFAULT))
        }
        val notif = NotificationCompat.Builder(applicationContext, "updates")
            .setContentTitle(title).setContentText(msg).setSmallIcon(android.R.drawable.ic_dialog_info).build()
        manager.notify(1, notif)
    }
}