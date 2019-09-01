package com.jiujiu.lib

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import kotlin.math.max


class CircleTextViewOld @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {
    private val TAG: String = "CircleTextView"

    private var offset: Float = 0f
    private var mBorderColor: Int
    private var mBorderWidth: Int
    private var mCircleColor: Int
//    private var mSingleLetter: Boolean

    private var mRect = Rect()
    private val mCirclePaint = Paint()
    private val mBorderPaint = Paint()
    private val mTextPaint = TextPaint()

    private var mTextWidth: Float = 0f
    private var mTextHeight: Float = 0f

    //    private var mTextPath = Path()
//    private var pathPaint = Paint()
//
//    private var mWidth = 120
//    private var mHeight = 120
    private var mRadius: Float = 0f
    private var circleX: Float = 0f
    private var circleY: Float = 0f

    private var mBitmap: Bitmap? = null


    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.CircleTextView,
            0, 0
        )

        mBorderColor =
            typedArray.getColor(R.styleable.CircleTextView_borderColor, Color.TRANSPARENT)
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.CircleTextView_borderWidth, 0)
        mCircleColor = typedArray.getColor(
            R.styleable.CircleTextView_circleColor,
            ColorGenerator.randomColor()
        )
//        mSingleLetter = typedArray.getBoolean(R.styleable.CircleTextView_showFirst, false)
        typedArray.recycle()

//        if (mSingleLetter && text.isNotEmpty()) text = text.first().toUpperCase().toString()

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
//
        mTextPaint.apply {
            isAntiAlias = true
            // todo: support shadow?
//            setShadowLayer(10f, 0f, 0f, Color.RED)
        }
//
//        pathPaint.apply {
//            isAntiAlias = true
//            style = Paint.Style.STROKE
//        }

        Log.d(TAG, "init before setup")
        setup()
        Log.d(TAG, "init after setup")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val availableLength = max(
            measuredWidth - paddingStart - paddingEnd,
            measuredHeight - paddingTop - paddingBottom
        )

        val width = when (widthMode) {
            MeasureSpec.AT_MOST -> (measuredWidth + 2 * mBorderWidth + 2 * offset).toInt() + 1
            MeasureSpec.UNSPECIFIED -> (measuredWidth + 2 * mBorderWidth + 2 * offset).toInt() + 1
            else -> measuredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.AT_MOST -> availableLength + paddingTop + paddingBottom + 2 * mBorderWidth
            MeasureSpec.UNSPECIFIED -> availableLength + paddingTop + paddingBottom + 2 * mBorderWidth
            else -> measuredHeight
        }

        setMeasuredDimension(max(width, height), max(width, height))

        gravity = Gravity.CENTER

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circleX = paddingLeft + (w - paddingLeft - paddingRight) / 2f
        circleY = paddingTop + (h - paddingTop - paddingBottom) / 2f
        if (w > 0 && h > 0)
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        setup()
    }

    // diameter in dp
    private fun createDrawable(): Drawable {
        val canvas = Canvas(mBitmap)

        canvas.drawCircle(
            circleX,
            circleY,
            mRadius + mBorderWidth,
            mBorderPaint
        )
        canvas.drawCircle(
            circleX,
            circleY,
            mRadius,
            mCirclePaint
        )
        return BitmapDrawable(resources, mBitmap)
    }

    private fun setup() {

        offset = textSize / 4

//        if (mSingleLetter) text = text[0].toString()

        mTextPaint.textSize = textSize
        mTextPaint.getTextBounds(text.toString(), 0, text.length, mRect)
        mTextWidth = mTextPaint.measureText(text.toString())
        mTextHeight = mRect.height().toFloat()

        mRadius =
            if (mTextWidth > mTextHeight - offset) (mTextWidth + 2 * offset) / 2 else mTextHeight / 2

        if (mBitmap != null)
            background = createDrawable()
    }

    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        setup()
    }


}
