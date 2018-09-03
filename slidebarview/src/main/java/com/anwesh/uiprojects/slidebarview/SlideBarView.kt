package com.anwesh.uiprojects.slidebarview

/**
 * Created by anweshmishra on 03/09/18.
 */

import android.app.Activity
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
        paint.color = Color.parseColor("#212121")
        drawCircle(gap * scale, 0f, gap/10, paint)
    }
    restore()
}

class SlideBarView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SBNode(var i : Int, val state : State = State()) {
        var prev : SBNode? = null
        var next : SBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, currI : Int, dir : Int, paint : Paint) {
            if (dir == -1) {
                next?.draw(canvas, currI, dir, paint)
            }
            canvas.drawSlideBarNode(i, state.scale, currI, paint)
            if (dir == 1) {
                next?.draw(canvas, currI, dir, paint)
            }
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SBNode {
            var curr : SBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class SlideBar(var i : Int) {

        private var root : SBNode = SBNode(0)
        private var curr : SBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, curr.i, dir, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SlideBarView) {
        private val animator : Animator = Animator(view)
        private val slideBar : SlideBar = SlideBar(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            slideBar.draw(canvas, paint)
            animator.animate {
                slideBar.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            slideBar.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : SlideBarView {
            val view : SlideBarView = SlideBarView(activity)
            activity.setContentView(view)
            return view
        }
    }
 }