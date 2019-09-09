package com.jiujiu.circletextview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jiujiu.lib.CircleSize
import com.jiujiu.lib.CircleTextView
import kotlinx.android.synthetic.main.activity_main_test.*
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)
    }

    private val tests = arrayOf("Hello", "TEST", "abcd", "D")

    fun setText(view: View) {
        ctv.setText(tests[Random.nextInt(4)])
        ctv2.setText(tests[Random.nextInt(4)])
    }

    fun setTestSize(view: View) {
        ctv.setTestSize(Random.nextInt(20, 50))
        ctv2.setTestSize(Random.nextInt(20, 50))
    }

    fun setTextColor(view: View) {
        ctv.setTextColor(Random.nextColor())
        ctv2.setTextColor(Random.nextColor())
    }

    fun setCircleColor(view: View) {
        ctv.setCircleColor(Random.nextColor())
        ctv2.setCircleColor(Random.nextColor())
    }

    fun setBorderColor(view: View) {
        ctv.setBorderColor(Random.nextColor())
        ctv2.setBorderColor(Random.nextColor())
    }

    fun setBorderWidth(view: View) {
        ctv.setBorderWidth(Random.nextInt(1, 12))
        ctv2.setBorderWidth(Random.nextInt(1, 12))
    }

    fun setHasShadow(view: View) {
        ctv.setShadow(!ctv.hasShadow())
        ctv2.setShadow(!ctv2.hasShadow())
    }

    fun setMaxLength(view: View) {
        ctv.setMaxLength(
            if (ctv.getText().length == 1) 1 else Random.nextInt(
                1,
                ctv.getText().length
            )
        )
        ctv2.setMaxLength(
            if (ctv2.getText().length == 1) 1 else Random.nextInt(
                1,
                ctv2.getText().length
            )
        )
    }

    fun setTextFill(view: View) {
        ctv.setTextStyle(CircleTextView.FILL)
        ctv2.setTextStyle(CircleTextView.FILL)
    }

    fun setTextBorder(view: View) {
        ctv.setTextStyle(CircleTextView.EMPTY)
        ctv2.setTextStyle(CircleTextView.EMPTY)
    }

    fun setOffset(view: View) {
        ctv.setOffset(Random.nextInt(10, 50))
        ctv2.setOffset(Random.nextInt(10, 50))
    }

    fun setLetterSpace(view: View) {
        ctv.setLetterSpace(Random.nextInt(1, 10) / 10f)
        ctv2.setLetterSpace(Random.nextInt(1, 10) / 10f)
    }

    fun setCircleSizeWrapText(view: View) {
        ctv2.setCircleSize(CircleSize.WRAP_TEXT)
    }

    fun setCircleSizeMatchParent(view: View) {
        ctv2.setCircleSize(CircleSize.MATCH_PARENT)
    }
}
