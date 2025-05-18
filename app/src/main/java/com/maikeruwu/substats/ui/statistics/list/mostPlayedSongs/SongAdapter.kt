package com.maikeruwu.substats.ui.statistics.list.mostPlayedSongs

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.SecureStorage
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.SubsonicAuthInterceptor
import okhttp3.HttpUrl
import java.time.format.DateTimeFormatter

class SongAdapter(
    private val songs: List<Song>,
    private val neverString: String
) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
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

    private fun getCoverArtUri(id: String): Uri {
        val baseUrl = SecureStorage.get(SecureStorage.Key.BASE_URL).orEmpty()

        // a base url looks like this: https://host/path/to/subsonic/
        val scheme = baseUrl.substringBefore("://")
        val host = baseUrl.substringAfter("://").substringBefore("/")
        val path = baseUrl.substringAfter("://").substringAfter("/")

        val builder = HttpUrl.Builder()
            .scheme(scheme)
            .host(host)
            .addPathSegments(path)
            .addPathSegment(SubsonicApiProvider.REST_SUFFIX)
            .addPathSegment("getCoverArt.view")
            .addQueryParameter("id", id)
        return SubsonicAuthInterceptor().applyQueryParams(builder).build().toString().toUri()
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val playCount: TextView = itemView.findViewById(R.id.valuePlayCount)
        val lastPlayed: TextView = itemView.findViewById(R.id.valueLastPlayed)
        val coverArt: ImageView = itemView.findViewById(R.id.coverArt)
    }
}