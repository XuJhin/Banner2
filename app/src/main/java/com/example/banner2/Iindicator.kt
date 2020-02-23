package com.example.banner2

import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.Px
import androidx.viewpager2.widget.ViewPager2.ScrollState

interface Iindicator {

    /**
     * Called when data initialization is complete
     *
     * @param pagerCount page num
     */
    fun initIndicatorCount(pagerCount: Int)

    /**
     * return View，and add banner
     */
    fun getView(): View?

    fun onPageScrolled(
        position: Int, positionOffset: Float,
        @Px positionOffsetPixels: Int
    )

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    fun onPageSelected(position: Int)

    /**
     * Called when the scroll state changes. Useful for discovering when the user begins
     * dragging, when a fake drag is started, when the pager is automatically settling to the
     * current page, or when it is fully stopped/idle. `state` can be one of [ ][.SCROLL_STATE_IDLE], [.SCROLL_STATE_DRAGGING] or [.SCROLL_STATE_SETTLING].
     */
    fun onPageScrollStateChanged(@ScrollState state: Int)

    fun getParams():RelativeLayout.LayoutParams
}