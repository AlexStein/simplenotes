package ru.softmine.simplenotes.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.annotation.Dimension.PX
import androidx.core.content.ContextCompat
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.common.dip

@Dimension(unit = DP)
private const val defRadiusDp = 6

@Dimension(unit = DP)
private const val defBoxSizeDp = 32

@Dimension(unit = DP)
private const val defStrokeWidthDp = 1

class ColorBoxView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private var colorBox: RectF = RectF()
    private var innerBox: RectF = RectF()

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    @ColorRes
    var fillColorRes: Int = R.color.color_white
        set(value) {
            field = value
            fillPaint.color = ContextCompat.getColor(context, value)
        }

    @ColorRes
    var strokeColorRes: Int = R.color.black
        set(value) {
            field = value
            strokePaint.color = ContextCompat.getColor(context, value)
        }

    @Dimension(unit = PX)
    var cornerRadius: Float = context.dip(defRadiusDp).toFloat()

    @Dimension(unit = PX)
    var boxSize: Float = context.dip(defBoxSizeDp).toFloat()

    @Dimension(unit = PX)
    var strokeWidth: Float = context.dip(defStrokeWidthDp).toFloat()
        set(value) {
            field = value
            strokePaint.strokeWidth = value
        }

    init {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.ColorBoxView)

        fillColorRes = a.getResourceId(R.styleable.ColorBoxView_fillColor, R.color.color_white)
        strokeColorRes = a.getResourceId(R.styleable.ColorBoxView_strokeColor, R.color.black)

        val defRadiusPx = context.dip(defRadiusDp).toFloat()
        cornerRadius = a.getDimension(R.styleable.ColorBoxView_cornerRadius, defRadiusPx)

        val strokeWidthPx = context.dip(defStrokeWidthDp).toFloat()
        strokeWidth = a.getDimension(R.styleable.ColorBoxView_strokeWidth, strokeWidthPx)

        val boxSizePx = context.dip(defBoxSizeDp).toFloat()
        boxSize = a.getDimension(R.styleable.ColorBoxView_boxSize, boxSizePx)

        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (boxSize + paddingLeft + paddingRight).toInt()
        val height = (boxSize + paddingLeft + paddingBottom).toInt()

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val left = paddingStart.toFloat()
        val top = paddingTop.toFloat()
        val right = (w - paddingEnd).toFloat()
        val bottom = (h - paddingBottom).toFloat()

        colorBox.set(left, top, right, bottom)
        innerBox.set(
            left + strokeWidth,
            top + strokeWidth,
            right - strokeWidth,
            bottom - strokeWidth
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRoundRect(colorBox, cornerRadius, cornerRadius, fillPaint);
        canvas?.drawRoundRect(innerBox, cornerRadius, cornerRadius, fillPaint);
    }
}