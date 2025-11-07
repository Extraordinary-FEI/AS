package com.example.cn.helloworld.ui.playlist

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.cn.helloworld.R
import com.example.cn.helloworld.data.model.Playlist
import com.example.cn.helloworld.data.model.Song
import java.util.Locale

class PlaylistFragment : Fragment(), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    private lateinit var playlistCover: ImageView
    private lateinit var playlistTitle: TextView
    private lateinit var playlistDescription: TextView
    private lateinit var playlistMeta: TextView
    private lateinit var songsRecyclerView: RecyclerView
    private lateinit var songsAdapter: PlaylistSongsAdapter

    private lateinit var viewModel: PlaylistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistCover = view.findViewById(R.id.image_playlist_cover)
        playlistTitle = view.findViewById(R.id.text_playlist_title)
        playlistDescription = view.findViewById(R.id.text_playlist_description)
        playlistMeta = view.findViewById(R.id.text_playlist_meta)
        songsRecyclerView = view.findViewById(R.id.recycler_playlist_songs)

        songsAdapter = PlaylistSongsAdapter { song ->
            val context = view.context
            Toast.makeText(context, "准备播放：${song.title}", Toast.LENGTH_SHORT).show()
        }

        songsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songsAdapter
            setHasFixedSize(true)
        }

        viewModel = ViewModelProviders.of(this).get(PlaylistViewModel::class.java)
        viewModel.playlist.observe(this, Observer { playlist ->
            if (playlist != null) {
                bindPlaylist(playlist)
            }
        })
        viewModel.songs.observe(this, Observer { songs ->
            songsAdapter.submitList(songs)
        })
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    private fun bindPlaylist(playlist: Playlist) {
        playlistTitle.text = playlist.title
        playlistDescription.text = playlist.description
        playlistMeta.text = buildMeta(playlist)

        val coverResId = playlist.coverResId
        if (coverResId != null) {
            playlistCover.setImageResource(coverResId)
        } else {
            playlistCover.setImageResource(R.drawable.cover_lisao)
        }
    }

    private fun buildMeta(playlist: Playlist): String {
        val count = playlist.songs.size
        val totalDurationMs = playlist.songs.sumByDouble { it.durationMs.toDouble() }
        val totalMinutes = (totalDurationMs / 1000 / 60).toInt()
        val tags = if (playlist.tags.isNotEmpty()) playlist.tags.joinToString(" / ") else ""
        return when {
            tags.isNotEmpty() -> String.format(Locale.getDefault(), "共 %d 首 · %d 分钟 · %s", count, totalMinutes, tags)
            else -> String.format(Locale.getDefault(), "共 %d 首 · %d 分钟", count, totalMinutes)
        }
    }

    private class PlaylistSongsAdapter(
        private val onItemClick: (Song) -> Unit
    ) : SimpleListAdapter<Song, PlaylistSongsAdapter.SongViewHolder>(SongDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
            return SongViewHolder(view, onItemClick)
        }

        override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        private class SongViewHolder(
            itemView: View,
            private val onItemClick: (Song) -> Unit
        ) : RecyclerView.ViewHolder(itemView) {

            private val coverView: ImageView = itemView.findViewById(R.id.image_song_cover)
            private val titleView: TextView = itemView.findViewById(R.id.text_song_title)
            private val artistView: TextView = itemView.findViewById(R.id.text_song_artist)
            private val durationView: TextView = itemView.findViewById(R.id.text_song_duration)
            private val descriptionView: TextView = itemView.findViewById(R.id.text_song_description)

            fun bind(song: Song) {
                titleView.text = song.title
                artistView.text = song.artist
                durationView.text = formatDuration(song.durationMs)

                val description = song.description
                if (description.isNullOrEmpty()) {
                    descriptionView.visibility = View.GONE
                } else {
                    descriptionView.visibility = View.VISIBLE
                    descriptionView.text = description
                }

                val coverRes = song.coverResId
                if (coverRes != null) {
                    coverView.setImageResource(coverRes)
                } else {
                    coverView.setImageResource(R.drawable.cover_baobei)
                }

                itemView.setOnClickListener { onItemClick(song) }
            }

            private fun formatDuration(durationMs: Long): String {
                val totalSeconds = durationMs / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }
        }

        private class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem == newItem
        }
    }

    private abstract class SimpleListAdapter<T, VH : RecyclerView.ViewHolder>(
        private val diffCallback: DiffUtil.ItemCallback<T>
    ) : RecyclerView.Adapter<VH>() {

        private val items = mutableListOf<T>()

        protected fun getItem(position: Int): T = items[position]

        override fun getItemCount(): Int = items.size

        fun submitList(newItems: List<T>?) {
            val targetItems = newItems ?: emptyList()
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = items.size

                override fun getNewListSize(): Int = targetItems.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return diffCallback.areItemsTheSame(
                        items[oldItemPosition],
                        targetItems[newItemPosition]
                    )
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return diffCallback.areContentsTheSame(
                        items[oldItemPosition],
                        targetItems[newItemPosition]
                    )
                }
            })
            items.clear()
            items.addAll(targetItems)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    companion object {
        fun newInstance(): PlaylistFragment = PlaylistFragment()
    }
}
