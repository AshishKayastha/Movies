package com.ashish.movieguide.ui.common.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ashish.movieguide.R
import com.ashish.movieguide.utils.Constants.THUMBNAIL_SIZE
import com.ashish.movieguide.utils.extensions.bindView
import com.ashish.movieguide.utils.extensions.inflate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * Created by Ashish on Jan 14.
 */
class ImageAdapter(
        val imageUrlList: ArrayList<String>,
        private var onItemClickListener: OnItemClickListener?
) : RecyclerView.Adapter<ImageAdapter.ImageHolder>(), RemoveListener {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageHolder {
        return ImageHolder(parent?.inflate(R.layout.list_item_detail_image)!!)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bindData(imageUrlList[position], position)
    }

    override fun getItemCount() = imageUrlList.size

    override fun onViewRecycled(holder: ImageHolder) {
        super.onViewRecycled(holder)
        Glide.clear(holder.imageView)
    }

    override fun removeListener() {
        onItemClickListener = null
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView by bindView(R.id.detail_content_image)

        init {
            itemView.setOnClickListener { view ->
                onItemClickListener?.onItemClick(adapterPosition, view)
            }
        }

        fun bindData(imageUrl: String, position: Int) {
            Glide.with(imageView.context)
                    .load(imageUrl)
                    .asBitmap()
                    .animate(R.anim.fade_in)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .override(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                    .into(imageView)

            imageView.transitionName = "image_$position"
        }
    }
}