package com.example.banner2

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.banner2.widget.IndicatorView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val pagerAdapter = BannerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pagerAdapter.setData(
            arrayListOf("A", "B", "C", "D", "E", "F")
        )
        banner.setIndicator(
            findViewById<IndicatorView>(R.id.indicator_view)
                .setIndicatorColor(Color.GRAY)
                .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_BEZIER)
                .setIndicatorSelectorColor(Color.RED), false
        )
            .setAdapter(pagerAdapter)

    }
}
