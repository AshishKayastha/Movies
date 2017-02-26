package com.ashish.movies.ui.multisearch.fragment

import com.ashish.movies.data.api.SearchApi
import com.ashish.movies.data.models.MultiSearch
import com.ashish.movies.data.models.Results
import com.ashish.movies.ui.base.recyclerview.BaseRecyclerViewMvpView
import com.ashish.movies.ui.base.recyclerview.BaseRecyclerViewPresenter
import com.ashish.movies.utils.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
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
                .observeOn(AndroidSchedulers.mainThread())
    }
}