package com.ashish.movieguide.data.network.entities.tmdb

import android.os.Parcelable
import com.ashish.movieguide.ui.common.adapter.RecyclerViewItem
import com.ashish.movieguide.ui.common.adapter.RecyclerViewItem.Companion.CONTENT_VIEW
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Season(
        val id: Long? = null,
        @Json(name = "air_date") val airDate: String? = null,
        @Json(name = "poster_path") val posterPath: String? = null,
        @Json(name = "season_number") val seasonNumber: Int? = null,
        @Json(name = "episode_count") val episodeCount: Int? = null
) : RecyclerViewItem, Parcelable {

    override fun getViewType() = CONTENT_VIEW
}