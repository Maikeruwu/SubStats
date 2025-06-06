package com.maikeruwu.substats.service

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import coil.imageLoader
import coil.load
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.exception.SubsonicException
import com.maikeruwu.substats.service.endpoint.AbstractSubsonicService
import com.maikeruwu.substats.ui.list.AbstractListViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

// -- image loading
private val httpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        var attempt = 0
        val maxRetries = 3
        while (true) {
            try {
                return@addInterceptor chain.proceed(chain.request())
            } catch (e: SocketTimeoutException) {
                if (++attempt > maxRetries) throw e
            }
        }
        return@addInterceptor chain.proceed(chain.request())
    }
    .build()

fun ImageView.loadCoverArt(id: String) {
    val newImageLoader = context.imageLoader.newBuilder()
        .okHttpClient(httpClient)
        .build()
    val uri = getCoverArtUri(id)
    Log.d("SubStats", "Loading cover art from URI: $uri")
    this.load(uri, newImageLoader) {
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
    return this.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
}

// time formatting
fun Int.formatDuration(): String {
    val hours = this / 3600
    val seconds = this % 60
    return if (hours > 0)
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, (this % 3600) / 60, seconds)
    else
        String.format(Locale.getDefault(), "%02d:%02d", this / 60, seconds)
}

// empty checks
fun String?.isEmptyCardValue(): Boolean {
    return this.isNullOrEmpty() || this == "0" || this == "00:00"
}

// boolean formatting
fun Boolean.formatBoolean(): Int {
    return if (this) {
        R.string.yes
    } else {
        R.string.no
    }
}

// handler
fun Fragment.getHandler(
    viewModel: AbstractListViewModel,
    dataIsEmpty: Boolean,
    jobs: MutableList<Job>? = null
): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { _, exception ->
        var message =
            if (!dataIsEmpty && exception is SocketTimeoutException) null
            else when (exception) {
                is HttpException -> {
                    getString(
                        R.string.response_error_code,
                        exception.code()
                    ) + if (exception.message().isNotEmpty()) getString(
                        R.string.response_error_message,
                        exception.message()
                    ) else ""
                }

                is SubsonicException -> {
                    getString(
                        R.string.response_error_code,
                        exception.code
                    ) + if (exception.message.isNotEmpty()) getString(
                        R.string.response_error_message,
                        exception.message
                    ) else ""
                }

                else -> {
                    getString(R.string.response_failed)
                }
            }
        if (message != null) {
            jobs?.forEach {
                it.cancel(exception.message.orEmpty(), exception)
                it.cancelChildren()
            }
            viewModel.setErrorText(
                message
            )
        }
    }
}

// service checks
fun Fragment.assertServicesAvailable(
    viewModel: AbstractListViewModel,
    vararg services: AbstractSubsonicService?
): Boolean {
    for (service in services) {
        if (service == null) {
            viewModel.setErrorText(getString(R.string.invalid_base_url))
            return false
        }
    }
    return true
}
