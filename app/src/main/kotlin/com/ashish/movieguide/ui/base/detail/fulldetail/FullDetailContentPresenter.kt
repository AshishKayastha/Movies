package com.ashish.movieguide.ui.base.detail.fulldetail

import com.ashish.movieguide.R
import com.ashish.movieguide.data.network.entities.common.FullDetailContent
import com.ashish.movieguide.data.network.entities.tmdb.VideoItem
import com.ashish.movieguide.data.network.entities.tmdb.Videos
import com.ashish.movieguide.data.network.entities.tmdb.YouTubeVideo
import com.ashish.movieguide.ui.base.detail.BaseDetailPresenter
import com.ashish.movieguide.utils.extensions.isNotNullOrEmpty
import com.ashish.movieguide.utils.schedulers.BaseSchedulerProvider

/**
 * Created by Ashish on Jan 20.
 */
abstract class FullDetailContentPresenter<I, T, V : FullDetailContentView<I>>(
        schedulerProvider: BaseSchedulerProvider
) : BaseDetailPresenter<I, T, V>(schedulerProvider) {

    companion object {
        private const val YOUTUBE_SITE = "YouTube"
        private const val YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v="
        private const val YOUTUBE_THUMB_URL = "https://img.youtube.com/vi/{id}/hqdefault.jpg"
    }

    override fun showDetailContent(fullDetailContent: FullDetailContent<I, T>) {
        fullDetailContent.omdbDetail?.apply {
            if (isValidRating(imdbRating) || isValidRating(tomatoRating) || isValidRating(tomatoUserRating)
                    || isValidRating(Metascore)) {
                view?.showRatingCard()
                setMetaScore(Metascore)
                setIMDbRating(imdbRating)
                setTomatoRating(tomatoMeter, tomatoImage)
                setAudienceScore(tomatoUserRating, tomatoUserMeter)
            }
        }

        fullDetailContent.detailContent?.let { handleVideoContents(it) }
        super.showDetailContent(fullDetailContent)
    }

    protected fun setTMDbRating(voteAverage: Double?) {
        val tmdbRating = voteAverage.toString()
        if (isValidRating(tmdbRating)) {
            view?.run {
                showRatingCard()
                showTmdbRating(tmdbRating)
            }
        }
    }

    private fun setIMDbRating(imdbRating: String?) {
        if (isValidRating(imdbRating)) view?.showImdbRating(imdbRating!!)
    }

    private fun setTomatoRating(tomatoRating: String?, tomatoImage: String?) {
        if (isValidRating(tomatoRating)) {
            view?.run {
                when (tomatoImage) {
                    "certified" -> showRottenTomatoesRating(tomatoRating!!, R.drawable.ic_rt_certified)
                    "fresh" -> showRottenTomatoesRating(tomatoRating!!, R.drawable.ic_rt_fresh)
                    "rotten" -> showRottenTomatoesRating(tomatoRating!!, R.drawable.ic_rt_rotten)
                }
            }
        }
    }

    private fun setAudienceScore(tomatoUserRating: String?, tomatoUserMeter: String?) {
        if (isValidRating(tomatoUserRating)) {
            view?.run {
                val flixterScore = tomatoUserRating!!.toFloat()
                if (flixterScore >= 3.5) {
                    showAudienceScore(tomatoUserMeter!!, R.drawable.ic_audience_score_good)
                } else {
                    showAudienceScore(tomatoUserMeter!!, R.drawable.ic_audience_score_bad)
                }
            }
        }
    }

    private fun setMetaScore(metaScore: String?) {
        if (isValidRating(metaScore)) {
            view?.run {
                val metaScoreInt = metaScore!!.toInt()
                when {
                    metaScoreInt > 60 -> showMetaScore(metaScore, 0xFF66CC33.toInt())
                    metaScoreInt in 41..60 -> showMetaScore(metaScore, 0xFFFFCC33.toInt())
                    else -> showMetaScore(metaScore, 0xFFFF0000.toInt())
                }
            }
        }
    }

    private fun isValidRating(rating: String?): Boolean {
        return rating.isNotNullOrEmpty() && rating != "N/A" && rating != "0" && rating != "NA"
    }

    private fun handleVideoContents(detailContent: I) {
        val videoResults = getVideos(detailContent)?.results
        if (videoResults.isNotNullOrEmpty()) {
            showYouTubeTrailer(videoResults!!)
            showYouTubeVideos(videoResults)
        }
    }

    abstract fun getVideos(detailContent: I): Videos?

    private fun showYouTubeTrailer(videoResults: List<VideoItem>) {
        val youtubeTrailerUrl = videoResults.firstOrNull { it.site == YOUTUBE_SITE }?.key
        if (youtubeTrailerUrl.isNotNullOrEmpty()) {
            view?.showTrailerFAB(YOUTUBE_BASE_URL + youtubeTrailerUrl!!)
        }
    }

    private fun showYouTubeVideos(videoResults: List<VideoItem>) {
        val youTubeVideos = ArrayList<YouTubeVideo>()
        videoResults.filter { (_, _, site, _, key) ->
            site == YOUTUBE_SITE && key.isNotNullOrEmpty()
        }.forEach { (_, name, _, _, key) ->
            val imageUrl = YOUTUBE_THUMB_URL.replace("{id}", key!!)
            youTubeVideos.add(YouTubeVideo(name, YOUTUBE_BASE_URL + key, imageUrl))
        }

        view?.showYouTubeVideos(youTubeVideos)
    }
}