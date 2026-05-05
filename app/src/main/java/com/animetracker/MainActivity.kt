package com.animetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.animetracker.worker.AnimeWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleAnimeWorker()
        setContent {
            MaterialTheme {
                Text("AnimeTracker V23: WorkManager Active")
            }
        }
    }

    private fun scheduleAnimeWorker() {
        val workRequest = PeriodicWorkRequestBuilder<AnimeWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "anime_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}