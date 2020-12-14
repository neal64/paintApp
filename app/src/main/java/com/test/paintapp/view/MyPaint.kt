package com.test.paintapp.view


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.test.paintapp.AppConstant

class MyPaint @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mPath = Path()
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val tolerance = ViewConfiguration.get(context).scaledTouchSlop
    private var currentX = 0f
    private var currentY = 0f
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    var selectedColor: Int = Color.BLACK
        get() = field
        set(value) {
            field = value
        }
    private var paint = Paint().apply {
        color = selectedColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = AppConstant.PAINT_STROKE_WIDTH
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(AppConstant.BG_COLOR)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath.reset()
                mPath.moveTo(motionTouchEventX, motionTouchEventY)
                currentX = motionTouchEventX
                currentY = motionTouchEventY
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(motionTouchEventX - currentX)
                val dy = Math.abs(motionTouchEventY - currentY)
                if (dx >= tolerance || dy >= tolerance) {
                    mPath.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
                    currentX = motionTouchEventX
                    currentY = motionTouchEventY
                    extraCanvas.drawPath(mPath, paint)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                mPath.reset()
            }
        }
        return true
    }

    fun eraser() {
        paint.apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            strokeWidth = AppConstant.ERASER_STROKE_WIDTH
        }
    }

    fun startPaint() {
        paint.apply {
            xfermode = null
            strokeWidth = AppConstant.PAINT_STROKE_WIDTH
        }
    }

    fun changeBrushColor() {
        paint.apply {
            color = selectedColor
        }
        invalidate()
    }

    fun clearView() {
        extraBitmap.eraseColor(Color.TRANSPARENT);
        mPath.reset();
        invalidate();
    }

}