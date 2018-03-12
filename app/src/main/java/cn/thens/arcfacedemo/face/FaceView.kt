package cn.thens.arcfacedemo.face

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView

class FaceView(context: Context?, attrs: AttributeSet? = null) : ImageView(context, attrs) {
    private val faceDetector by lazy { ArcFaceHelper.createDetector() }

    override fun setImageBitmap(bitmap: Bitmap) {
        val facedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        bitmap.recycle()
        val canvas = Canvas(facedBitmap)
        val paint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 16F
        }
        faceDetector.detect(facedBitmap).forEach {
            canvas.drawRect(it.rect, paint)
        }
        super.setImageBitmap(facedBitmap)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        faceDetector.destroy()
    }
}