package com.savet.local.ocr.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.savet.local.baselibrary.utils.LogUtils
import com.savet.local.baselibrary.view.MinSeekBar
import com.savet.local.ocr.R
import com.savet.local.ocr.databinding.FragmentSettingsBinding
import com.savet.local.ocr.utils.BaseSettingUtils
import com.savet.local.ocr.utils.OcrUtils

class SettingsFragment : Fragment(), MinSeekBar.OnMinSeekBarChangeListener {

    companion object {
        private const val TAG: String = "SettingsFragment"
    }

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initAllViewListener()

        updateAll()

        return root
    }

    /**
     * 更新所有的UI显示
     *
     */
    private fun updateAll() {
        val boxThresh = (OcrUtils.getBoxThresh() * 100).toInt()
        val boxScoreThresh = (OcrUtils.getBoxScoreThresh() * 100).toInt()
        val lenRatio = (OcrUtils.getMaxSideLenRatio() * 100).toInt()
        val padding = OcrUtils.getPadding()
        val unClipRatio = (OcrUtils.getUnClipRatio() * 10).toInt()
        binding.boxThreshSeekBar.setFixProgress(boxThresh)
        binding.boxScoreThreshSeekBar.setFixProgress(boxScoreThresh)
        binding.maxSideLenRatioSeekBar.setFixProgress(lenRatio)
        binding.paddingSeekBar.setFixProgress(padding)
        binding.scaleUnClipRatioSeekBar.setFixProgress(unClipRatio)
        binding.doAngleSw.isChecked = OcrUtils.getDoAngle()
        binding.mostAngleSw.isChecked = OcrUtils.getMostAngle()

        binding.boxThreshTv.text =
            getString(R.string.box_thresh_f, OcrUtils.getBoxThresh())
        binding.boxScoreThreshTv.text =
            getString(R.string.box_score_thresh_f, OcrUtils.getBoxScoreThresh())
        binding.maxSideLenRatioTv.text =
            getString(R.string.len_ratio_f, lenRatio)
        binding.paddingTv.text = getString(R.string.padding_f, padding)
        binding.unClipRatioTv.text =
            getString(R.string.box_un_clip_ratio_f, OcrUtils.getUnClipRatio())

        binding.autoLoadImageSC.isChecked = BaseSettingUtils.getAutoLoadImage()
        binding.openAdvancedSettingSW.isChecked = BaseSettingUtils.getOpenAdvancedSetting()
        binding.fastDetectSC.isChecked = BaseSettingUtils.getFastDetect()

        controlViewEnableStatus(BaseSettingUtils.getOpenAdvancedSetting())
    }

    /**
     * 控制高级设置中的选项是否启用
     *
     * @param enable true - 启用
     */
    private fun controlViewEnableStatus(enable: Boolean) {
        binding.boxThreshSeekBar.isEnabled = enable
        binding.boxScoreThreshSeekBar.isEnabled = enable
        binding.maxSideLenRatioSeekBar.isEnabled = enable && !BaseSettingUtils.getFastDetect()
        binding.paddingSeekBar.isEnabled = enable && !BaseSettingUtils.getFastDetect()
        binding.scaleUnClipRatioSeekBar.isEnabled = enable
        binding.doAngleSw.isEnabled = enable && !BaseSettingUtils.getFastDetect()
        binding.mostAngleSw.isEnabled =
            enable && OcrUtils.getDoAngle() && !BaseSettingUtils.getFastDetect()
    }

    /**
     * 初始化所有控件的监听者
     *
     */
    private fun initAllViewListener() {
        binding.autoLoadImageSC.setOnCheckedChangeListener { _, isChecked ->
            BaseSettingUtils.setAutoLoadImage(isChecked)
        }

        binding.fastDetectSC.setOnCheckedChangeListener { _, isChecked ->
            BaseSettingUtils.setFastDetect(isChecked)
            if (isChecked) {
                OcrUtils.setMaxSideLenRatio(OcrUtils.FastMap.MAX_SIDE_LEN_RATIO_F.value as Float)
                OcrUtils.setPadding(OcrUtils.FastMap.PADDING_I.value as Int)
                OcrUtils.setDoAngle(OcrUtils.FastMap.DO_ANGLE_B.value as Boolean)
            }
            updateAll() // 重新载入
        }

        binding.openAdvancedSettingSW.setOnCheckedChangeListener { _, isChecked ->
            BaseSettingUtils.setOpenAdvancedSetting(isChecked)
            controlViewEnableStatus(isChecked)
        }

        binding.resetBtn.setOnClickListener {
            OcrUtils.resetAll()
            updateAll()// 重新载入
        }

        // 高级设置部分
        binding.boxThreshSeekBar.setOnMinSeekBarChangeListener(this)
        binding.boxScoreThreshSeekBar.setOnMinSeekBarChangeListener(this)
        binding.maxSideLenRatioSeekBar.setOnMinSeekBarChangeListener(this)
        binding.paddingSeekBar.setOnMinSeekBarChangeListener(this)
        binding.paddingSeekBar.setFixProgress(OcrUtils.getPadding())
        binding.scaleUnClipRatioSeekBar.setOnMinSeekBarChangeListener(this)
        binding.doAngleSw.setOnCheckedChangeListener { _, isChecked ->
            OcrUtils.setDoAngle(isChecked)
            binding.mostAngleSw.isEnabled = isChecked
        }
        binding.mostAngleSw.setOnCheckedChangeListener { _, isChecked ->
            OcrUtils.setMostAngle(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProgressChanged(seekBar: MinSeekBar?, progress: Int, fromUser: Boolean) {
        LogUtils.d(TAG, "progress : $progress")
        seekBar ?: return
        when (seekBar.id) {
            R.id.maxSideLenRatioSeekBar -> {
                updateMaxSideLen(progress)
            }
            R.id.paddingSeekBar -> {
                updatePadding(progress)
            }
            R.id.boxScoreThreshSeekBar -> {
                updateBoxScoreThresh(progress)
            }
            R.id.boxThreshSeekBar -> {
                updateBoxThresh(progress)
            }
            R.id.scaleUnClipRatioSeekBar -> {
                updateUnClipRatio(progress)
            }
            else -> {
                LogUtils.i(TAG, "unknown id : ${seekBar.id}")
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: MinSeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: MinSeekBar?) {

    }

    private fun updateMaxSideLen(progress: Int) {
        val ratio = progress.toFloat() / 100.toFloat()
        binding.maxSideLenRatioTv.text = getString(R.string.len_ratio_f, progress)
        OcrUtils.setMaxSideLenRatio(ratio)
    }

    private fun updatePadding(progress: Int) {
        binding.paddingTv.text = getString(R.string.padding_f, progress)
        OcrUtils.setPadding(progress)
    }

    private fun updateBoxScoreThresh(progress: Int) {
        val thresh = progress.toFloat() / 100.toFloat()
        binding.boxScoreThreshTv.text = getString(R.string.box_score_thresh_f, thresh)
        OcrUtils.setBoxScoreThresh(thresh)
    }

    private fun updateBoxThresh(progress: Int) {
        val thresh = progress.toFloat() / 100.toFloat()
        binding.boxThreshTv.text = getString(R.string.box_thresh_f, thresh)
        OcrUtils.setBoxThresh(thresh)
    }

    private fun updateUnClipRatio(progress: Int) {
        val scale = progress.toFloat() / 10.toFloat()
        binding.unClipRatioTv.text = getString(R.string.box_un_clip_ratio_f, scale)
        OcrUtils.setUnClipRatio(scale)
    }
}