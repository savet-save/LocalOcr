package com.savet.local.ocr.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.savet.local.baselibrary.LogUtils
import com.savet.local.ocr.databinding.FragmentGalleryBinding
import com.savet.local.ocr.ui.adapter.DetectResultAdapter
import com.savet.local.ocr.utils.OcrUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*


class GalleryFragment : Fragment() {

    companion object {
        private const val TAG: String = "GalleryFragment"
    }

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var _galleryViewModel : GalleryViewModel? = null

    private val galleryViewModel get() = _galleryViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _galleryViewModel =
            ViewModelProvider(this)[GalleryViewModel::class.java]

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val imageView = binding.imageView
        Glide.with(this@GalleryFragment)
            .load("file:///android_asset/test.png")
            .into(imageView)

        binding.recyclerViewDetectResult.layoutManager = getLayoutManager()
        binding.recyclerViewDetectResult.addItemDecoration(DetectResultAdapter.DetectItemDecoration())

        return root
    }

    override fun onStart() {
        super.onStart()

        binding.fab.setOnClickListener {
            detect()
        }
    }

    private fun detect() {

        flow {
            val drawable = binding.imageView.drawable
            drawable ?: return@flow
            val bitmap = drawable.toBitmap()
            val ocrResult = OcrUtils.detect(bitmap)
            emit(ocrResult)
        }.flowOn(Dispatchers.IO)
            .onStart {
                showLoading()
                Snackbar.make(binding.root, "开始识别", Snackbar.LENGTH_LONG).show()
            }.onEach {
                LogUtils.d(TAG, "each : ${Thread.currentThread().name}")
                LogUtils.d(TAG, it.strRes)
//                binding.progressBar.hide()
                Glide.with(this@GalleryFragment)
                    .load(it.boxImg)
                    .skipMemoryCache(true) // 跳过内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 跳过磁盘缓存
                    .into(binding.imageView)
                // 处理识别结果并显示
                flow {
                    emit(galleryViewModel.getDetectAdapterDateList(it))
                }.flowOn(Dispatchers.Default)
                    .onEach { array ->
                        binding.recyclerViewDetectResult.adapter = DetectResultAdapter(array)
                    }.launchIn(lifecycleScope)

            }.catch {
                it.printStackTrace()
            }.launchIn(lifecycleScope)
    }


    private fun showLoading() {
//        binding.progressBar.visibility = View.VISIBLE
//        binding.progressBar.show()
//        Glide.with(this@GalleryFragment).load(R.drawable.anim_loading).into(binding.imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _galleryViewModel = null
    }

    /**
     * 获得布局管理器
     *
     * @return FlexboxLayoutManager
     */
    private fun getLayoutManager(): FlexboxLayoutManager {
        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW //从左往右, 从上到下
        layoutManager.flexWrap = FlexWrap.WRAP //自动换行
        layoutManager.justifyContent = JustifyContent.FLEX_START //主轴(水平)左对齐
        return layoutManager
    }


}