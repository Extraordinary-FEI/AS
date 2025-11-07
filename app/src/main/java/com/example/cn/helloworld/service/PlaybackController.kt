package com.example.cn.helloworld.service

import com.example.cn.helloworld.data.model.Playlist
import com.example.cn.helloworld.data.model.Song

/**
 * 播放控制接口，预留与播放器或远程服务的交互能力。
 */
interface PlaybackController {

    /**
     * 绑定需要播放的歌单，播放器可自行处理播放列表逻辑。
     */
    fun setPlaylist(playlist: Playlist)

    /**
     * 播放指定歌曲。
     */
    fun play(song: Song)

    /** 暂停当前播放。 */
    fun pause()

    /** 继续播放。 */
    fun resume()

    /** 停止播放并释放资源。 */
    fun stop()

    /**
     * 跳转到指定的播放进度。
     *
     * @param positionMs 目标进度（毫秒）。
     */
    fun seekTo(positionMs: Long)

    /** 播放下一首。 */
    fun skipToNext()

    /** 播放上一首。 */
    fun skipToPrevious()
}
