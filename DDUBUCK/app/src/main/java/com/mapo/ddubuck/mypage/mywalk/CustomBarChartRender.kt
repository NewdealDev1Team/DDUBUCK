package id.co.barchartresearch

import android.graphics.*
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil

class CustomBarChartRender(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : BarChartRenderer(chart, animator, viewPortHandler) {
    //RectF() 직사각형에 대한 부동좌표를 의미
    private val barShadowRectBuffer = RectF()
    //bar반지름
    private var barRadius = 0

    fun setRadius(mRadius: Int) {
        this.barRadius = mRadius
    }

    override fun drawDataSet(canvas: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.apply {
            color = dataSet.barBorderColor
            strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        }
        mShadowPaint.color = dataSet.barShadowColor

        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor
            val barData = mChart.barData
            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / HALF_SIZE
            var x: Float
            var i = 0
            val count =
                ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()).coerceAtMost(dataSet.entryCount.toDouble())

            while (i < count) {
                val barEntry = dataSet.getEntryForIndex(i)
                x = barEntry.x

                barShadowRectBuffer.apply {
                    left = x - barWidthHalf
                    right = x + barWidthHalf
                }
                trans.rectValueToPixel(barShadowRectBuffer)

                if (mViewPortHandler.isInBoundsLeft(barShadowRectBuffer.right).not()) {
                    i++
                    continue
                }

                if (mViewPortHandler.isInBoundsRight(barShadowRectBuffer.left).not()) break
                barShadowRectBuffer.apply {
                    top = mViewPortHandler.contentTop()
                    bottom = mViewPortHandler.contentBottom()
                }

                canvas.drawRoundRect(
                    mBarRect,
                    barRadius.toFloat(),
                    barRadius.toFloat(),
                    mShadowPaint
                )
                i++
            }
        }
        val barBuffer = mBarBuffers[index].apply {
            setPhases(phaseX, phaseY)
            setDataSet(index)
            setInverted(mChart.isInverted(dataSet.axisDependency))
            setBarWidth(mChart.barData.barWidth)
            feed(dataSet)
        }

        trans.pointValuesToPixel(barBuffer.buffer)
        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }
        var j = 0
        while (j < barBuffer.size()) {
            if (mViewPortHandler.isInBoundsLeft(barBuffer.buffer[j + 2]).not()) {
                j += 4
                continue
            }
            if (mViewPortHandler.isInBoundsRight(barBuffer.buffer[j]).not()) break

            if (isSingleColor.not()) { //현재 그려진 값의 색상을 설정함 인덱스 | 범위를 벗어난 경우 색상을 재사용함.
                mRenderPaint.color = dataSet.getColor(j / 4)
            }

            if (dataSet.gradientColor != null) {
                val gradientColor = dataSet.gradientColor
                with(barBuffer) {
                    mRenderPaint.shader = LinearGradient(
                        buffer[j],
                        buffer[j + 3],
                        buffer[j],
                        buffer[j + 1],
                        gradientColor.startColor,
                        gradientColor.endColor,
                        Shader.TileMode.MIRROR
                    )
                }
            }
            if (dataSet.gradientColors != null) {
                with(barBuffer) {
                    mRenderPaint.shader = LinearGradient(
                        buffer[j],
                        buffer[j + 3],
                        buffer[j],
                        buffer[j + 1],
                        dataSet.getGradientColor(j / 4).startColor,
                        dataSet.getGradientColor(j / 4).endColor,
                        Shader.TileMode.MIRROR
                    )
                }
            }

            with(barBuffer) {
                val path2: Path = roundRect(
                    RectF(buffer[j], buffer[j + 1], buffer[j + 2], buffer[j + 3]),
                    isRoundedTopLeft = true,
                    isRoundedTopRight = true,
                    isRoundedBottomRight = false,
                    isRoundedBottomLeft = false
                )
                canvas.drawPath(path2, mRenderPaint)
            }

            if (drawBorder) {
                with(barBuffer) {
                    val path: Path = roundRect(
                        RectF(buffer[j], buffer[j + 1], buffer[j + 2], buffer[j + 3]),
                        isRoundedTopLeft = true,
                        isRoundedTopRight = true,
                        isRoundedBottomRight = false,
                        isRoundedBottomLeft = false
                    )
                    canvas.drawPath(path, mBarBorderPaint)
                }
            }
            j += 4
        }
    }

    //막대 그래프 라운드
    private fun roundRect(
        rect: RectF,
        isRoundedTopLeft: Boolean,
        isRoundedTopRight: Boolean,
        isRoundedBottomRight: Boolean,
        isRoundedBottomLeft: Boolean
    ): Path {
        var rx = barRadius.toFloat()
        var ry = barRadius.toFloat()
        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom

        if (rx < 0) rx = DEFAULT_VALUE
        if (ry < 0) ry = DEFAULT_VALUE

        val width = right - left
        val height = bottom - top

        if (rx > width / 2) rx = width / 2
        if (ry > height / 2) ry = height / 2

        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry

        return Path().apply {
            moveTo(right, top + ry)

            if (isRoundedTopRight) rQuadTo(DEFAULT_VALUE, -ry, -rx, -ry) //top-right corner
            else {
                rLineTo(DEFAULT_VALUE, -ry)
                rLineTo(-rx, DEFAULT_VALUE)
            }
            rLineTo(-widthMinusCorners, DEFAULT_VALUE)

            if (isRoundedTopLeft) rQuadTo(-rx, DEFAULT_VALUE, -rx, ry) //top-left corner
            else {
                rLineTo(-rx, DEFAULT_VALUE)
                rLineTo(DEFAULT_VALUE, ry)
            }
            rLineTo(DEFAULT_VALUE, heightMinusCorners)

            if (isRoundedBottomLeft) rQuadTo(DEFAULT_VALUE, ry, rx, ry) //bottom-left corner
            else {
                rLineTo(DEFAULT_VALUE, ry)
                rLineTo(rx, DEFAULT_VALUE)
            }
            rLineTo(widthMinusCorners, DEFAULT_VALUE)

            if (isRoundedBottomRight) rQuadTo(rx, DEFAULT_VALUE, rx, -ry) //bottom-right corner
            else {
                rLineTo(rx, DEFAULT_VALUE)
                rLineTo(0F, -ry)
            }
            rLineTo(DEFAULT_VALUE, -heightMinusCorners)

            close() //가까워지면 마지막 lineto(차트 위)를 제거 할 수 있다.
        }
    }

    companion object {
        private const val DEFAULT_VALUE = 0F
        private const val HALF_SIZE = 0F
    }
}