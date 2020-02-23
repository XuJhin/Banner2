package com.example.banner2.widget

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller


class FixedSpeedScroller(context: Context?, interpolator: Interpolator?) :
    Scroller(context, interpolator) {
    var mDuration = 1500

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        startScroll(startX, startY, dx, dy, mDuration)
    }

    override fun startScroll(
        startX: Int,
        startY: Int,
        dx: Int,
        dy: Int,
        duration: Int
    ) { //管你 ViewPager 传来什么时间，我完全不鸟你
        super.startScroll(startX, startY, dx, dy, mDuration)
    }

    fun getmDuration(): Int {
        return mDuration
    }

    fun setmDuration(duration: Int) {
        mDuration = duration
    }
}

