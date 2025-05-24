package com.maikeruwu.substats.service

import android.net.Uri
import android.widget.ImageView
import androidx.core.net.toUri
import coil.load
import com.maikeruwu.substats.R
import okhttp3.HttpUrl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// -- image loading
fun ImageView.loadCoverArt(id: String) {
    val uri = getCoverArtUri(id)
    android.util.Log.d("CoverArt", "Loading URI: $uri")
    this.load(uri) {
        placeholder(R.drawable.outline_music_note_95)
        error(R.drawable.outline_music_note_95)
    }
}

fun getCoverArtUri(id: String): Uri {
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

// date formatting
fun LocalDateTime.formatDate(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

// time formatting
fun Int.formatDuration(): String {
    return String.format(Locale.getDefault(), "%02d:%02d", this / 60, this % 60)
}

// empty checks
fun String?.isEmptyCardValue(): Boolean {
    return this.isNullOrEmpty() || this == "0" || this == "00:00"
}