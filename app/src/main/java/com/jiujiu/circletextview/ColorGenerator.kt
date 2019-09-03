package com.jiujiu.circletextview

import android.graphics.Color
import kotlin.random.Random


fun Random.nextColor(): Int {
    return Color.argb(
        255,
        Random.Default.nextInt(256),
        Random.Default.nextInt(256),
        Random.Default.nextInt(256)
    )
}
