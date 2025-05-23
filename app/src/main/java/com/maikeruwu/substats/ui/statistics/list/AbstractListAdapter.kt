package com.maikeruwu.substats.ui.statistics.list

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.maikeruwu.substats.R
import com.maikeruwu.substats.service.SecureStorage
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.SubsonicAuthInterceptor
import okhttp3.HttpUrl

abstract class AbstractListAdapter(
    protected val neverString: String
) : RecyclerView.Adapter<AbstractListAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_song, parent, false)
        return ListViewHolder(view)
    }

    protected fun getCoverArtUri(id: String): Uri {
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

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: RelativeLayout = itemView.findViewById(R.id.cardSong)
        val name: TextView = itemView.findViewById(R.id.name)
        val playCount: TextView = itemView.findViewById(R.id.valuePlayCount)
        val lastPlayed: TextView = itemView.findViewById(R.id.valueLastPlayed)
        val coverArt: ImageView = itemView.findViewById(R.id.coverArt)

        init {
            card.setOnClickListener {
                Toast.makeText(it.context, name.text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}