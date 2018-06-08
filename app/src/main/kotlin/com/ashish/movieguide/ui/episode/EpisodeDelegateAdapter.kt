package com.ashish.movieguide.ui.episode

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.ashish.movieguide.R
import com.ashish.movieguide.data.network.entities.tmdb.Episode
import com.ashish.movieguide.ui.base.recyclerview.BaseContentHolder
import com.ashish.movieguide.ui.base.recyclerview.ContentDelegateAdapter
import com.ashish.movieguide.ui.common.adapter.OnItemClickListener
import com.ashish.movieguide.ui.common.adapter.ViewType
import com.ashish.movieguide.utils.extensions.applyText
import com.ashish.movieguide.utils.extensions.getStillImageUrl

/**
 * Created by Ashish on Jan 08.
 */
class EpisodeDelegateAdapter(
        layoutId: Int,
        onItemClickListener: OnItemClickListener?
) : ContentDelegateAdapter(layoutId, onItemClickListener) {

    override fun getHolder(parent: ViewGroup, layoutId: Int) = EpisodeHolder(parent, layoutId)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        (holder as EpisodeHolder).bindData(item as Episode)
    }

    class EpisodeHolder(parent: ViewGroup, layoutId: Int) : BaseContentHolder<Episode>(parent, layoutId) {

        override fun bindData(item: Episode) {
            with(item) {
                super.bindData(item)
                val context = itemView.context
                contentTitle.applyText(name)
                ratingLabel?.setRating(rating)
                contentSubtitle.applyText(String.format(context.getString(R.string.episode_number_format),
                        episodeNumber))
            }
        }

        override fun getImageUrl(item: Episode): String? = item.stillPath.getStillImageUrl()
    }
}