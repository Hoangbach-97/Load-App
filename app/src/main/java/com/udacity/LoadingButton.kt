package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var btnText: String = ""
    private var textDpl: String = ""
    private var btnDownloadText: String = ""

    private var widthSize = 0
    private var heightSize = 0
    private var progress: Int = 0
    private var circleColor: Int = 0
    private var valueAnimator = ValueAnimator()
    private var progressArc = RectF()

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            circleColor = ContextCompat.getColor(context, R.color.colorAccent)
            btnText = getString(R.styleable.LoadingButton_btnTextAttr) ?: ""
            btnDownloadText = getString(R.styleable.LoadingButton_btnDownloadTextAttr) ?: ""
            textDpl = btnText
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50.0f
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rectArea = Rect()
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = ContextCompat.getColor(context, R.color.white)
        paint.getTextBounds(textDpl, 0, textDpl.length, rectArea)
        canvas.drawText(
            textDpl,
            widthSize / 2f,
            (measuredHeight.toFloat() / 2 - rectArea.centerY()),
            paint
        )

        if (buttonState == ButtonState.Loading) {
            paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            canvas.drawRect(0f, 0f, (progress / 1000f * widthSize), heightSize.toFloat(), paint)
            paint.color = circleColor
            canvas.drawArc(progressArc, 0f, (progress / 1000f * 360f), true, paint)
        }

    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSizeAndState(
            paddingLeft + paddingRight + suggestedMinimumWidth,
            widthMeasureSpec, 1
        )
        val height = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
        progressArc = RectF(
            widthSize - 200f,
            heightSize / 2 - 40f,
            widthSize.toFloat() - 150f,
            heightSize / 2 + 40f
        )

    }

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        if (new == ButtonState.Clicked) {
            textDpl = btnDownloadText
            setState(ButtonState.Loading)
            isEnabled = false
        }
        if (new == ButtonState.Loading) {
            valueAnimator = ValueAnimator.ofInt(0, 1000).apply {
                addUpdateListener {
                    progress = animatedValue as Int
                    invalidate()
                }
                duration = 20000
                doOnStart {
                    isEnabled = false
                    textDpl = btnDownloadText
                }

                doOnEnd {
                    textDpl = btnText
                    isEnabled = true
                    progress = 0
                }
                start()
            }
        }
        if (new == ButtonState.Completed) {
            isEnabled = true
            textDpl = btnText
        }
        invalidate()
    }

    fun downloadCompleted() {
        valueAnimator.setCurrentFraction(valueAnimator.animatedFraction + 0.1f)
        valueAnimator.duration = 1000
        valueAnimator.start()
    }

    fun animationCompleted() {
        valueAnimator.cancel()
        isEnabled = true
    }

    fun setState(_state: ButtonState) {
        this.buttonState = _state
    }
}
