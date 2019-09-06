package com.jiujiu.circletextview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
    }

    fun setTestSize(view: View) {
        ctv.setTestSize(Random.nextInt(20, 50))
    }

    fun setTextColor(view: View) {
        ctv.setTextColor(Random.nextColor())
    }

    fun setCircleColor(view: View) {
        ctv.setCircleColor(Random.nextColor())
    }

    fun setBorderColor(view: View) {
        ctv.setBorderColor(Random.nextColor())
    }

    fun setBorderWidth(view: View) {
        ctv.setBorderWidth(Random.nextInt(1, 12))
    }

    fun setTextStyle(view: View) {

    }

    fun setHasShadow(view: View) {
        ctv.setShadow(!ctv.hasShadow())
    }

    fun setMaxLength(view: View) {
        ctv.setMaxLength(
            if (ctv.getText().length == 1) 1 else Random.nextInt(
                1,
                ctv.getText().length
            )
        )
    }

    fun setTextFill(view: View) {
        ctv.setTextStyle(CircleTextView.FILL)
    }

    fun setTextBorder(view: View) {
        ctv.setTextStyle(CircleTextView.EMPTY)
    }

    fun setOffset(view: View) {
        ctv.setOffset(Random.nextInt(10, 50))
    }

    fun setLetterSpace(view: View) {
        ctv.setLetterSpace(Random.nextInt(1, 10) / 10f)
    }
}
