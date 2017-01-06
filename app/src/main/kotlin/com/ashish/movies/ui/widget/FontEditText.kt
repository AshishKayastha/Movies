package com.ashish.movies.ui.widget

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import com.ashish.movies.utils.FontUtils

/**
 * Created by Ashish on Jan 06.
 */
class FontEditText : AppCompatEditText {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        FontUtils.applyFont(this, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        FontUtils.applyFont(this, attrs)
    }
}