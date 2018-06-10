package com.ashish.movieguide.data.network.entities.tmdb

import android.os.Parcelable
import com.ashish.movieguide.ui.common.adapter.RecyclerViewItem
import com.ashish.movieguide.ui.common.adapter.RecyclerViewItem.Companion.CONTENT_VIEW
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Person(
        val id: Long? = null,
        val name: String? = null,
        @Json(name = "profile_path") val profilePath: String? = null
) : RecyclerViewItem, Parcelable {

    override fun getViewType() = CONTENT_VIEW
}