package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator = ValueAnimator()
    private var downloadText: String
    private var buttonPaint: Paint
    private var progressPaint: Paint
    private val arcPaint: Paint
    private var textPaint: Paint
    private var progress: Int = 0
    private var buttonColor: Int
    private var progressBarColor: Int
    private var progressArcColor: Int
    private var loadingButtonTextSize: Float

    // I thank paul77uk's github example on how to start implementing the button state logic.
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new){
            ButtonState.Loading ->
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

            ButtonState.Completed -> {
                valueAnimator.cancel()
                progress = 0
                invalidate()
            }

            ButtonState.NotClicked -> {
                progress = 0
                invalidate()
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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
        try {
            buttonColor = typedArray.getColor(R.styleable.LoadingButton_buttonColor, 0)
            progressBarColor = typedArray.getColor(R.styleable.LoadingButton_progressBarColor, 0)
            progressArcColor = typedArray.getColor(R.styleable.LoadingButton_progressArcColor, 0)
            loadingButtonTextSize = typedArray.getDimension(R.styleable.LoadingButton_textSize, 0f)
        }finally {
            typedArray.recycle()
        }

        buttonState = ButtonState.NotClicked

        buttonPaint = Paint().apply {
            style = Paint.Style.FILL
            color = buttonColor
        }

        progressPaint = Paint().apply {
            color = progressBarColor
            style = Paint.Style.FILL
        }

        arcPaint = Paint().apply {
            style = Paint.Style.FILL
            color = progressArcColor
        }

        textPaint = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = loadingButtonTextSize
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = (textHeight / 2) - textPaint.descent()

        val buttonBounds = Rect(0,0, width, height)
        canvas?.drawRect(buttonBounds, buttonPaint)

        // I thank paul77uk's github example on how to start implementing the button state logic.
        when (buttonState) {
            ButtonState.Loading -> {
                canvas?.drawRect(0f, 0f, progress.toFloat(), heightSize.toFloat(), progressPaint)
                canvas?.drawArc(
                    widthSize - 175f,
                    heightSize / 2 - 35f,
                    widthSize - 105f,
                    heightSize / 2 + 35f,
                    270F,
                    progress / 2.5f,
                    true,
                    arcPaint
                )
            }
            ButtonState.NotClicked,
            ButtonState.Completed -> {
                canvas?.drawRect(buttonBounds, buttonPaint)
            }
        }
        
        canvas?.drawText(downloadText,
            buttonBounds.centerX() * 1f,
            buttonBounds.centerY() * 1f + (textOffset), textPaint)
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

    fun upateStatus(state: ButtonState){
        buttonState = state
    }

    fun setText(newText: String){
        downloadText = newText
        invalidate()
    }
}