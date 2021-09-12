package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private val valueAnimator = ValueAnimator()
    private lateinit var downloadText: String

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,0).apply {

                downloadText = getString(R.styleable.LoadingButton_text)!!
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint().apply {
            color = ContextCompat.getColor(context ,R.color.colorPrimary)
        }

        val textPaint = TextPaint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = resources.getDimension(R.dimen.custom_button_text_size)
        }

        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = (textHeight / 2) - textPaint.descent()
        val bounds = Rect(0,0, width, height)

        canvas?.drawRect(bounds, paint)
        canvas?.drawText(downloadText,
            bounds.centerX() * 1f,
            bounds.centerY() * 1f + (textOffset), textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}