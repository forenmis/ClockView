package com.example.clockview.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.clockview.R
import com.example.clockview.entity.Weather
import com.example.clockview.utils.dpToPx
import com.example.clockview.utils.spToPx
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin


class ClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintDivisionMin = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2f.dpToPx()
        color = Color.LTGRAY
        style = Paint.Style.STROKE
    }
    private val paintDivisionHours = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2f.dpToPx()
        style = Paint.Style.STROKE
        color = Color.DKGRAY
    }
    private val paintNumber = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.dpToPx()
        color = Color.DKGRAY
        style = Paint.Style.FILL
        textSize = 16f.dpToPx()
    }
    private val paintWhiteHours = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2f.dpToPx()
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val paintWhiteMinutes = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.dpToPx()
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND

    }
    private val paintHourArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 6f.dpToPx()
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND

    }
    private val paintMinArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 4f.dpToPx()
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val paintSecondArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2f.dpToPx()
        color = Color.RED
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val paintSecondSolid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 4f.dpToPx()
        color = Color.RED
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val paintCenter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.dpToPx()
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val paintCenterBlack = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.dpToPx()
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private val paintNumsInGreyCircles = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 22f.spToPx()
        style = Paint.Style.FILL
    }
    private val paintBlueCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.dpToPx()
        color = ContextCompat.getColor(context, R.color.blue_color)
        style = Paint.Style.FILL
    }
    private val paintYellowCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.dpToPx()
        color = ContextCompat.getColor(context, R.color.yellow_color)
        style = Paint.Style.FILL
    }
    private val paintGreyCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.dpToPx()
        color = Color.LTGRAY
        style = Paint.Style.FILL

    }

    private val rect = Rect()

    private val rectForGreyCircle = Rect()
    private val pathDivisionHours = Path()
    private val pathDivisionMin = Path()

    private val pairMap = hashMapOf<String, Pair<Float, Float>>()
    private val heartBeatBitmap: Bitmap

    private var radius = 0f
    private var radiusCircle = 0f
    private var radiusNum = 0f
    private var cx = 0f
    private var cy = 0f
    private var innerRadius = 0f
    private var outerRadius = 0f
    private var currentSeconds: Int = 0
    private var currentMinutes: Int = 0
    private var currentHours: Int = 0
    private var currentHeartBeat: Int = 90
    private var currentWeather: Weather = Weather.Sunny(20)
    private var currentIconWeather: Bitmap

    init {
        heartBeatBitmap =
            AppCompatResources.getDrawable(context, R.drawable.ic_heart_beat)!!.toBitmap()
        currentIconWeather =
            AppCompatResources.getDrawable(context, R.drawable.ic_weather_sunny)!!.toBitmap()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        cx = width / 2f
        cy = height / 2f
        radius = (if (width > height) cy else cx) - 16f.dpToPx()
        radiusNum = (if (width > height) cy else cx) - 48f.dpToPx()
        radiusCircle = radiusNum / 2f
        innerRadius = radius - 16f.dpToPx()
        outerRadius = radius
        calculateClockFace()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        pairMap.forEach { (text, coordinate) ->
            canvas.drawText(text, coordinate.first, coordinate.second, paintNumber)
        }
        canvas.drawCircle(cx - radiusNum / 2, cy, radiusCircle - 24f.dpToPx(), paintBlueCircle)
        canvas.drawCircle(cx + radiusNum / 2, cy, radiusCircle - 24f.dpToPx(), paintYellowCircle)
        canvas.drawCircle(cx, cy - radiusNum / 2, radiusCircle - 30f.dpToPx(), paintGreyCircle)
        canvas.drawCircle(cx, cy + radiusNum / 2, radiusCircle - 30f.dpToPx(), paintGreyCircle)
        canvas.drawPath(pathDivisionHours, paintDivisionHours)
        canvas.drawPath(pathDivisionMin, paintDivisionMin)
        drawWeather(canvas)
        drawHeartBeat(canvas, currentHeartBeat)
        drawHourArrow(canvas, currentHours)
        drawMinuteArrow(canvas, currentMinutes)
        drawSecondArrow(canvas, currentSeconds)
        canvas.drawCircle(cx, cy, 8f.dpToPx(), paintCenter)
        canvas.drawCircle(cx, cy, 2f.dpToPx(), paintCenterBlack)
        canvas.drawBitmap(
            heartBeatBitmap,
            (cx - heartBeatBitmap.width / 2.0f),
            (cy + radiusNum / 2f),
            null
        )
    }

    fun updTime(time: Calendar) {
        currentSeconds = time.get(Calendar.SECOND)
        currentMinutes = time.get(Calendar.MINUTE)
        currentHours = time.get(Calendar.HOUR)
        invalidate()
    }

    fun updHeartBeat(heartBeat: Int) {
        currentHeartBeat += heartBeat
        invalidate()
    }

    fun updWeather(weather: Weather) {
        currentWeather = weather
        currentIconWeather = when (weather) {
            is Weather.Cloud -> AppCompatResources.getDrawable(
                context,
                R.drawable.ic_weather_cloud
            )!!.toBitmap()

            is Weather.Snow -> AppCompatResources.getDrawable(context, R.drawable.ic_weather_snow)!!
                .toBitmap()

            is Weather.Sunny -> AppCompatResources.getDrawable(
                context,
                R.drawable.ic_weather_sunny
            )!!.toBitmap()

            is Weather.Wind -> AppCompatResources.getDrawable(context, R.drawable.ic_weather_wind)!!
                .toBitmap()
        }
        invalidate()
    }

    private fun calculateClockFace() {
        var hour = 3
        pairMap.clear()
        for (angle in 0 until 360 step 6) {
            val pointStart = calculateXY(innerRadius, angle.toFloat())
            val pointFinish = calculateXY(outerRadius, angle.toFloat())
            if (angle % 30 == 0) {
                val text = when (hour) {
                    in 0..12 -> hour.toString()
                    else -> (hour - 12).toString()
                }
                paintNumber.getTextBounds(text, 0, text.length, rect)
                val pairXY = calculateXY(radiusNum, angle.toFloat())
                pathDivisionHours.moveTo(pointStart.first, pointStart.second)
                pathDivisionHours.lineTo(pointFinish.first, pointFinish.second)
                pairMap[text] =
                    (pairXY.first - paintNumber.measureText(text) / 2f) to (pairXY.second + rect.height() / 2f)

                hour++
            } else {
                pathDivisionMin.moveTo(pointStart.first, pointStart.second)
                pathDivisionMin.lineTo(pointFinish.first, pointFinish.second)
            }

        }
    }

    private fun drawSecondArrow(canvas: Canvas, seconds: Int) {
        val angle = 360f / 60f * seconds.toFloat() - 90f
        val pairXY = calculateXY(radiusNum, angle)
        canvas.drawLine(cx, cy, pairXY.first, pairXY.second, paintSecondArrow)
        drawSecondSolid(canvas, seconds)
    }

    private fun drawSecondSolid(canvas: Canvas, seconds: Int) {
        val startAngle = 360f / 60f * seconds.toFloat() - 90f
        val endAngle = ((startAngle + 180f) % 360f)
        val endXYSolid = calculateXY(24f.dpToPx(), startAngle)
        val startXYSolid = calculateXY(12f.dpToPx(), endAngle)
        canvas.drawLine(
            startXYSolid.first,
            startXYSolid.second,
            endXYSolid.first,
            endXYSolid.second,
            paintSecondSolid
        )
    }

    private fun drawMinuteArrow(canvas: Canvas, minutes: Int) {
        val angle = 360f / 60f * minutes.toFloat() - 90f
        val pairXY = calculateXY(radiusNum, angle)
        canvas.drawLine(cx, cy, pairXY.first, pairXY.second, paintMinArrow)
        drawMinuteSolid(canvas, angle)
    }

    private fun drawMinuteSolid(canvas: Canvas, angle: Float) {
        val startXYSolid = calculateXY(radiusNum * 0.25f, angle)
        val endXYSolid = calculateXY(radiusNum - 2f.dpToPx(), angle)
        canvas.drawLine(
            startXYSolid.first,
            startXYSolid.second,
            endXYSolid.first,
            endXYSolid.second,
            paintWhiteMinutes
        )
    }

    private fun drawHourArrow(canvas: Canvas, hours: Int) {
        val angle = 360f / 12f * hours.toFloat() - 90f
        val radius = radiusNum / 2f
        val pair = calculateXY(radius, angle)
        canvas.drawLine(cx, cy, pair.first, pair.second, paintHourArrow)
        drawHourSolid(canvas, angle, radius)
    }

    private fun drawHourSolid(canvas: Canvas, angle: Float, radius: Float) {
        val startXYSolid = calculateXY(radius * 0.25f, angle)
        val endXYSolid = calculateXY(radius - 2f.dpToPx(), angle)
        canvas.drawLine(
            startXYSolid.first,
            startXYSolid.second,
            endXYSolid.first,
            endXYSolid.second,
            paintWhiteHours
        )
    }

    private fun drawHeartBeat(canvas: Canvas, heartBeat: Int) {
        val text = heartBeat.toString()
        paintNumsInGreyCircles.getTextBounds(text, 0, text.length, rectForGreyCircle)
        canvas.drawText(
            text,
            cx - paintNumsInGreyCircles.measureText(text) / 2f,
            cy + radiusNum / 2f - heartBeatBitmap.height / 4f,
            paintNumsInGreyCircles
        )
    }

    private fun drawWeather(canvas: Canvas) {
        rectForGreyCircle.setEmpty()
        val text = currentWeather.temperature.toString()
        paintNumsInGreyCircles.getTextBounds(text, 0, text.length, rectForGreyCircle)
        canvas.drawBitmap(
            currentIconWeather,
            (cx - currentIconWeather.width / 2.0f),
            (cy - radiusNum / 2f - currentIconWeather.height),
            null
        )
        canvas.drawText(
            "$text°",
            cx - paintNumsInGreyCircles.measureText(text) / 2f,
            cy - radiusNum / 2f + currentIconWeather.height,
            paintNumsInGreyCircles
        )
    }

    private fun calculateXY(radius: Float, angle: Float): Pair<Float, Float> {
        val x = (cx + radius * cos(Math.toRadians(angle.toDouble())).toFloat())
        val y = (cy + radius * sin(Math.toRadians(angle.toDouble())).toFloat())
        return x to y
    }
}