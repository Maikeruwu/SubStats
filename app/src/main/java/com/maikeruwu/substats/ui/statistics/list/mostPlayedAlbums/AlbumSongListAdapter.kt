package com.maikeruwu.substats.ui.statistics.list.mostPlayedAlbums

import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.formatDate
import com.maikeruwu.substats.service.loadCoverArt
import com.maikeruwu.substats.ui.statistics.list.AbstractListAdapter
import java.time.LocalDateTime

class AlbumSongListAdapter(
    private val albumSongs: Map<Album, List<Song>>,
    neverString: String,
    onItemClickListener: (position: Int) -> Unit
) : AbstractListAdapter(neverString, onItemClickListener) {
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val pair = albumSongs.entries.elementAt(position)

        holder.name.text = pair.key.name
        holder.playCount.text = pair.value.sumOf { song -> song.playCount }.toString()
        holder.lastPlayed.text =
            pair.value.maxByOrNull { it.played ?: LocalDateTime.MIN }?.played?.formatDate()
                ?: neverString
        holder.coverArt.loadCoverArt(pair.value.firstOrNull()?.coverArt.orEmpty())
    }

    override fun getItemCount(): Int {
        return albumSongs.size
    }
}