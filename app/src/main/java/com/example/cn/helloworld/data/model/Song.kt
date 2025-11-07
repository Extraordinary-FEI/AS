package com.example.cn.helloworld.data.model

import android.support.annotation.DrawableRes

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val durationMs: Long,
    val streamUrl: String,
    val description: String? = null,
    val coverUrl: String? = null,
    @DrawableRes val coverResId: Int? = null
)
