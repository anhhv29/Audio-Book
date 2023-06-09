package vn.gotech.audiobook.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class CustomImageViewZoom @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyle), OnTouchListener {


    private val matrixCurrent = Matrix()
    private val savedMatrix = Matrix()

    private var mode = NONE

    private val mStartPoint = PointF()
    private val mMiddlePoint = PointF()
    private val mBitmapMiddlePoint = Point()

    private var oldDist = 1f
    private val matrixValues = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var scale: Float = 0.toFloat()
    private var oldEventX = 0f
    private var oldEventY = 0f
    private var oldStartPointX = 0f
    private var oldStartPointY = 0f
    private var mViewWidth = -1
    private var mViewHeight = -1
    private var mBitmapWidth = -1
    private var mBitmapHeight = -1
    private var mDraggable = false

    init {
        setOnTouchListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w
        mViewHeight = h
    }

    fun setBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            setImageBitmap(bitmap)

            mBitmapWidth = bitmap.width
            mBitmapHeight = bitmap.height
            mBitmapMiddlePoint.x = mViewWidth / 2 - mBitmapWidth / 2
            mBitmapMiddlePoint.y = mViewHeight / 2 - mBitmapHeight / 2

            matrixCurrent.postTranslate(
                mBitmapMiddlePoint.x.toFloat(),
                mBitmapMiddlePoint.y.toFloat()
            )
            this.imageMatrix = matrixCurrent
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrixCurrent)
                mStartPoint.set(event.x, event.y)
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrixCurrent)
                    midPoint(mMiddlePoint, event)
                    mode = ZOOM
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                drag(event)
            } else if (mode == ZOOM) {
                zoom(event)
            }
        }

        return true
    }


    private fun drag(event: MotionEvent) {
        matrixCurrent.getValues(matrixValues)

        val left = matrixValues[2]
        val top = matrixValues[5]
        val bottom = top + matrixValues[0] * mBitmapHeight - mViewHeight
        val right = left + matrixValues[0] * mBitmapWidth - mViewWidth

        var eventX = event.x
        var eventY = event.y
        val spacingX = eventX - mStartPoint.x
        val spacingY = eventY - mStartPoint.y
        val newPositionLeft = (if (left < 0) spacingX else spacingX * -1) + left
        val newPositionRight = spacingX + right
        val newPositionTop = (if (top < 0) spacingY else spacingY * -1) + top
        val newPositionBottom = spacingY + bottom
        var x = true
        var y = true

        if (newPositionRight < 0.0f || newPositionLeft > 0.0f) {
            if (newPositionRight < 0.0f && newPositionLeft > 0.0f) {
                x = false
            } else {
                eventX = oldEventX
                mStartPoint.x = oldStartPointX
            }
        }
        if (newPositionBottom < 0.0f || newPositionTop > 0.0f) {
            if (newPositionBottom < 0.0f && newPositionTop > 0.0f) {
                y = false
            } else {
                eventY = oldEventY
                mStartPoint.y = oldStartPointY
            }
        }

        if (mDraggable) {
            matrixCurrent.set(savedMatrix)
            matrixCurrent.postTranslate(
                if (x) eventX - mStartPoint.x else 0F,
                if (y) eventY - mStartPoint.y else 0F
            )
            this.imageMatrix = matrixCurrent
            if (x) oldEventX = eventX
            if (y) oldEventY = eventY
            if (x) oldStartPointX = mStartPoint.x
            if (y) oldStartPointY = mStartPoint.y
        }

    }

    private fun zoom(event: MotionEvent) {
        matrixCurrent.getValues(matrixValues)

        val newDist = spacing(event)
        val bitmapWidth = matrixValues[0] * mBitmapWidth
        val bimtapHeight = matrixValues[0] * mBitmapHeight
        val `in` = newDist > oldDist

        if (!`in` && matrixValues[0] < 1) {
            return
        }
        mDraggable = bitmapWidth > mViewWidth || bimtapHeight > mViewHeight

        val midX = (mViewWidth / 2).toFloat()
        val midY = (mViewHeight / 2).toFloat()

        matrixCurrent.set(savedMatrix)
        scale = newDist / oldDist
        matrixCurrent.postScale(
            scale,
            scale,
            if (bitmapWidth > mViewWidth) mMiddlePoint.x else midX,
            if (bimtapHeight > mViewHeight) mMiddlePoint.y else midY
        )

        this.imageMatrix = matrixCurrent


    }


    /** Determine the space between the first two fingers  */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)

        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /** Calculate the mid point of the first two fingers  */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    companion object {

        internal const val NONE = 0
        internal const val DRAG = 1
        internal const val ZOOM = 2
    }


}