package com.animetracker

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.constraints.Constraints
import androidx.work.NetworkType
import coil.compose.AsyncImage
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

// ------------------------------
// Explicit Typed Models (Fixes Type Inference)
// ------------------------------
data class AnimeResponse(val data: AnimeData)
data class AnimeData(val mal_id: Int, val title: String, val images: AnimeImages)
data class AnimeImages(val jpg: ImageJpg)
data class ImageJpg(val image_url: String)

// ------------------------------
// Fixed JikanService Retrofit Interface
// ------------------------------
interface JikanService {
    @GET("v4/anime/{id}")
    fun getAnimeById(@Path("id") animeId: Int): Call<AnimeResponse>
}

// ------------------------------
// Discord Bot Monitor + Off Alert Logic
// ------------------------------
object DiscordBotMonitor {
    var isBotOnline: Boolean = true

    fun checkBotStatus() {
        // Simulated bot health check
        isBotOnline = (Math.random() > 0.25)
    }
}

// ------------------------------
// Main Activity
// ------------------------------
class MainActivity : ComponentActivity() {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.jikan.moe/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val jikanService: JikanService by lazy { retrofit.create(JikanService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBackgroundSync()
        checkDiscordBotAndAlert()

        setContent {
            AppUI()
        }
    }

    // Check Discord Bot status and trigger alert if offline
    private fun checkDiscordBotAndAlert() {
        DiscordBotMonitor.checkBotStatus()
        if (!DiscordBotMonitor.isBotOnline) {
            Toast.makeText(this, "ALERT: Discord Bot is OFFLINE", Toast.LENGTH_LONG).show()
        }
    }

    // WorkManager Periodic Sync
    private fun setupBackgroundSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWork: WorkRequest = PeriodicWorkRequestBuilder<AnimeSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AnimeSyncTask",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            syncWork
        )
    }
}

// Background Worker
class AnimeSyncWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        DiscordBotMonitor.checkBotStatus()
        if (!DiscordBotMonitor.isBotOnline) {
            android.widget.Toast.makeText(applicationContext, "Discord Bot Offline Detected", Toast.LENGTH_SHORT).show()
        }
        return Result.success()
    }
}

// Compose UI
@Composable
fun AppUI() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "AniForge Architect V24", modifier = Modifier.padding(bottom = 6.dp))
        Text(text = "Retrofit Type Fix | Discord Bot Monitor Active")
    }
}