package com.savet.local.baselibrary.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import com.savet.local.baselibrary.R

/**
 * can use app:minValue control seek bar
 *
 * @author savet
 * @date 2024/3/7
 */
class MinSeekBar : AppCompatSeekBar {

    companion object {
        private const val MIN_VALUE_DEFAULT: Int = 0
        private const val TAG: String = "MinSeekBar"
    }

    /**
     * Progress change or touch listener
     */
    interface OnMinSeekBarChangeListener {
        /**
         * Notification that the progress level has changed. Clients can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         *
         * @param seekBar The SeekBar whose progress has changed
         * @param progress The current progress level. This will be in the range min..max where min
         * and max were set by [ProgressBar.setMin] and
         * [ProgressBar.setMax], respectively. (The default values for
         * min is 0 and max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        fun onProgressChanged(seekBar: MinSeekBar?, progress: Int, fromUser: Boolean)

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar.
         * @param seekBar The SeekBar in which the touch gesture began
         */
        fun onStartTrackingTouch(seekBar: MinSeekBar?)

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         * @param seekBar The SeekBar in which the touch gesture began
         */
        fun onStopTrackingTouch(seekBar: MinSeekBar?)
    }

    /**
     * min value
     */
    private var minValue = MIN_VALUE_DEFAULT

    private var listener: OnMinSeekBarChangeListener? = null

    init {

        /**
         * proxy
         */
        super.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listener?.onProgressChanged(seekBar as MinSeekBar, progress + minValue, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                listener?.onStartTrackingTouch(seekBar as MinSeekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                listener?.onStopTrackingTouch(seekBar as MinSeekBar)
            }
        })

    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MinSeekBar)
        val min = attributes.getInt(R.styleable.MinSeekBar_minValue, MIN_VALUE_DEFAULT)
        attributes.recycle()

        setFixMin(min)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MinSeekBar)
        val min = attributes.getInt(R.styleable.MinSeekBar_minValue, MIN_VALUE_DEFAULT)
        attributes.recycle()

        setFixMin(min)
    }

    /**
     * set min value
     *
     * @param value value, can't more than [AppCompatSeekBar.getMax] + [getFixMin]
     */
    fun setFixMin(value: Int) {
        if (value >= (super.getMax() + minValue) || value < 0) {
            minValue = 0
        } else {
            minValue = value
            super.setMax(super.getMax() - minValue)
        }
    }

    /**
     * get min value
     *
     * @return min value
     */
    fun getFixMin(): Int {
        return minValue
    }

    /**
     * set max value
     *
     * @param max value
     */
    fun setFixMax(max: Int) {
        super.setMax(max - minValue)
    }

    /**
     * get max value
     *
     * @return max value
     */
    fun getFixMax(): Int {
        return super.getMax() + minValue
    }

    /**
     * set progress
     *
     * @param progress progress
     * @see AppCompatSeekBar.setProgress
     */
    fun setFixProgress(progress: Int) {
        super.setProgress(progress - minValue)
    }

    /**
     * set progress
     *
     * @param progress progress
     * @param animate animate
     * @see AppCompatSeekBar.setProgress
     */
    fun setFixProgress(progress: Int, animate: Boolean) {
        super.setProgress(progress - minValue, animate)
    }

    /**
     * get progress
     *
     * @return progress
     */
    fun getFixProgress() : Int {
        return super.getProgress() + minValue
    }

    @Deprecated("not use, please use [MinSeekBar.setOnMinSeekBarChangeListener]")
    override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener?) {
        // nothing
        Log.w(
            TAG, "not supper use setOnSeekBarChangeListener," +
                    " pls use setOnMinSeekBarChangeListener()"
        )
    }

    /**
     * Sets a listener to receive notifications of changes to the SeekBar's progress level. Also
     * provides notifications of when the user starts and stops a touch gesture within the SeekBar.
     *
     * @param l The min seek bar notification listener
     *
     * @see MinSeekBar.OnMinSeekBarChangeListener
     */
    fun setOnMinSeekBarChangeListener(l: OnMinSeekBarChangeListener?) {
        this.listener = l
    }

}
