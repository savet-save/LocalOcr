package com.savet.local.baselibrary.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.savet.local.baselibrary.R

class ScanView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val SCAN_DURATION_DEFAULT = 3000 // 默认扫描时间
        const val SCAN_LINE_WIDTH_DEFAULT = 6f // 默认线条宽度
        const val SCAN_LINE_COLOR_DEFAULT = Color.GREEN // 默认颜色
    }

    private val scanLinePaint = Paint()
    private var scanLinePosition = 0f

    private val scanAnimator: ValueAnimator

    // 控制动画是否开始
    private var isAnimating: Boolean = false


    private var scanDuration: Int = SCAN_DURATION_DEFAULT
    private var scanLineWidth: Float = SCAN_LINE_WIDTH_DEFAULT
    private var scanLineColor : Int = SCAN_LINE_COLOR_DEFAULT

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ScanView)
        scanDuration = attributes.getInt(R.styleable.ScanView_scanDuration, SCAN_DURATION_DEFAULT)
        scanLineWidth = attributes.getDimension(R.styleable.ScanView_scanLineWidth, SCAN_LINE_WIDTH_DEFAULT)
        scanLineColor = attributes.getColor(R.styleable.ScanView_scanLineColor, Color.BLACK)
        attributes.recycle()

        scanLinePaint.strokeWidth = scanLineWidth  // 线条的宽度

        scanAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = scanDuration.toLong() // 扫描动画的时长
            repeatMode = ValueAnimator.REVERSE // 设置往复模式
            repeatCount = ValueAnimator.INFINITE // 设置无限循环
            interpolator = AccelerateDecelerateInterpolator() // 设置加速减速插值器
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                scanLinePosition = height * progress
                invalidate()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 创建线性渐变
        val colors = intArrayOf(Color.TRANSPARENT, scanLineColor, Color.TRANSPARENT)
        val positions = floatArrayOf(0f, 0.5f, 1f)

        // 设置线性渐变的起始点和结束点坐标，以覆盖整个控件
        val shader = LinearGradient(0f, 0f, w.toFloat(), 0f, colors, positions, Shader.TileMode.CLAMP)
        scanLinePaint.shader = shader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startX = 0f
        val endX = width.toFloat()
        val scanLineY = scanLinePosition

        // 绘制实际的线条
        canvas.drawLine(startX, scanLineY, endX, scanLineY, scanLinePaint)

    }

    override fun onDetachedFromWindow() {
        isAnimating = false
        scanAnimator.cancel()
        super.onDetachedFromWindow()
    }

    /**
     * 开始扫描
     *
     */
    fun startScan() {
        if (!isAnimating) {
            isAnimating = true
            scanAnimator.start()
        }
    }

    /**
     * 停止扫描
     *
     */
    fun stopScan() {
        if (isAnimating) {
            scanAnimator.cancel()
            isAnimating = false
        }
    }

    /**
     * 重新开始扫描
     *
     */
    fun reStartScan() {
        scanLinePosition = 0f
        startScan()
    }
}

