package com.ashish.movieguide.ui.tvshow.detail

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.view.menu.ActionMenuItemView
import android.view.MenuItem
import android.view.View
import com.ashish.movieguide.R
import com.ashish.movieguide.data.models.tmdb.Season
import com.ashish.movieguide.data.models.tmdb.TVShow
import com.ashish.movieguide.data.models.tmdb.TVShowDetail
import com.ashish.movieguide.data.models.trakt.TraktShow
import com.ashish.movieguide.data.preferences.PreferenceHelper
import com.ashish.movieguide.di.modules.ActivityModule
import com.ashish.movieguide.di.multibindings.activity.ActivityComponentBuilderHost
import com.ashish.movieguide.ui.base.detail.fulldetail.FullDetailContentActivity
import com.ashish.movieguide.ui.common.adapter.OnItemClickListener
import com.ashish.movieguide.ui.common.adapter.RecyclerViewAdapter
import com.ashish.movieguide.ui.common.rating.RatingDialog
import com.ashish.movieguide.ui.season.SeasonDetailActivity
import com.ashish.movieguide.ui.widget.FontTextView
import com.ashish.movieguide.utils.Constants.ADAPTER_TYPE_SEASON
import com.ashish.movieguide.utils.Constants.ADAPTER_TYPE_TV_SHOW
import com.ashish.movieguide.utils.TMDbConstants.TMDB_URL
import com.ashish.movieguide.utils.extensions.changeWatchlistMenuItem
import com.ashish.movieguide.utils.extensions.find
import com.ashish.movieguide.utils.extensions.getBackdropUrl
import com.ashish.movieguide.utils.extensions.getPosterUrl
import com.ashish.movieguide.utils.extensions.isNotNullOrEmpty
import com.ashish.movieguide.utils.extensions.setFavoriteIcon
import com.ashish.movieguide.utils.extensions.setRatingItemTitle
import com.ashish.movieguide.utils.extensions.setTitleAndYear
import com.ashish.movieguide.utils.extensions.show
import com.ashish.movieguide.utils.extensions.startFavoriteAnimation
import dagger.Lazy
import icepick.State
import kotlinx.android.synthetic.main.acivity_detail_tv_show.*
import kotlinx.android.synthetic.main.layout_detail_app_bar.*
import kotlinx.android.synthetic.main.layout_detail_similar_content_stub.*
import javax.inject.Inject

/**
 * Created by Ashish on Jan 03.
 */
