package com.ashish.movies.ui.tvshow.detail

import com.ashish.movies.data.models.TVShow
import com.ashish.movies.data.models.TVShowDetail
import com.ashish.movies.ui.base.detail.BaseDetailMvpView

/**
 * Created by Ashish on Jan 04.
 */
interface TVShowDetailMvpView : BaseDetailMvpView<TVShowDetail> {

    fun showSimilarTVShowList(similarTVShowList: List<TVShow>)
}