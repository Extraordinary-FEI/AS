package com.example.cn.helloworld.service;

import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;

/**
 * 播放控制接口，预留给后续播放器实现。
 */
public interface PlaybackController {

    /**
     * 绑定需要播放的歌单，播放器可自行处理播放列表逻辑。
     */
    void setPlaylist(Playlist playlist);

    /** 播放指定歌曲。 */
    void play(Song song);

    /** 暂停当前播放。 */
    void pause();

    /** 继续播放。 */
    void resume();

    /** 停止播放并释放资源。 */
    void stop();

    /**
     * 跳转到指定的播放进度。
     *
     * @param positionMs 目标进度（毫秒）。
     */
    void seekTo(long positionMs);

    /** 播放下一首。 */
    void skipToNext();

    /** 播放上一首。 */
    void skipToPrevious();
}
