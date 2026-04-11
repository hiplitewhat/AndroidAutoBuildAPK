package com.example.animetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.animetracker.ui.theme.AnimeTrackerTheme
import com.example.animetracker.worker.AnimeWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val WORK_NAME = "anime_check"
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
        } else {
            Log.w(TAG, "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission (Android 13+)
        requestNotificationPermission()

        // Safety: re-schedule WorkManager on app launch
        scheduleAnimeWorker()

        setContent {
            AnimeTrackerTheme {
                // Your Compose UI here
                AnimeTrackerApp()
            }
        }
    }

    /**
     * Safety re-scheduling on app launch.
     * Uses KEEP policy to avoid duplicates.
     */
    private fun scheduleAnimeWorker() {
        Log.d(TAG, "Ensuring AnimeWorker is scheduled...")

        val workRequest = PeriodicWorkRequestBuilder<AnimeWorker>(
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Log.d(TAG, "AnimeWorker scheduled (KEEP policy)")
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}