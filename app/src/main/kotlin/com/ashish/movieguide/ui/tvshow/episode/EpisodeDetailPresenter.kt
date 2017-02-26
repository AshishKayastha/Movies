package com.ashish.movieguide.ui.tvshow.episode

import com.ashish.movieguide.R
import com.ashish.movieguide.data.interactors.TVShowInteractor
import com.ashish.movieguide.data.models.EpisodeDetail
import com.ashish.movieguide.data.models.FullDetailContent
import com.ashish.movieguide.di.scopes.ActivityScope
import com.ashish.movieguide.ui.base.detail.fulldetail.FullDetailContentPresenter
import com.ashish.movieguide.ui.base.detail.fulldetail.FullDetailContentView
import com.ashish.movieguide.utils.extensions.getFormattedMediumDate
import com.ashish.movieguide.utils.schedulers.BaseSchedulerProvider
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by Ashish on Jan 08.
 */
@ActivityScope
class EpisodeDetailPresenter @Inject constructor(
        private val tvShowInteractor: TVShowInteractor,
        schedulerProvider: BaseSchedulerProvider
) : FullDetailContentPresenter<EpisodeDetail, FullDetailContentView<EpisodeDetail>>(schedulerProvider) {

    private var seasonNumber: Int = 1
    private var episodeNumber: Int = 1

    fun setSeasonAndEpisodeNumber(seasonNumber: Int, episodeNumber: Int) {
        this.seasonNumber = seasonNumber
        this.episodeNumber = episodeNumber
    }

    override fun getDetailContent(id: Long) = tvShowInteractor.getFullEpisodeDetail(id, seasonNumber, episodeNumber)

    override fun showDetailContent(fullDetailContent: FullDetailContent<EpisodeDetail>) {
        super.showDetailContent(fullDetailContent)
        setTMDbRating(fullDetailContent.detailContent?.voteAverage)
    }

    override fun getContentList(fullDetailContent: FullDetailContent<EpisodeDetail>): List<String> {
        val contentList = ArrayList<String>()
        fullDetailContent.detailContent?.apply {
            val omdbDetail = fullDetailContent.omdbDetail
            contentList.apply {
                add(overview ?: "")
                add(omdbDetail?.Rated ?: "")
                add(omdbDetail?.Awards ?: "")
                add(seasonNumber.toString())
                add(episodeNumber.toString())
                add(airDate.getFormattedMediumDate())
                add(omdbDetail?.Production ?: "")
                add(omdbDetail?.Country ?: "")
                add(omdbDetail?.Language ?: "")
            }
        }

        return contentList
    }

    override fun getBackdropImages(detailContent: EpisodeDetail) = detailContent.images?.backdrops

    override fun getPosterImages(detailContent: EpisodeDetail) = detailContent.images?.posters

    override fun getVideos(detailContent: EpisodeDetail) = detailContent.videos

    override fun getCredits(detailContent: EpisodeDetail) = detailContent.credits

    override fun getErrorMessageId() = R.string.error_load_episode_detail
}