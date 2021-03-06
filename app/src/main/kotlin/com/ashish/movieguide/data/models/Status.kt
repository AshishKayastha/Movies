package com.ashish.movieguide.data.models

import com.squareup.moshi.Json

data class Status(
        val success: Boolean? = null,
        @Json(name = "status_code") val statusCode: Int? = null,
        @Json(name = "status_message") val statusMessage: String? = null
)