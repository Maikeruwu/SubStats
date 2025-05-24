package com.maikeruwu.substats.ui.statistics.list.mostPlayedSongs

import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.formatDate
import com.maikeruwu.substats.service.loadCoverArt
import com.maikeruwu.substats.ui.statistics.list.AbstractListAdapter

class SongListAdapter(
    private val songs: List<Song>,
    neverString: String,
    onItemClickListener: (position: Int) -> Unit
) : AbstractListAdapter(neverString, onItemClickListener) {
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val song = songs[position]
        holder.name.text = song.title
        holder.playCount.text = song.playCount.toString()
        holder.lastPlayed.text = song.played?.formatDate() ?: neverString
        holder.coverArt.loadCoverArt(song.coverArt)
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}