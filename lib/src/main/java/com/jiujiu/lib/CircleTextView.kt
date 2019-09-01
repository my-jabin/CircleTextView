package com.jiujiu.lib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlin.math.max


class CircleTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val FILL = 1
        const val EMPTY = 2
    }

    private var offset: Float = 0f
    private var mBorderColor: Int
    private var mBorderWidth: Int
    private var mCircleColor: Int
    private var mTextSize: Int
    private var mTextColor: Int
    private var mText: CharSequence
    private var mMaxLength: Int
    private var mTextStyle: Int
    private var mShadow: Boolean

    private val mShadowX = 10f
    private val mShadowY = 10f

    private val mCirclePaint = Paint()
    private val mBorderPaint = Paint()
    private val mTextPaint = TextPaint()

    private var mTextWidth: Float = 0f
    private var mTextHeight: Float = 0f

    private var mTextPath = Path()
    private var pathPaint = Paint()

    private var mRadius: Float = 0f
    private var circleX: Float = 0f
    private var circleY: Float = 0f


    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.CircleTextView,
            0, 0
        )

        mBorderColor =
            typedArray.getColor(R.styleable.CircleTextView_borderColor, Color.TRANSPARENT)
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.CircleTextView_borderWidth, 0)

        mCircleColor = typedArray.getColor(
            R.styleable.CircleTextView_circleColor,
            Color.TRANSPARENT
            // todo: what if circle color is transparent?
        )

        mTextSize = typedArray.getDimensionPixelSize(
            R.styleable.CircleTextView_textSize,
            (24 * context.resources.displayMetrics.density).toInt()
        )
        mTextColor = typedArray.getColor(R.styleable.CircleTextView_textColor, Paint().color)

        mTextStyle = typedArray.getInt(R.styleable.CircleTextView_textStyle, FILL)

        mShadow = typedArray.getBoolean(R.styleable.CircleTextView_shadow, false)

        offset = mTextSize / 4f
        mText = typedArray.getText(R.styleable.CircleTextView_text)
        mMaxLength = typedArray.getInt(R.styleable.CircleTextView_maxLength, Int.MAX_VALUE)
        typedArray.recycle()

        mBorderPaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = mBorderColor
        }

        mCirclePaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = mCircleColor
        }

        mTextPaint.apply {
            isAntiAlias = true
            color = mTextColor
        }

        pathPaint.apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = mTextColor
            strokeWidth = 1 * context.resources.displayMetrics.density
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var mWidth = widthSize
        var mHeight = heightSize

        mTextPaint.textSize = mTextSize.toFloat()
        mTextWidth = if (mMaxLength != Int.MAX_VALUE) {
            mTextPaint.measureText(mText.toString().substring(0, mMaxLength))
        } else {
            mTextPaint.measureText(mText.toString())
        }

        val fm = mTextPaint.fontMetrics
        mTextHeight = fm.descent - fm.ascent

        if (widthMode != MeasureSpec.EXACTLY) {
            mWidth =
                (paddingStart + paddingRight + mTextWidth + 2 * mBorderWidth + 2 * offset).toInt() + 1
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            mHeight =
                (paddingTop + paddingBottom + mTextHeight + 2 * mBorderWidth + 2 * offset).toInt() + 1
        }

        if (mShadow) {
            mWidth += mShadowX.toInt()
            mHeight += mShadowY.toInt()
        }

        setMeasuredDimension(max(mWidth, mHeight), max(mWidth, mHeight))

    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.translate(circleX, circleY)

        // todo: shadow??
        if (mShadow) {
            mBorderPaint.setShadowLayer(
                10f,
                mShadowX,
                mShadowY,
                ColorUtils.setAlphaComponent(mBorderColor, 120)
            )
            mCirclePaint.setShadowLayer(
                10f,
                mShadowX,
                mShadowY,
                ColorUtils.setAlphaComponent(mCircleColor, 120)
            )
        }

        canvas?.drawCircle(0f, 0f, mRadius + mBorderWidth, mBorderPaint)
        canvas?.drawCircle(0f, 0f, mRadius, mCirclePaint)


        val length = if (mMaxLength == Int.MAX_VALUE) mText.length else mMaxLength

        if (mTextStyle == FILL) {
            canvas?.drawText(
                mText.toString().substring(0, length),
                -mTextWidth / 2,
                mTextHeight / 2 - mTextPaint.fontMetrics.descent,
                mTextPaint
            )
        } else {
            mTextPaint.getTextPath(
                mText.toString().substring(0, length),
                0,
                length,
                -mTextWidth / 2,
                mTextHeight / 2 - mTextPaint.fontMetrics.descent,
                mTextPath
            )
            canvas?.drawPath(mTextPath, pathPaint)
        }

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circleX = paddingLeft + (w - paddingLeft - paddingRight - mShadowX) / 2f
        circleY = paddingTop + (h - paddingTop - paddingBottom - mShadowY) / 2f
        mRadius =
            if (mTextWidth > mTextHeight - offset) (mTextWidth + 2 * offset) / 2 else mTextHeight / 2 + offset
    }

    fun setText(text: String) {
        mText = text
        invalidate()
    }


}