class TVShowDetailActivity : FullDetailContentActivity<TVShowDetail, TraktShow,
        TVShowDetailView, TVShowDetailPresenter>(),
        TVShowDetailView, RatingDialog.UpdateRatingListener {

    companion object {
        private const val EXTRA_TV_SHOW = "tv_show"

        @JvmStatic
        fun createIntent(context: Context, tvShow: TVShow?): Intent {
            return Intent(context, TVShowDetailActivity::class.java)
                    .putExtra(EXTRA_TV_SHOW, tvShow)
        }
    }

    @Inject lateinit var ratingDialog: Lazy<RatingDialog>
    @Inject lateinit var preferenceHelper: PreferenceHelper

    @JvmField @State var tvShow: TVShow? = null

    private var seasonsAdapter: RecyclerViewAdapter<Season>? = null
    private var similarTVShowsAdapter: RecyclerViewAdapter<TVShow>? = null

    private val isLoggedIn: Boolean by lazy {
        preferenceHelper.getId() > 0
    }

    private val onSeasonItemClickLitener = object : OnItemClickListener {
        override fun onItemClick(position: Int, view: View) {
            val season = seasonsAdapter?.getItem<Season>(position)
            val intent = SeasonDetailActivity.createIntent(this@TVShowDetailActivity, tvShow?.id, season)
            startNewActivityWithTransition(view, R.string.transition_season_poster, intent)
        }
    }

    private val onSimilarTVShowItemClickLitener = object : OnItemClickListener {
        override fun onItemClick(position: Int, view: View) {
            val tvShow = similarTVShowsAdapter?.getItem<TVShow>(position)
            val intent = TVShowDetailActivity.createIntent(this@TVShowDetailActivity, tvShow)
            startNewActivityWithTransition(view, R.string.transition_tv_poster, intent)
        }
    }

    override fun injectDependencies(builderHost: ActivityComponentBuilderHost) {
        builderHost.getActivityComponentBuilder(TVShowDetailActivity::class.java,
                TVShowDetailComponent.Builder::class.java)
                .withModule(ActivityModule(this))
                .build()
                .inject(this)
    }

    override fun getLayoutId() = R.layout.acivity_detail_tv_show

    override fun getIntentExtras(extras: Bundle?) {
        tvShow = extras?.getParcelable(EXTRA_TV_SHOW)
    }

    override fun getTransitionNameId() = R.string.transition_tv_poster

    override fun loadDetailContent() {
        presenter?.loadDetailContent(tvShow?.id)
    }

    override fun getBackdropPath() = tvShow?.backdropPath.getBackdropUrl()

    override fun getPosterPath() = tvShow?.posterPath.getPosterUrl()

    override fun showDetailContent(detailContent: TVShowDetail) {
        detailContent.run {
            if (getBackdropPath().isNullOrEmpty() && backdropPath.isNotNullOrEmpty()) {
                showBackdropImage(backdropPath.getBackdropUrl())
            }

            imdbId = detailContent.externalIds?.imdbId
            contentTitleText.setTitleAndYear(name, firstAirDate)
        }

        if (isLoggedIn) {
            ratingDialog.get().setRatingListener(this)
            menu?.setRatingItemTitle(R.string.title_rate_tv_show)
        }

        detailTVContainer.show()
        super.showDetailContent(detailContent)
    }

    override fun getDetailContentType() = ADAPTER_TYPE_TV_SHOW

    override fun getItemTitle(): String = tvShow?.name ?: ""

    override fun showSeasonsList(seasonsList: List<Season>) {
        seasonsAdapter = RecyclerViewAdapter(R.layout.list_item_content_alt, ADAPTER_TYPE_SEASON,
                onSeasonItemClickLitener)

        inflateViewStubRecyclerView(seasonsViewStub, R.id.seasonsRecyclerView, seasonsAdapter!!, seasonsList)
    }

    override fun showSimilarTVShowList(similarTVShowList: List<TVShow>) {
        similarTVShowsAdapter = RecyclerViewAdapter(R.layout.list_item_content_alt, ADAPTER_TYPE_TV_SHOW,
                onSimilarTVShowItemClickLitener)

        similarContentViewStub.setOnInflateListener { _, view ->
            val textView = view.find<FontTextView>(R.id.similarContentTitle)
            textView.setText(R.string.similar_tv_shows_title)
        }

        inflateViewStubRecyclerView(similarContentViewStub, R.id.similarContentRecyclerView,
                similarTVShowsAdapter!!, similarTVShowList)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_favorite -> performAction { presenter?.markAsFavorite() }
        R.id.action_watchlist -> performAction { presenter?.addToWatchlist() }

        R.id.action_rating -> performAction {
            if (isLoggedIn) {
                ratingDialog.get().showRatingDialog(myRatingLabel.getRating())
            }
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun getShareText(): CharSequence {
        return tvShow!!.name + "\n\n" + TMDB_URL + "tv/" + tvShow!!.id
    }

    override fun setFavoriteIcon(isFavorite: Boolean) {
        menu?.setFavoriteIcon(isFavorite)
    }

    override fun animateFavoriteIcon(isFavorite: Boolean) {
        val view = toolbar?.findViewById(R.id.action_favorite)
        if (view is ActionMenuItemView) {
            view.startFavoriteAnimation(isFavorite)
        }
    }

    override fun changeWatchlistMenuItem(isInWatchlist: Boolean) {
        menu?.changeWatchlistMenuItem(isInWatchlist)
    }

    override fun showSavedRating(rating: Int?) {
        myRatingLabel.setRating(rating)
    }

    override fun addRating(rating: Int) {
        presenter?.addRating(rating)
    }

    override fun removeRating() {
        presenter?.removeRating()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        if (isLoggedIn) ratingDialog.get().dismissDialog()
        super.onConfigurationChanged(newConfig)
    }

    override fun onStop() {
        if (isLoggedIn) ratingDialog.get().dismissDialog()
        super.onStop()
    }

    override fun performCleanup() {
        super.performCleanup()
        seasonsAdapter?.removeListener()
        similarTVShowsAdapter?.removeListener()
        if (isLoggedIn) ratingDialog.get().setRatingListener(null)
    }
}