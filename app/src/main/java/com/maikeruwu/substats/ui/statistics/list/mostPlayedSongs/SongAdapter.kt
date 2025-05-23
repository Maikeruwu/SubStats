package com.maikeruwu.substats.ui.statistics.list.mostPlayedSongs

import coil.load
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.ui.statistics.list.AbstractListAdapter
import java.time.format.DateTimeFormatter

class SongAdapter(
    private val songs: List<Song>, neverString: String,
) : AbstractListAdapter(neverString) {
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val song = songs[position]
        holder.name.text = song.title
        holder.playCount.text = song.playCount.toString()
        holder.lastPlayed.text =
            song.played?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ?: neverString
        holder.coverArt.load(getCoverArtUri(song.coverArt)) {
            placeholder(R.drawable.outline_music_note_95)
            error(R.drawable.outline_music_note_95)
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}