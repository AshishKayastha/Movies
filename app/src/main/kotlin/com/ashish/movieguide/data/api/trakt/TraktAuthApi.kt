package com.ashish.movieguide.data.api.trakt

import com.ashish.movieguide.data.models.trakt.TokenRequest
import com.ashish.movieguide.data.models.trakt.TraktToken
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface TraktAuthApi {

    @POST("oauth/token")
    fun getAccessToken(@Body tokenRequest: TokenRequest): Single<TraktToken>
}