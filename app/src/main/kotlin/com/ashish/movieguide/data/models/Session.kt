package com.ashish.movieguide.data.models

import com.squareup.moshi.Json

data class Session(
        val success: Boolean = false,
        @Json(name = "session_id") val sessionId: String? = null
)