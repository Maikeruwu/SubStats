package com.maikeruwu.substats.ui.statistics.list.mostPlayedGenres

import com.maikeruwu.substats.model.data.Genre
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.formatDate
import com.maikeruwu.substats.service.loadCoverArt
import com.maikeruwu.substats.ui.statistics.list.AbstractListAdapter
import java.time.LocalDateTime

class GenreSongListAdapter(
    private val genreSongs: Map<Genre, List<Song>>,
    neverString: String,
    onItemClickListener: (position: Int) -> Unit
) : AbstractListAdapter(neverString, onItemClickListener) {
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val pair = genreSongs.entries.elementAt(position)

        holder.name.text = pair.key.value
        holder.playCount.text = pair.value.sumOf { it.playCount }.toString()
        holder.lastPlayed.text =
            pair.value.maxByOrNull { it.played ?: LocalDateTime.MIN }?.played?.formatDate()
                ?: neverString
        holder.coverArt.loadCoverArt(pair.value.firstOrNull()?.coverArt.orEmpty())
    }

    override fun getItemCount(): Int {
        return genreSongs.size
    }
}