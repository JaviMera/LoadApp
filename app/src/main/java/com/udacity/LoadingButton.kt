package com.udacity

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.renderscript.Sampler
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator = ValueAnimator()
    private var downloadText: String
    private var buttonPaint: Paint
    private var progressPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        style = Paint.Style.FILL
    }

    private var textPaint: Paint
    private var progress: Int
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new){
            ButtonState.Clicked ->
                valueAnimator = ValueAnimator.ofInt(0, measuredWidth).apply {
                    addUpdateListener {
                        progress = animatedValue as Int
                        invalidate()
                    }
                    duration = 2000
                    repeatMode = ValueAnimator.RESTART
                    repeatCount = ValueAnimator.INFINITE
                    start()
                }
        }
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,0).apply {
                downloadText = getString(R.styleable.LoadingButton_text)!!
        }

        progress = 0
        buttonPaint = Paint()
        textPaint = Paint()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        buttonPaint.apply {
            color = ContextCompat.getColor(context ,R.color.colorPrimary)
        }

        textPaint.apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = resources.getDimension(R.dimen.loading_button_text_size)
        }

        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = (textHeight / 2) - textPaint.descent()

        val buttonBounds = Rect(0,0, width, height)
        canvas?.drawRect(buttonBounds, buttonPaint)

        if(buttonState == ButtonState.Clicked){
            canvas?.drawRect(0f, 0f, progress.toFloat(), heightSize.toFloat(), progressPaint)
        }
        
        canvas?.drawText(downloadText,
            buttonBounds.centerX() * 1f,
            buttonBounds.centerY() * 1f + (textOffset), textPaint)
    }

    fun upateStatus(state: ButtonState){
        buttonState = state
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

    fun setText(newText: String){

        downloadText = newText
        invalidate()
        requestLayout()
    }
}