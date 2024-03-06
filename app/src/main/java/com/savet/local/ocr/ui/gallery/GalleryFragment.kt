package com.savet.local.ocr.ui.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.savet.local.ocr.ui.manager.ControlScrollLayoutManager
import com.savet.local.ocr.utils.OcrUtils
import com.savet.local.ocr.utils.copyToClipboard
import com.savet.local.ocr.utils.getLatestImageUri
import com.savet.local.ocr.utils.isAllGranted
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

    private var _galleryViewModel: GalleryViewModel? = null

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

        setOnClickListener()

        initRecyclerView()

        registerForActivity {
            showImage(it)
        }

        return root
    }

    private fun setOnClickListener() {
        binding.detectButton.setOnClickListener {
            detect()
        }

        binding.copyContentButton.setOnClickListener {
            copyContent()
        }

        binding.pickImageButton.setOnClickListener {
            pickImageFromGallery()
        }
    }


    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>

    /**
     * 请求权限成功执行
     */
    private var requestPermissionSuccess: () -> Unit = {}

    /**
     * 统一注册
     *
     * fragment:1.3.0-alpha08 之后，
     * 需要在Fragment注册registerForActivityResult时，注册体需要写在onAttach() 或 onCreate()里
     *
     * @param getImageUri 获得图片
     */
    private fun registerForActivity(getImageUri: (Uri) -> Unit) {
        getImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedImageUri: Uri? = result.data?.data
                    // 处理选择的图片 URI
                    selectedImageUri?.also {
                        getImageUri(selectedImageUri)
                    }
                }
            }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // 用户授予了权限
                    requestPermissionSuccess()
                } else {
                    // 用户拒绝了权限，可以给出相应提示
                    Snackbar.make(binding.root, "权限获取失败", Snackbar.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        LogUtils.d(TAG, "onResume")
        requestStoragePermission()
        context?.getLatestImageUri()?.also {
            showImage(it)
        }
    }

    private fun showImage(it: Uri) {
        binding.detectImageView.visibility = View.VISIBLE
        Glide.with(this@GalleryFragment)
            .load(it)
            .into(binding.detectImageView)
    }

    /**
     * 获取读取权限，失败给与提示
     *
     * @param hasPermission 拥有权限或请求成功时执行
     */
    private fun requestStoragePermission(hasPermission: () -> Unit = {}) {
        val readStoragePermission: String = Manifest.permission.READ_EXTERNAL_STORAGE
        if (!requireContext().isAllGranted(readStoragePermission)) {
            this.requestPermissionSuccess = hasPermission
            requestPermissionLauncher.launch(readStoragePermission)
        } else {
            hasPermission()
        }
    }

    /**
     * 通过相册选择一个照片的uri
     *
     */
    private fun pickImageFromGallery() {
        requestStoragePermission{
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }

            getImageLauncher.launch(intent)
        }

    }

    // 识别图像
    private fun detect() {

        flow {
            binding.detectImageView.subsampling
            LogUtils.d(TAG, "")
            val drawable = binding.detectImageView.drawable
            drawable ?: return@flow
            val bitmap = drawable.toBitmap()
            val ocrResult = OcrUtils.detect(bitmap)
            emit(ocrResult)
        }.flowOn(Dispatchers.IO)
            .onStart {
                showLoading()
                Snackbar.make(binding.root, "开始识别", Snackbar.LENGTH_LONG).show()
            }.onEach {
//                LogUtils.d(TAG, it.strRes)
//                binding.progressBar.hide()
                Glide.with(this@GalleryFragment)
                    .load(it.boxImg)
                    .skipMemoryCache(true) // 跳过内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 跳过磁盘缓存
                    .into(binding.detectImageView)
                binding.copyContentButton.visibility = View.VISIBLE
                binding.gl1.setGuidelinePercent(0.4f) // 用于显示recyclerView
                // 处理识别结果并显示
                flow {
                    emit(galleryViewModel.getDetectAdapterDateList(it))
                }.flowOn(Dispatchers.Default)
                    .onEach { array ->
                        binding.detectResultRV.adapter = DetectResultAdapter(array)
                        // 用于确保焦点在最开始
                        binding.detectResultRV.layoutManager?.apply {
                            scrollToPosition(0)
                            LogUtils.d(TAG, "scrollToPosition to zero")
                        }
                    }.launchIn(lifecycleScope)

            }.catch {
                it.printStackTrace()
            }.launchIn(lifecycleScope)
    }

    /**
     * 拷贝内容到剪切板
     *
     */
    private fun copyContent() {
        val adapter = binding.detectResultRV.adapter as? DetectResultAdapter
        adapter?.also {
            it.getSelectContent().copyToClipboard(requireContext(), TAG)
            Snackbar.make(binding.root, "拷贝到系统剪切板", Snackbar.LENGTH_LONG).show()
        }
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
        val layoutManager = ControlScrollLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW //从左往右, 从上到下
        layoutManager.flexWrap = FlexWrap.WRAP //自动换行
        layoutManager.justifyContent = JustifyContent.FLEX_START //主轴(水平)左对齐
        return layoutManager
    }

    /**
     * 初始化识别结果展示控件
     */
    private fun initRecyclerView() {
        binding.detectResultRV.layoutManager = getLayoutManager()
        binding.detectResultRV.addItemDecoration(DetectResultAdapter.DetectItemDecoration())
        binding.detectResultRV.addOnItemTouchListener(DetectResultAdapter.SelectListener())
        binding.detectResultRV.itemAnimator = null  // 取消动画
        binding.detectResultRV.setHasFixedSize(true) // ItemView的高度固定
        binding.detectResultRV.setItemViewCacheSize(16) // 设置RecyclerView缓存
    }


}