package com.example.animetracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.animetracker.R
import com.example.animetracker.data.FavoritesRepository
import com.example.animetracker.network.AnimeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "AnimeWorker"
        private const val CHANNEL_ID = "anime_updates"
        private const val CHANNEL_NAME = "Anime Updates"
        private const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Checking for new anime episodes...")

            val favorites = FavoritesRepository.getFavorites(applicationContext)

            if (favorites.isEmpty()) {
                Log.d(TAG, "No favorites found, skipping check")
                return@withContext Result.success()
            }

            for (anime in favorites) {
                val hasNewEpisode = AnimeApiService.checkNewEpisode(anime.id)

                if (hasNewEpisode) {
                    sendNotification(
                        title = "New Episode Available! 🎬",
                        message = "${anime.title} has a new episode out!"
                    )
                }
            }

            Log.d(TAG, "Anime check completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking anime: ${e.message}", e)
            Result.retry()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new anime episodes"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}