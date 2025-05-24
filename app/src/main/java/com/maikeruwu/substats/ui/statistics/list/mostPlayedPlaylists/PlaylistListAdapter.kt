package com.maikeruwu.substats.ui.statistics.list.mostPlayedPlaylists

import com.maikeruwu.substats.model.data.Playlist
import com.maikeruwu.substats.service.formatDate
import com.maikeruwu.substats.service.loadCoverArt
import com.maikeruwu.substats.ui.statistics.list.AbstractListAdapter
import java.time.LocalDateTime

class PlaylistListAdapter(
    private val playlists: List<Playlist>,
    neverString: String,
    onItemClickListener: (position: Int) -> Unit
) : AbstractListAdapter(neverString, onItemClickListener) {
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val playlist = playlists[position]

        holder.name.text = playlist.name
        holder.playCount.text = playlist.entry?.sumOf { song -> song.playCount }.toString()
        holder.lastPlayed.text =
            playlist.entry?.maxByOrNull { it.played ?: LocalDateTime.MIN }?.played?.formatDate()
                ?: neverString
        holder.coverArt.loadCoverArt(playlist.coverArt)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}