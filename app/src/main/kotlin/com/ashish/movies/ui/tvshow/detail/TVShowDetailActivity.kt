package com.ashish.movies.ui.tvshow.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import butterknife.bindView
import com.ashish.movies.R
import com.ashish.movies.data.models.Credit
import com.ashish.movies.data.models.People
import com.ashish.movies.data.models.TVShow
import com.ashish.movies.data.models.TVShowDetail
import com.ashish.movies.di.components.AppComponent
import com.ashish.movies.ui.base.detail.BaseDetailActivity
import com.ashish.movies.ui.common.adapter.OnItemClickListener
import com.ashish.movies.ui.common.adapter.RecyclerViewAdapter
import com.ashish.movies.ui.common.adapter.RecyclerViewAdapter.Companion.ADAPTER_TYPE_TV_SHOW
import com.ashish.movies.ui.people.detail.PeopleDetailActivity
import com.ashish.movies.ui.widget.FontTextView
import com.ashish.movies.utils.Constants.BACKDROP_W780_URL_PREFIX
import com.ashish.movies.utils.Constants.NOT_AVAILABLE
import com.ashish.movies.utils.Constants.POSTER_W500_URL_PREFIX
import com.ashish.movies.utils.extensions.convertListToCommaSeparatedText
import com.ashish.movies.utils.extensions.getFormattedReleaseDate
import com.ashish.movies.utils.extensions.isNotNullOrEmpty
import com.ashish.movies.utils.extensions.setTitleAndYear

/**
 * Created by Ashish on Jan 03.
 */
class TVShowDetailActivity : BaseDetailActivity<TVShowDetail, TVShowDetailMvpView, TVShowDetailPresenter>(),
        TVShowDetailMvpView {

    private val statusText: FontTextView by bindView(R.id.status_text)
    private val genresText: FontTextView by bindView(R.id.genres_text)
    private val seasonsText: FontTextView by bindView(R.id.season_text)
    private val networkText: FontTextView by bindView(R.id.network_text)
    private val episodesText: FontTextView by bindView(R.id.episodes_text)
    private val lastAirDateText: FontTextView by bindView(R.id.last_air_date_text)
    private val firstAirDateText: FontTextView by bindView(R.id.first_air_date_text)
    private val similarTVShowsViewStub: ViewStub by bindView(R.id.similar_content_view_stub)

    private var tvShow: TVShow? = null
    private lateinit var similarTVShowsAdapter: RecyclerViewAdapter<TVShow>

    private val onCastItemClickListener = object : OnItemClickListener {
        override fun onItemClick(position: Int, view: View) {
            onTVShowCreditItemClicked(castAdapter, position, view)
        }
    }

    private val onCrewItemClickListener = object : OnItemClickListener {
        override fun onItemClick(position: Int, view: View) {
            onTVShowCreditItemClicked(crewAdapter, position, view)
        }
    }

    private fun onTVShowCreditItemClicked(adapter: RecyclerViewAdapter<Credit>?, position: Int, view: View) {
        val credit = adapter?.getItem<Credit>(position)
        val people = People(credit?.id, credit?.name, profilePath = credit?.profilePath)
        val intent = PeopleDetailActivity.createIntent(this@TVShowDetailActivity, people)
        startActivityWithTransition(R.string.transition_poster_image, view, intent)
    }

    private val onSimilarTVShowItemClickLitener = object : OnItemClickListener {
        override fun onItemClick(position: Int, view: View) {
            val tvShow = similarTVShowsAdapter.getItem<TVShow>(position)
            val intent = TVShowDetailActivity.createIntent(this@TVShowDetailActivity, tvShow)
            startActivityWithTransition(R.string.transition_poster_image, view, intent)
        }
    }

    companion object {
        const val EXTRA_TV_SHOW = "tv_show"

        fun createIntent(context: Context, tvShow: TVShow?): Intent {
            return Intent(context, TVShowDetailActivity::class.java)
                    .putExtra(EXTRA_TV_SHOW, tvShow)
        }
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(TVShowDetailModule()).inject(this)
    }

    override fun getLayoutId() = R.layout.acivity_detail_tv_show

    override fun getIntentExtras(extras: Bundle?) {
        tvShow = extras?.getParcelable(EXTRA_TV_SHOW)
    }

    override fun loadDetailContent() = presenter.loadDetailContent(tvShow?.id)

    override fun getBackdropPath(): String {
        val backdropPath = tvShow?.backdropPath
        return if (backdropPath.isNotNullOrEmpty()) BACKDROP_W780_URL_PREFIX + backdropPath else ""
    }

    override fun getPosterPath(): String {
        val posterPath = tvShow?.posterPath
        return if (posterPath.isNotNullOrEmpty()) POSTER_W500_URL_PREFIX + posterPath else ""
    }

    override fun showDetailContent(detailContent: TVShowDetail?) {
        detailContent?.apply {
            if (getBackdropPath().isNullOrEmpty() && backdropPath.isNotNullOrEmpty()) {
                showBackdropImage(BACKDROP_W780_URL_PREFIX + backdropPath)
            }

            titleText.setTitleAndYear(name, firstAirDate)
            overviewText.text = overview ?: NOT_AVAILABLE
            statusText.text = status ?: NOT_AVAILABLE
            seasonsText.text = numberOfSeasons.toString()
            episodesText.text = numberOfEpisodes.toString()
            genresText.text = genres.convertListToCommaSeparatedText { it.name.toString() }
            networkText.text = networks.convertListToCommaSeparatedText { it.name.toString() }
            lastAirDateText.text = lastAirDate.getFormattedReleaseDate(this@TVShowDetailActivity)
            firstAirDateText.text = firstAirDate.getFormattedReleaseDate(this@TVShowDetailActivity)
        }
        super.showDetailContent(detailContent)
    }

    override fun getItemTitle(): String = tvShow?.name ?: ""

    override fun getCastItemClickListener() = onCastItemClickListener

    override fun getCrewItemClickListener() = onCrewItemClickListener

    override fun showSimilarTVShowList(similarTVShowList: List<TVShow>) {
        similarTVShowsAdapter = RecyclerViewAdapter(R.layout.list_item_content_alt, ADAPTER_TYPE_TV_SHOW,
                onSimilarTVShowItemClickLitener)

        similarTVShowsViewStub.setOnInflateListener { viewStub, view ->
            val textView = view.findViewById(R.id.similar_content_title) as FontTextView
            textView.setText(R.string.similar_tv_shows_title)
        }

        inflateViewStubRecyclerView(similarTVShowsViewStub, R.id.similar_content_recycler_view,
                similarTVShowsAdapter, similarTVShowList)
    }
}