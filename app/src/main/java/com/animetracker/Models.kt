package com.animetracker

data class AnimeResponse(
    val data: List<AnimeData>
)

data class AnimeData(
    val mal_id: Int,
    val title: String,
    val episodes: Int?,
    val images: AnimeImages
)

data class AnimeImages(
    val jpg: ImageUrl
)

data class ImageUrl(
    val image_url: String
)