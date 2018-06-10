package com.ashish.movieguide.ui.common.adapter

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import com.ashish.movieguide.ui.base.recyclerview.BaseContentHolder
import com.ashish.movieguide.ui.common.adapter.RecyclerViewItem.Companion.CONTENT_VIEW
import com.ashish.movieguide.ui.common.adapter.RecyclerViewItem.Companion.ERROR_VIEW
import com.ashish.movieguide.ui.common.adapter.RecyclerViewItem.Companion.LOADING_VIEW
import com.ashish.movieguide.utils.glide.GlideApp
import java.util.ArrayList

/**
 * Created by Ashish on Dec 30.
 */
class RecyclerViewAdapter<in I : RecyclerViewItem>(
        layoutId: Int,
        adapterType: Int,
        private var onItemClickListener: OnItemClickListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), RemoveListener {

    private val loadingItem = object : RecyclerViewItem {
        override fun getViewType(): Int = LOADING_VIEW
    }

    private val errorItem = object : RecyclerViewItem {
        override fun getViewType(): Int = ERROR_VIEW
    }

    private var itemList: ArrayList<RecyclerViewItem> = ArrayList()
    private var delegateAdapters = SparseArray<RecyclerViewDelegateAdapter>()
    private val contentAdapter =
            AdapterFactory.getDelegateAdapter(layoutId, adapterType, onItemClickListener)

    init {
        delegateAdapters.put(LOADING_VIEW, LoadingDelegateAdapter())
        delegateAdapters.put(CONTENT_VIEW, contentAdapter)
        delegateAdapters.put(ERROR_VIEW, LoadMoreErrorDelegateAdapter(onItemClickListener))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, itemList[position])
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int): Int = itemList[position].getViewType()

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is BaseContentHolder<*>) {
            GlideApp.with(holder.posterImage.context).clear(holder.posterImage)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <I> getItem(position: Int) = itemList[position] as I

    fun showItemList(newItemList: List<I>?) {
        newItemList?.let {
            val oldPosition = itemCount
            itemList = ArrayList(it)
            notifyItemRangeInserted(oldPosition, it.size)
        }
    }

    fun addLoadingItem() {
        itemList.add(loadingItem)
        notifyItemInserted(itemCount - 1)
    }

    fun addErrorItem() {
        itemList.add(errorItem)
        notifyItemInserted(itemCount - 1)
    }

    fun addNewItemList(newItemList: List<I>?) {
        val loadingItemPosition = removeLoadingItem()
        newItemList?.let {
            itemList.addAll(it)
            notifyItemRangeChanged(loadingItemPosition, itemCount)
        }
    }

    fun removeLoadingItem(): Int {
        val position = itemList.indexOf(loadingItem)
        if (position > -1) removeItem(position)
        return position
    }

    fun removeErrorItem() {
        val position = itemList.indexOf(errorItem)
        if (position > -1) removeItem(position)
    }

    fun replaceItem(position: Int, item: I) {
        itemList[position] = item
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    fun clearAll() {
        val totalItems = itemCount
        if (totalItems > 0) {
            itemList.clear()
            notifyItemRangeRemoved(0, totalItems)
        }
    }

    override fun removeListener() {
        onItemClickListener = null
        (contentAdapter as RemoveListener).removeListener()
    }
}