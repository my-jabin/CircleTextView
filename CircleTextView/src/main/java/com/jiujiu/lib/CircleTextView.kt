package com.jiujiu.lib

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlin.math.max
import kotlin.math.min


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
    private var mBorderWidth: Float
    private var mCircleColor: Int
    private var mTextSize: Int
    private var mTextColor: Int
    private var mText: CharSequence
    private var mMaxLength: Int
    private var mTextStyle: Int
    private var mShadow: Boolean
    private var mShadowColor = Color.TRANSPARENT

    private val mShadowX = 0.5f * resources.displayMetrics.density
    private val mShadowY = 0.8f * resources.displayMetrics.density

    private val mRect = Rect()
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
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.CircleTextView,
            0, 0
        )

        mBorderColor =
            typedArray.getColor(R.styleable.CircleTextView_borderColor, Color.TRANSPARENT)
        mBorderWidth =
            typedArray.getDimensionPixelSize(R.styleable.CircleTextView_borderWidth, 0).toFloat()

        mCircleColor = typedArray.getColor(
            R.styleable.CircleTextView_circleColor,
            Color.TRANSPARENT
        )

        mTextSize = typedArray.getDimensionPixelSize(
            R.styleable.CircleTextView_textSize,
            (24 * context.resources.displayMetrics.density).toInt()
        )
        mTextColor = typedArray.getColor(R.styleable.CircleTextView_textColor, Paint().color)

        mTextStyle = typedArray.getInt(R.styleable.CircleTextView_textStyle, FILL)

        mShadow = typedArray.getBoolean(R.styleable.CircleTextView_shadow, false)

        offset = mTextSize / 3f
        mText = typedArray.getText(R.styleable.CircleTextView_text)
        mMaxLength = typedArray.getInt(R.styleable.CircleTextView_maxLength, Int.MAX_VALUE)
        typedArray.recycle()

        setUpPaint()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var mWidth = widthSize
        var mHeight = heightSize

        mTextPaint.textSize = mTextSize.toFloat()

        mTextWidth = if (mMaxLength != Int.MAX_VALUE && mMaxLength < mText.length) {
            mTextPaint.getTextBounds(
                mText.toString().substring(0, mMaxLength),
                0,
                mMaxLength,
                mRect
            )
            mTextPaint.measureText(mText.toString().substring(0, mMaxLength))
        } else {
            mTextPaint.getTextBounds(mText.toString(), 0, mText.length, mRect)
            mTextPaint.measureText(mText.toString())
        }

        val fm = mTextPaint.fontMetrics
        mTextHeight = min(fm.descent - fm.ascent, mRect.height().toFloat())


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

        if (mShadow && mCircleColor != Color.TRANSPARENT) {
            mShadowColor = ColorUtils.setAlphaComponent(
                if (mBorderWidth > 0) mBorderColor else mCircleColor,
                125
            )
            mCirclePaint.setShadowLayer(
                2 * resources.displayMetrics.density,
                mBorderWidth + mShadowX,
                mBorderWidth + mShadowY,
                mShadowColor
            )
        }

        canvas?.drawCircle(0f, 0f, mRadius, mCirclePaint)
        canvas?.drawCircle(0f, 0f, mRadius, mBorderPaint)

        val length = if (mText.length < mMaxLength) mText.length else mMaxLength

        if (mTextStyle == FILL) {
            canvas?.drawText(
                mText.toString().substring(0, length),
                -mTextWidth / 2,
//                mTextHeight / 2 - mTextPaint.fontMetrics.descent,
                mTextHeight / 2,
                mTextPaint
            )
        } else {
            mTextPaint.getTextPath(
                mText.toString().substring(0, length),
                0,
                length,
                -mTextWidth / 2,
//                mTextHeight / 2 - mTextPaint.fontMetrics.descent,
                mTextHeight / 2,
                mTextPath
            )
            canvas?.drawPath(mTextPath, pathPaint)
        }

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circleX = if (mShadow) {
            paddingLeft + (w - paddingLeft - paddingRight - mShadowX) / 2f
        } else {
            paddingLeft + (w - paddingLeft - paddingRight) / 2f
        }
        circleY = if (mShadow) {
            paddingTop + (h - paddingTop - paddingBottom - mShadowY) / 2f
        } else {
            paddingTop + (h - paddingTop - paddingBottom) / 2f
        }
        mRadius =
            if (mTextWidth > mTextHeight - offset) (mTextWidth + 2 * offset) / 2 else mTextHeight / 2 + offset
    }

    private fun setUpPaint() {

        mBorderPaint.reset()
        mBorderPaint.apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            color = mBorderColor
            strokeWidth = mBorderWidth
        }

        mCirclePaint.reset()
        mCirclePaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = mCircleColor
        }

        mTextPaint.reset()
        mTextPaint.apply {
            isAntiAlias = true
            color = mTextColor
        }

        pathPaint.reset()
        pathPaint.apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = mTextColor
            strokeWidth = 1 * context.resources.displayMetrics.density
        }
    }

    fun setText(text: String) {
        mText = text
        requestLayout()
        invalidate()
    }

    // unit: sp
    fun setTestSize(size: Int) {
        this.mTextSize = (size * context.resources.displayMetrics.scaledDensity).toInt()
        offset = mTextSize / 3f
        requestLayout()
        invalidate()
    }

    fun setTextColor(color: Int) {
        mTextColor = color
        setUpPaint()
        requestLayout()
        invalidate()
    }

    fun setCircleColor(color: Int) {
        mCircleColor = color
        setUpPaint()
        requestLayout()
        invalidate()
    }

    fun setBorderColor(color: Int) {
        mBorderColor = color
        setUpPaint()
        requestLayout()
        invalidate()
    }

    // unit：dp
    fun setBorderWidth(width: Int) {
        mBorderWidth = width * resources.displayMetrics.density
        setUpPaint()
        requestLayout()
        invalidate()
    }

    fun setTextStyle(style: Int) {
        when (style) {
            FILL -> mTextStyle = FILL
            EMPTY -> mTextStyle = EMPTY
            else -> {
            }
        }
        setUpPaint()
        requestLayout()
        invalidate()
    }

    fun getTextStyle() = mTextStyle

    fun setShadow(has: Boolean) {
        mShadow = has
        setUpPaint()
        requestLayout()
        invalidate()
    }

    fun hasShadow() = mShadow

    fun getMaxLength() = mMaxLength

    fun setMaxLength(length: Int) {
        if (length > 0) {
            mMaxLength = length
            setUpPaint()
            requestLayout()
            invalidate()
        }
    }

    fun getText() = mText

}