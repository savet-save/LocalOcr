package com.savet.local.ocr.ui.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.TooltipCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.savet.local.baselibrary.utils.LogUtils
import com.savet.local.baselibrary.utils.ToastUtils
import com.savet.local.ocr.R
import com.savet.local.ocr.base.BackPressedListener
import com.savet.local.ocr.base.BaseFragment
import com.savet.local.ocr.databinding.FragmentGalleryBinding
import com.savet.local.ocr.type.ImageData
import com.savet.local.ocr.type.TakePhotoContract
import com.savet.local.ocr.ui.adapter.DetectResultAdapter
import com.savet.local.ocr.ui.manager.ControlScrollLayoutManager
import com.savet.local.ocr.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*


class GalleryFragment : BaseFragment() {

    companion object {
        private const val TAG: String = "GalleryFragment"
    }

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var _galleryViewModel: GalleryViewModel? = null

    private val galleryViewModel get() = _galleryViewModel!!

    /**
     * 保存选择/拍摄的图像
     */
    private var savedImage: ImageData = ImageData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _galleryViewModel =
            ViewModelProvider(this)[GalleryViewModel::class.java]

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initButton()

        initRecyclerView()

        registerForActivity {
            showImage(it)
        }

        return root
    }

    private fun initButton() {
        // 识别按钮
        binding.detectButton.setOnClickListener {
            detect()
        }
        TooltipCompat.setTooltipText(binding.detectButton, getText(R.string.detect_btn_hint))

        // 拷贝按钮
        binding.copyContentButton.setOnClickListener {
            copyContent()
        }
        TooltipCompat.setTooltipText(
            binding.copyContentButton,
            getText(R.string.copy_content_btn_hint)
        )

        // 选择按钮
        binding.pickImageButton.setOnClickListener {
            pickImageFromGallery()
        }
        TooltipCompat.setTooltipText(binding.pickImageButton, getText(R.string.pick_image_btn_hint))

        binding.takeImageButton.setOnClickListener {
            takeImage { uri ->
                showImage(uri)
            }
        }
    }


    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Unit?>

    /**
     * 请求权限成功时执行
     */
    private var requestReadPermissionSuccess: (() -> Unit)? = null

    /**
     * 拍摄图片成功时执行
     */
    private var takeImageSuccess: ((Uri) -> Unit)? = null

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
                    requestReadPermissionSuccess?.let { it() }
                } else {
                    // 用户拒绝了权限，可以给出相应提示
                    ToastUtils.showToast("权限获取失败")
                }
            }

        takePictureLauncher =
            registerForActivityResult(TakePhotoContract()) { uri ->
                // bitmap from camera
                uri ?: return@registerForActivityResult
                takeImageSuccess?.let { it(uri) }
            }
    }

    override fun onStart() {
        super.onStart()
        LogUtils.d(TAG, "onResume")
        if (BaseSettingUtils.getAutoLoadImage()) {
            requestStoragePermission {
                context?.getLatestImageUri()?.also {
                    showImage(it)
                }
            }
        }
    }

    private fun showImage(imageUri: Uri) {
        binding.detectImageView.visibility = View.VISIBLE
        Glide.with(this@GalleryFragment)
            .load(imageUri)
            .into(binding.detectImageView)
        savedImage.date = imageUri
        savedImage.imageType = ImageData.ImageType.URI
    }

    private fun showImage(image: Bitmap) {
        binding.detectImageView.visibility = View.VISIBLE
        Glide.with(this@GalleryFragment)
            .load(image)
            .into(binding.detectImageView)
        savedImage.date = image
        savedImage.imageType = ImageData.ImageType.BITMAP
    }

    /**
     * 获取读取权限，失败给与提示
     *
     * @param hasPermission 拥有权限或请求成功时执行
     */
    private fun requestStoragePermission(hasPermission: () -> Unit = {}) {
        val readStoragePermission: String = Manifest.permission.READ_EXTERNAL_STORAGE
        if (!requireContext().isAllGranted(readStoragePermission)) {
            this.requestReadPermissionSuccess = hasPermission
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
        requestStoragePermission {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }

            getImageLauncher.launch(intent)
        }

    }

    /**
     * 从相册中获得图片
     *
     * @param getImage 获得图片时执行
     */
    private fun takeImage(getImage: (Uri) -> Unit) {
        takeImageSuccess = getImage
        takePictureLauncher.launch(null)
    }

    // 识别图像
    private fun detect() {

        flow {
            savedImage.date ?: return@flow
            when (savedImage.imageType) {
                ImageData.ImageType.URI -> {
                    val bitmap = Glide.with(this@GalleryFragment)
                        .asBitmap()
                        .skipMemoryCache(true)
                        .load(savedImage.date as Uri)
                        .submit()
                        .get()

                    val ocrResult = OcrUtils.detect(bitmap)
                    emit(ocrResult)
                }
                ImageData.ImageType.BITMAP -> {
                    val ocrResult = OcrUtils.detect(savedImage.date as Bitmap)
                    emit(ocrResult)
                }
            }
        }.flowOn(Dispatchers.IO)
            .onStart {
                controlLoading(true)
                ToastUtils.showToast("开始识别")
            }.onEach {
                OcrUtils.recycleOcrResultBitmap(it)
                // 避免识别过程中切换页面导致的NullPointerException
                val bind = _binding ?: return@onEach
                bind.copyContentButton.visibility = View.VISIBLE
                bind.gl1.setGuidelinePercent(0.4f) // 用于显示recyclerView
                // 处理识别结果并显示
                flow {
                    // 避免识别过程中切换页面导致的NullPointerException
                    val viewModel = _galleryViewModel ?: return@flow
                    emit(viewModel.getDetectAdapterDateList(it))
                }.flowOn(Dispatchers.Default)
                    .onEach { array ->
                        controlLoading(false)
                        // 用于确保焦点在最开始
                        bind.detectResultRV.layoutManager?.apply {
                            scrollToPosition(0)
                            LogUtils.d(TAG, "scrollToPosition to zero")
                        }
                        (bind.detectResultRV.adapter as DetectResultAdapter).setDataList(array)
                    }.launchIn(lifecycleScope)

            }.catch {
                // 该流的所有异常都会在这里处理
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
            val selectContent = it.getSelectContent()
            if (selectContent.isNotEmpty()) {
                selectContent.copyToClipboard(requireContext(), TAG)
                ToastUtils.showToast("拷贝到系统剪切板")
            }
        }
    }

    private fun controlLoading(show: Boolean) {
        if (show) {
            binding.detectLoadingView.visibility = View.VISIBLE
            binding.detectLoadingView.reStartScan()
        } else {
            binding.detectLoadingView.stopScan()
            binding.detectLoadingView.visibility = View.INVISIBLE
        }

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
        binding.detectResultRV.adapter = DetectResultAdapter(emptyList())
    }

    override fun handleBackPressed(): Boolean {
        val adapter = binding.detectResultRV.adapter
        if (adapter != null && adapter is DetectResultAdapter && adapter.clearAllSelect())  {
            LogUtils.d(TAG, "handleBackPressed true")
            return true // 清理了选中状态
        }
        return super.handleBackPressed()
    }
}