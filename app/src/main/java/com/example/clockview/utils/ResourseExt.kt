package com.example.clockview.utils

import android.content.res.Resources

fun Float.dpToPx(): Float {
    return (this * Resources.getSystem().displayMetrics.density)
}

fun Float.spToPx(): Float {
    return (this * Resources.getSystem().displayMetrics.scaledDensity)
}