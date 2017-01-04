package com.ashish.movies.ui.movie.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.View
import android.view.ViewStub
import butterknife.bindView
import com.ashish.movies.R
import com.ashish.movies.data.models.Movie
import com.ashish.movies.data.models.MovieDetail
import com.ashish.movies.di.components.AppComponent
import com.ashish.movies.ui.base.detail.BaseDetailActivity
import com.ashish.movies.ui.common.adapter.OnItemClickListener
import com.ashish.movies.ui.common.adapter.RecyclerViewAdapter
import com.ashish.movies.ui.common.adapter.RecyclerViewAdapter.Companion.ADAPTER_TYPE_MOVIE
import com.ashish.movies.ui.widget.FontTextView
import com.ashish.movies.utils.Constants.BACKDROP_W780_URL_PREFIX
import com.ashish.movies.utils.Constants.NOT_AVAILABLE
import com.ashish.movies.utils.Constants.POSTER_W500_URL_PREFIX
import com.ashish.movies.utils.extensions.getFormattedGenres
import com.ashish.movies.utils.extensions.getFormattedNumber
import com.ashish.movies.utils.extensions.getFormattedReleaseDate
import com.ashish.movies.utils.extensions.getFormattedRuntime
import com.ashish.movies.utils.extensions.isNotNullOrEmpty
import com.ashish.movies.utils.extensions.setTitleAndYear

/**
 * Created by Ashish on Dec 31.
 */
class MovieDetailActivity : BaseDetailActivity<MovieDetail, MovieDetailMvpView, MovieDetailPresenter>(), MovieDetailMvpView,
        AppBarLayout.OnOffsetChangedListener {

    private val statusText: FontTextView by bindView(R.id.status_text)
    private val budgetText: FontTextView by bindView(R.id.budget_text)
    private val genresText: FontTextView by bindView(R.id.genres_text)
    private val runtimeText: FontTextView by bindView(R.id.runtime_text)
    private val revenueText: FontTextView by bindView(R.id.revenue_text)
    private val taglineText: FontTextView by bindView(R.id.tagline_text)
    private val releaseDateText: FontTextView by bindView(R.id.release_date_text)
    private val similarMoviesViewStub: ViewStub by bindView(R.id.similar_content_view_stub)

    private var movie: Movie? = null
    private var similarMoviesAdapter: RecyclerViewAdapter<Movie>? = null

    companion object {
        const val EXTRA_MOVIE = "movie"

        fun createIntent(context: Context, movie: Movie?): Intent {
            return Intent(context, MovieDetailActivity::class.java)
                    .putExtra(EXTRA_MOVIE, movie)
        }
    }

    private val onSimilarMovieItemClickLitener = object : OnItemClickListener {
        override fun onItemClick(position: Int, view: View) {
            val movie = similarMoviesAdapter?.getItem<Movie>(position)
            val intent = createIntent(this@MovieDetailActivity, movie)
            startActivityWithTransition(R.string.transition_poster_image, view, intent)
        }
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(MovieDetailModule()).inject(this)
    }

    override fun getLayoutId() = R.layout.activity_detail_movie

    override fun getIntentExtras(extras: Bundle?) {
        movie = extras?.getParcelable(EXTRA_MOVIE)
    }

    override fun loadDetailContent() = presenter.loadDetailContent(movie?.id)

    override fun getBackdropPath(): String {
        val backdropPath = movie?.backdropPath
        return if (backdropPath.isNotNullOrEmpty()) BACKDROP_W780_URL_PREFIX + backdropPath else ""
    }

    override fun getPosterPath(): String {
        val posterPath = movie?.posterPath
        return if (posterPath.isNotNullOrEmpty()) POSTER_W500_URL_PREFIX + posterPath else ""
    }

    override fun getItemTitle(): String = movie?.title ?: ""

    override fun showDetailContent(detailContent: MovieDetail?) {
        detailContent?.apply {
            titleText.setTitleAndYear(title, releaseDate)
            overviewText.text = overview ?: NOT_AVAILABLE
            taglineText.text = tagline ?: NOT_AVAILABLE
            genresText.text = genres.getFormattedGenres()
            statusText.text = status ?: NOT_AVAILABLE
            budgetText.text = budget.getFormattedNumber()
            revenueText.text = revenue.getFormattedNumber()
            runtimeText.text = runtime.getFormattedRuntime()
            releaseDateText.text = releaseDate.getFormattedReleaseDate(this@MovieDetailActivity)
        }
        super.showDetailContent(detailContent)
    }

    override fun showSimilarMoviesList(similarMoviesList: List<Movie>) {
        similarMoviesAdapter = RecyclerViewAdapter(R.layout.list_item_content_alt, ADAPTER_TYPE_MOVIE, onSimilarMovieItemClickLitener)
        inflateViewStubRecyclerView(similarMoviesViewStub, R.id.similar_content_recycler_view,
                similarMoviesAdapter!!, similarMoviesList)
    }

    override fun performCleanup() {
        similarMoviesAdapter?.removeListener()
        super.performCleanup()
    }
}