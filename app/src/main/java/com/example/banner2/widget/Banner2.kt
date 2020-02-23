package com.example.banner2.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.banner2.Iindicator

class Banner2 @JvmOverloads constructor(
    val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(mContext, attrs, defStyleAttr) {
    val viewPager2 = ViewPager2(mContext)
    val bannerAdapter = BannerAdapter()
    var canLoop: Boolean = true
    var autoLoop: Boolean = true
    var totalCounts = 0
    var realTotalItem = 0
    var currentPosition = 0
    private val autoTurningTime: Long = BannerConfig.DELAY_TIME
    private var indicator: Iindicator? = null
    private var changeCallback: OnPageChangeCallback? = null
    fun setOuterPageChangeListener(listener: OnPageChangeCallback?): Banner2? {
        changeCallback = listener
        return this
    }

    /**
     * 设置indicator，支持在xml中创建
     *
     * @param attachToRoot true 添加到banner布局中
     */
    fun setIndicator(
        indicator: Iindicator?,
        attachToRoot: Boolean
    ): Banner2 {
        if (this.indicator != null) {
            removeView(this.indicator!!.getView())
        } else {
            this.indicator = indicator
        }
        if (indicator != null) {
            if (attachToRoot) {
                addView(this.indicator!!.getView(), this.indicator?.getParams())
            }
        }
        return this
    }

    fun setAutoLoop(param: Boolean): Banner2 {
        this.autoLoop = param
        this.canLoop = param
        return this
    }

    init {
        addView(viewPager2)
        initView()
    }

    private fun initView() {
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                changeCallback?.onPageSelected(getRealPosition(position))
                indicator?.onPageSelected(getRealPosition(position))
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val realPosition: Int = getRealPosition(position)
                changeCallback?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels
                )
                indicator?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    Log.e("tag", "current=$currentPosition")
                    if (currentPosition == totalCounts - 1) {
                        setCurrentItem(1)
                    } else if (currentPosition == 0) {
                        setCurrentItem(realTotalItem + currentPosition)
                    }
                }
                changeCallback?.onPageScrollStateChanged(state)
                indicator?.onPageScrollStateChanged(state)

            }
        })
    }

    private fun setCurrentItem(realPosition: Int) {
        Log.e("tag", "real=$currentPosition")
        viewPager2.setCurrentItem(realPosition, false)
    }

    fun setAdapter(paramAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) {
        bannerAdapter.registerAdapter(paramAdapter)
        initPageCount()
        initBanner()
        initScrollVelocity()
    }

    private fun initScrollVelocity() {
        try {
            val clazz = Class.forName("androidx.viewpager.widget.ViewPager")
            val field = clazz.getDeclaredField("mScroller")
            val fixedSpeedScroller = FixedSpeedScroller(mContext, LinearOutSlowInInterpolator())
            fixedSpeedScroller.setmDuration(BannerConfig.PAGER_SCROLL_VELOCITY)
            field.isAccessible = true
            field.set(viewPager2, fixedSpeedScroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initBanner() {
        viewPager2.adapter = bannerAdapter
        val adapter = viewPager2.adapter
        if (adapter == null) {
            viewPager2.adapter = bannerAdapter
        } else {
            if (canLoop && totalCounts > 1) {
                setCurrentItem(1)
            }
            adapter.notifyDataSetChanged()
        }
        indicator?.initIndicatorCount(realTotalItem)
    }

    private fun initPageCount() {
        val wrapperAdapter = bannerAdapter.adapter ?: return
        realTotalItem = wrapperAdapter.itemCount
        when {
            realTotalItem == 0 -> {
                this.totalCounts = 0
            }
            realTotalItem == 1 -> {
                this.totalCounts = 1
                canLoop = false
            }
            canLoop -> {
                totalCounts = wrapperAdapter.itemCount + 2
            }
            else -> {
                totalCounts = wrapperAdapter.itemCount
            }
        }
    }

    var itemDataSetChangeObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(
            positionStart: Int,
            itemCount: Int,
            payload: Any?
        ) {
            onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged()
        }

        override fun onChanged() {
            if (viewPager2 != null && bannerAdapter != null) {
                initPageCount()
            }
        }
    }

    /****
     *通过当前位置获取以前真实位置
     */
    fun getRealPosition(position: Int): Int {
        return (position - 1 + realTotalItem) % realTotalItem
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (autoLoop) {
            startLoop()
        }
    }

    private fun startLoop() {
        stopLoop()
        postDelayed(task, autoTurningTime)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (autoLoop) {
            stopLoop()
        }
    }

    private fun stopLoop() {
        removeCallbacks(task)
    }

    private val task: Runnable = object : Runnable {
        override fun run() {
            if (autoLoop && totalCounts > 1) {
                currentPosition++
                if (currentPosition == realTotalItem + 1 + 1) {
                    currentPosition = 1
                    viewPager2.setCurrentItem(currentPosition, false)
                    post(this)
                } else {
                    Log.e("auto", "next  +++++++$currentPosition")
                    viewPager2.currentItem = currentPosition
                    postDelayed(this, autoTurningTime)
                }

            }
        }
    }

    inner class BannerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (adapter == null) {
                throw  (Exception("you need register a adapter"))
            }
            return adapter!!.onCreateViewHolder(parent, viewType)
        }

        override fun getItemCount(): Int {
            if (adapter == null) {
                throw  (Exception("you need register a adapter"))
            }
            return totalCounts
        }

        override fun getItemViewType(position: Int): Int {
            return super.getItemViewType(getRealPosition(position))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            adapter?.onBindViewHolder(holder, getRealPosition(position))
        }

        fun registerAdapter(paramAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) {
            if (paramAdapter == null) {
                throw (Exception("you need register a adapter"))
            }
            if (adapter != null) {
                adapter?.unregisterAdapterDataObserver(itemDataSetChangeObserver)
            }
            this.adapter = paramAdapter
            this.adapter?.registerAdapterDataObserver(itemDataSetChangeObserver)
        }
    }


}