package com.maikeruwu.substats.ui.list.mostPlayedAlbums

import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.service.formatDate
import com.maikeruwu.substats.service.loadCoverArt
import com.maikeruwu.substats.ui.list.AbstractListAdapter
import java.time.LocalDateTime

class AlbumListAdapter(
    private val albums: List<Album>,
    neverString: String,
    onItemClickListener: (position: Int) -> Unit
) : AbstractListAdapter(neverString, onItemClickListener) {
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val album = albums[position]

        holder.name.text = album.name
        holder.playCount.text = album.song?.sumOf { it.playCount }.toString()
        holder.lastPlayed.text =
            album.song?.maxByOrNull { it.played ?: LocalDateTime.MIN }?.played?.formatDate()
                ?: neverString
        holder.coverArt.loadCoverArt(album.coverArt)
    }

    override fun getItemCount(): Int {
        return albums.size
    }
}