package id.co.barchartresearch

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.item_marker_view.view.*

/**
 * Created by pertadima on 03,December,2019
 */

@SuppressLint("ViewConstructor")
class CustomMarketView(context: Context, layoutResources: Int) :
    MarkerView(context, layoutResources) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tv_chart_marker.text = e?.y.toString()
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF? {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}
