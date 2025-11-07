package com.example.cn.helloworld.ui.playlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.example.cn.helloworld.R
import com.example.cn.helloworld.data.model.Playlist
import com.example.cn.helloworld.data.model.Song

class PlaylistViewModel : ViewModel() {

    private val playlistLiveData = MutableLiveData<Playlist>()

    val playlist: LiveData<Playlist> = playlistLiveData

    val songs: LiveData<List<Song>> = Transformations.map(playlistLiveData) { playlist ->
        playlist?.songs ?: emptyList()
    }

    init {
        loadMockPlaylist()
    }

    private fun loadMockPlaylist() {
        val mockSongs = listOf(
            Song(
                id = "song-lisao",
                title = "离骚",
                artist = "许嵩",
                durationMs = 245_000,
                streamUrl = "https://example.com/audio/li_sao.mp3",
                description = "沉静悠扬的钢琴旋律，适合夜晚放松",
                coverResId = R.drawable.cover_lisao
            ),
            Song(
                id = "song-nishuo",
                title = "你说",
                artist = "林俊杰",
                durationMs = 214_000,
                streamUrl = "https://example.com/audio/ni_shuo.mp3",
                description = "温柔治愈系，诉说心底的故事",
                coverResId = R.drawable.cover_nishuo
            ),
            Song(
                id = "song-baobei",
                title = "宝贝",
                artist = "张悬",
                durationMs = 198_000,
                streamUrl = "https://example.com/audio/bao_bei.mp3",
                description = "轻快民谣，伴你醒来迎接阳光",
                coverResId = R.drawable.cover_baobei
            )
        )

        val playlist = Playlist(
            id = "playlist-classic",
            title = "轻听华语 · 治愈精选",
            description = "精选 2000 年后治愈系华语歌曲，适合午后阅读或夜晚放松聆听。",
            playUrl = "https://example.com/playlist/classic-heal",
            coverResId = R.drawable.cover_lisao,
            tags = listOf("华语", "治愈", "安静"),
            songs = mockSongs
        )

        playlistLiveData.value = playlist
    }
}
