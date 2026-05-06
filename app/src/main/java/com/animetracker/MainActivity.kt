package com.animetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.*
import coil.compose.AsyncImage
import com.animetracker.worker.AnimeWorker
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val jikanApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.jikan.moe/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JikanService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Auto-schedule background task
        val workRequest = PeriodicWorkRequestBuilder<AnimeWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AnimeTrackerWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        setContent {
            var animeList by remember { mutableStateOf<List<AnimeData>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                try {
                    val response = jikanApi.getTopAnime()
                    animeList = response.data
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isLoading = false
                }
            }

            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Column {
                    Text(
                        "Top Anime (Live Jikan API)", 
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    if (isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    LazyColumn {
                        items(animeList) { anime ->
                            Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                                Row(modifier = Modifier.padding(8.dp)) {
                                    AsyncImage(
                                        model = anime.images.jpg.image_url,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp)
                                    )
                                    Column(modifier = Modifier.padding(start = 8.dp)) {
                                        Text(anime.title, style = MaterialTheme.typography.titleMedium)
                                        Text("Episodes: ${anime.episodes ?: "N/A"}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}