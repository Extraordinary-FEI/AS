package com.example.cn.helloworld.data.model

import androidx.annotation.DrawableRes

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val playUrl: String? = null,
    val coverUrl: String? = null,
    @DrawableRes val coverResId: Int? = null,
    val tags: List<String> = emptyList(),
    val songs: List<Song> = emptyList()
)
