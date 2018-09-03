package com.anwesh.uiprojects.slidebarview

/**
 * Created by anweshmishra on 03/09/18.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.content.Context
import android.graphics.Color

val nodes : Int = 5

fun Canvas.drawSlideBarNode(i : Int, scale : Float, currI : Int, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = (w * 0.9f) / nodes
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(0.05f * w + gap * i, h/2)
    paint.color = Color.parseColor("#616161")
    drawLine(0f, 0f, gap, 0f, paint)
    paint.color = Color.parseColor("#1976D2")
    drawLine(0f, 0f, gap * scale, 0f, paint)
    if (i == currI) {
        paint.color = Color.parseColor("#FFD600")
        drawCircle(gap * scale, 0f, gap/10, paint)
    }
    restore()
}

class SlideBarView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}