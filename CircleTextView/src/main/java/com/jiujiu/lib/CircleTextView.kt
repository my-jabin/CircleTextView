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

    private var mOffset: Float = 0f
    private var mBorderColor: Int
    private var mBorderWidth: Float
    private var mCircleColor: Int
    private var mTextSize: Int
    private var mTextColor: Int
    private var mLetterSpace: Float
    private var hasText: Boolean
    private var mText: CharSequence
    private var mMaxLength: Int
    private var mTextStyle: Int
    private var mShadow: Boolean
    private var mShadowColor = Color.parseColor("#c5c5c5")

    private val mShadowX = 2.0f * resources.displayMetrics.density
    private val mShadowY = 1.5f * resources.displayMetrics.density

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
    private var mCircleSize: Int

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
        mLetterSpace = typedArray.getFloat(R.styleable.CircleTextView_letterSpace, 0f)
        if (mLetterSpace < 0 || mLetterSpace > 1) mLetterSpace = 0f
        mShadow = typedArray.getBoolean(R.styleable.CircleTextView_shadow, false)
        mOffset = typedArray.getDimensionPixelSize(R.styleable.CircleTextView_offset, mTextSize / 3)
            .toFloat()
        hasText = typedArray.hasValueOrEmpty(R.styleable.CircleTextView_text)
        mText = if (hasText) typedArray.getText(R.styleable.CircleTextView_text) else ""
        mMaxLength = typedArray.getInt(R.styleable.CircleTextView_maxLength, Int.MAX_VALUE)
        mCircleSize =
            typedArray.getInt(R.styleable.CircleTextView_circle_size, CircleSize.WRAP_TEXT.ordinal)
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

        if (hasText) {
            measureText()
            if (widthMode != MeasureSpec.EXACTLY) {
                mWidth =
                    (paddingStart + paddingRight + mTextWidth + 2 * mBorderWidth + 2 * mOffset).toInt() + 1
            }

            if (heightMode != MeasureSpec.EXACTLY) {
                mHeight =
                    (paddingTop + paddingBottom + mTextHeight + 2 * mBorderWidth + 2 * mOffset).toInt() + 1
            }

            if (mShadow) {
                mWidth += mShadowX.toInt()
                mHeight += mShadowY.toInt()
            }
        } else {
            mWidth = 0
            mHeight = 0
        }
        setMeasuredDimension(max(mWidth, mHeight), max(mWidth, mHeight))
        setUpCircle(max(mWidth, mHeight), max(mWidth, mHeight))
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.translate(circleX, circleY)

        if (mShadow && mCircleColor != Color.TRANSPARENT) {
            mShadowColor = ColorUtils.setAlphaComponent(
                if (mBorderWidth > 0) mBorderColor else mShadowColor,
                125
            )
            mCirclePaint.setShadowLayer(
                2 * resources.displayMetrics.density,
                if (mBorderWidth > 0) mBorderWidth * 1.2f else mShadowX,
                if (mBorderWidth > 0) mBorderWidth * 0.8f else mShadowY,
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
                mTextHeight / 2,
                mTextPaint
            )
        } else {
            mTextPaint.getTextPath(
                mText.toString().substring(0, length),
                0,
                length,
                -mTextWidth / 2,
                mTextHeight / 2,
                mTextPath
            )
            canvas?.drawPath(mTextPath, pathPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setUpCircle(w, h)
    }

    private fun measureText() {
        mTextPaint.textSize = mTextSize.toFloat()
        mTextPaint.letterSpacing = mLetterSpace
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
    }

    private fun setUpCircle(w: Int, h: Int) {
        circleX = if (mShadow) {
            paddingLeft + (w - paddingLeft - paddingRight - mShadowX) / 2f + mShadowX / 2
        } else {
            paddingLeft + (w - paddingLeft - paddingRight) / 2f
        }
        circleY = if (mShadow) {
            paddingTop + (h - paddingTop - paddingBottom - mShadowY) / 2f + mShadowY / 2
        } else {
            paddingTop + (h - paddingTop - paddingBottom) / 2f + 1
        }

        if (mCircleSize == CircleSize.WRAP_TEXT.ordinal) {
            mRadius =
                if (mTextWidth > mTextHeight - mOffset) (mTextWidth + 2 * mOffset) / 2 else mTextHeight / 2 + mOffset
        } else if (mCircleSize == CircleSize.MATCH_PARENT.ordinal) {
            mRadius = max(
                (w - paddingStart - paddingEnd - 2 * mBorderWidth - mShadowX) / 2f,
                (h - paddingTop - paddingBottom - 2 * mBorderWidth - mShadowY) / 2f
            )
        }
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
            letterSpacing = mLetterSpace
            textScaleX = 1.2f
        }

        pathPaint.reset()
        pathPaint.apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = mTextColor
            letterSpacing = mLetterSpace
            textScaleX = 1.2f
            strokeWidth = 1 * context.resources.displayMetrics.density
        }
    }

    private fun setUp() {
        setUpPaint()
        requestLayout()
        invalidate()
    }

    fun setText(text: String?) {
        mText = text ?: ""
        hasText = !mText.isBlank()
        setUp()
    }

    // unit: sp
    fun setTestSize(size: Int) {
        this.mTextSize = (size * context.resources.displayMetrics.scaledDensity).toInt()
        mOffset = mTextSize / 3f
        setUp()
    }

    fun setTextColor(color: Int) {
        mTextColor = color
        setUp()
    }

    fun setCircleColor(color: Int) {
        mCircleColor = color
        setUp()
    }

    fun setBorderColor(color: Int) {
        mBorderColor = color
        setUp()
    }

    // unit：dp
    fun setBorderWidth(width: Int) {
        mBorderWidth = width * resources.displayMetrics.density
        setUp()
    }

    fun setTextStyle(style: Int) {
        when (style) {
            FILL -> mTextStyle = FILL
            EMPTY -> mTextStyle = EMPTY
            else -> {
            }
        }
        setUp()
    }

    fun getTextStyle() = mTextStyle

    fun setShadow(has: Boolean) {
        mShadow = has
        setUp()
    }

    fun hasShadow() = mShadow

    fun getMaxLength() = mMaxLength

    fun setMaxLength(length: Int) {
        if (length > 0) {
            mMaxLength = length
            setUp()
        }
    }

    fun getText(): String = mText.toString()

    // Unit in dp
    fun setOffset(offset: Int) {
        mOffset = offset.toFloat()
        setUp()
    }

    fun setLetterSpace(space: Float) {
        if (space >= 0) {
            mLetterSpace = space
            setUp()
        }
    }

    fun setCircleSize(type: CircleSize) {
        mCircleSize = type.ordinal
        setUp()
    }
}


enum class CircleSize(type: Int) { MATCH_PARENT(0), WRAP_TEXT(1) }
