package com.maikeruwu.substats.ui.statistics.list.mostPlayedAlbums

import coil.load
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.ui.statistics.list.AbstractListAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AlbumSongsAdapter(
    private val albumSongs: Map<Album, List<Song>>,
    neverString: String
) : AbstractListAdapter(neverString) {
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val pair = albumSongs.entries.elementAt(position)

        holder.name.text = pair.key.name
        holder.playCount.text = pair.value.sumOf { song -> song.playCount }.toString()
        holder.lastPlayed.text =
            pair.value.maxByOrNull { it.played ?: LocalDateTime.MIN }?.played?.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ) ?: neverString
        holder.coverArt.load(getCoverArtUri(pair.value.firstOrNull()?.coverArt.orEmpty())) {
            placeholder(R.drawable.outline_music_note_95)
            error(R.drawable.outline_music_note_95)
        }
    }

    override fun getItemCount(): Int {
        return albumSongs.size
    }
}