package com.ashish.movieguide.ui.multisearch.fragment

import com.ashish.movieguide.data.api.tmdb.SearchApi
import com.ashish.movieguide.data.models.MultiSearch
import com.ashish.movieguide.data.models.Results
import com.ashish.movieguide.ui.base.recyclerview.BaseRecyclerViewMvpView
import com.ashish.movieguide.ui.base.recyclerview.BaseRecyclerViewPresenter
import com.ashish.movieguide.utils.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Ashish on Jan 05.
 */
class MultiSearchPresenter @Inject constructor(
        private val searchApi: SearchApi,
        schedulerProvider: BaseSchedulerProvider
) : BaseRecyclerViewPresenter<MultiSearch, BaseRecyclerViewMvpView<MultiSearch>>(schedulerProvider) {

    private var searchQuery = ""

    fun setSearchQuery(searchQuery: String) {
        this.searchQuery = searchQuery
    }

    override fun getType(type: Int?) = searchQuery

    override fun getResultsObservable(type: String?, page: Int): Observable<Results<MultiSearch>> {
        return searchApi.getMultiSearchResults(type!!, page)
                .observeOn(schedulerProvider.ui())
    }
}