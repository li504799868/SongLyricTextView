package com.lzp.songlyrictextview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.util.AttributeSet
import android.view.animation.LinearInterpolator

/**
 * @author li.zhipeng
 *
 *      歌词变色TextView
 * */
class SongLyricTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    /**
     * 文字为播放的颜色
     * */
    private val normalColor = Color.parseColor("#333333")

    /**
     * 文字播放的颜色
     * */
    private val playColor = Color.parseColor("#fec403")

    /**
     * 要变色的宽度
     * */
    private var consumeWidth: Float = 0f

    /**
     * 是否正在播放中
     * */
    private var isPlaying = false

    private val mPaint = paint

    /**
     * 要变色的区域
     * */
    private val path = Path()

    /**
     * 负责变色动画类
     * */
    private val animator by lazy {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            consumeWidth = it.animatedValue as Float
            invalidate()
        }
        animator
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = normalColor
        super.onDraw(canvas)

        if (layout == null) {
            invalidate()
            return
        }
        // 是否是播放状态
        if (isPlaying) {
            path.reset()
            val lineCount = layout.lineCount
            val content = text.toString()
            for (i in 0 until lineCount) {
                // 计算一行文字的宽度
                val lineWidth = mPaint.measureText(
                    content.substring(layout.getLineStart(i), layout.getLineEnd(i))
                )

                if (lineWidth <= consumeWidth) {
                    // 如果是之前已经变色区域，直接添加到path中
                    consumeWidth -= lineWidth
                    path.addRect(
                        layout.getLineLeft(i),
                        layout.getLineTop(i).toFloat(),
                        layout.getLineRight(i),
                        layout.getLineBottom(i).toFloat(),
                        Path.Direction.CCW
                    )
                } else {
                    // 如果该行正好是要变色的行，直接改变颜色
                    // 把需要的consumeWidth放入path中
                    path.addRect(
                        layout.getLineLeft(i),
                        layout.getLineTop(i).toFloat(),
                        layout.getLineLeft(i) + consumeWidth,
                        layout.getLineBottom(i).toFloat(),
                        Path.Direction.CCW
                    )
                    break
                }
            }
            // 设置需要绘制的区域，并着色
            canvas.clipPath(path)
            mPaint.color = playColor
            layout.draw(canvas)
        }
    }

    /**
     * 开始播放
     *
     * @param startIndex 开始的文字的索引
     * @param endIndex 结束的文字的索引
     * @param duration 播放时长
     * */
    fun startPlayLine(startIndex: Int, endIndex: Int, duration: Long) {
        isPlaying = true
        if (startIndex == -1) return
        val startWidth = mPaint.measureText(this.text.substring(0, startIndex))
        val endWidth = startWidth + mPaint.measureText(this.text.substring(startIndex, endIndex))
        animator.setFloatValues(startWidth, endWidth)
        animator.duration = if (duration > 0) duration else 1000
        animator.start()
    }

    /**
     * 停止播放
     * */
    fun stopPlay() {
        isPlaying = false
        animator.cancel()
        invalidate()
    }

}