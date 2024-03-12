package id.deeromptech.addnewsdata.view

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ForwardScope
import id.deeromptech.addnewsdata.GlideApp
import id.deeromptech.addnewsdata.R
import id.deeromptech.addnewsdata.databinding.FragmentAddArticleBinding
import id.deeromptech.addnewsdata.model.News
import id.deeromptech.addnewsdata.utils.Constant
import id.deeromptech.addnewsdata.utils.Resource
import id.deeromptech.addnewsdata.viewmodel.AddingDataViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class AddArticleFragment : Fragment() {

    private var _binding: FragmentAddArticleBinding? = null
    private val binding get() = _binding
    private val viewModel: AddingDataViewModel by viewModels()
    val args by navArgs<AddArticleFragmentArgs>()
    private var mIdArticle: String? = null
    private var mTitle: String? = null
    private var mContent: String? = null
    private var request: RequestBody? = null
    private var imageBody: MultipartBody.Part? = null
    private var imageUri: Uri? = null
    private var isPicked = false
    private var imageByteArray: ByteArray? = null
    private var isEditPost: Boolean? = false
    private var postBody: News? = null
    val TAG = "EditUserInformation"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddArticleBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isEditPost = args.isEdit
        postBody = args.news
        initView()
        getLiveData()
    }

    private fun initView() = with(binding) {

        this?.btnAddPhoto?.setOnClickListener {
            changePhotoArticle()
        }

        if (isEditPost == true) {
            getPostData()
        }

        this?.buttonSave?.setOnClickListener {
            getInputData()

            val isEmptyTitle = Constant.isTextEmpty(mTitle)
            val isEmptyContent = Constant.isTextEmpty(mContent)

            if ( !isEmptyTitle && !isEmptyContent
            ) {
                when (isEditPost) {
                    true -> {
                        if (isPicked){
                            viewModel.editPostArticleImage(
                                imageByteArray!!,
                                postBody?.id.toString(),
                                mTitle.toString(),
                                mContent.toString()
                            )
                        }
                        else {
                            viewModel.editPostArticle(
                                postBody?.id.toString(),
                                mTitle.toString(),
                                mContent.toString(),
                            )
                        }
                    }

                    else -> {
                        if (isPicked){
                            viewModel.uploadAndPostArticleImage(
                                imageByteArray!!,
                                mIdArticle.toString(),
                                mTitle.toString(),
                                mContent.toString()
                            )
                        }
                        else {
                            viewModel.uploadAndPostArticle(
                                mIdArticle.toString(),
                                mTitle.toString(),
                                mContent.toString()
                            )
                        }
                    }
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    "Data tidak boleh ada yang kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun getPostData() {
        if (isEditPost == true) {
            binding?.inputNamaArtikel?.setText(postBody?.title ?: "-")
            binding?.inputContent?.setText(postBody?.content ?: "-")
            GlideApp.with(this)
                .load(postBody?.image ?: "-")
                .placeholder(R.drawable.image_empty)
                .into(binding?.imageThumbnail!!)
            binding?.imageThumbnail?.visibility = View.VISIBLE
        }
    }

    private fun getInputData() = with(binding) {
        mIdArticle = UUID.randomUUID().toString()
        mTitle = this?.inputNamaArtikel?.text.toString()
        mContent = this?.inputContent?.text.toString()
    }

    private fun changePhotoArticle() {
        val permissionList = arrayListOf<String>()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionList.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissionList.add(Manifest.permission.CAMERA)
        }

        PermissionX.init(this)
            .permissions(permissionList)
            .explainReasonBeforeRequest()
            .onForwardToSettings { scope: ForwardScope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    getString(R.string.message_permission),
                    "OK",
                    "Batal"
                )
            }
            .request { allGranted: Boolean, _: List<String?>?, _: List<String?>? ->
                if (allGranted) {
                    cropImage.launch(
                        options {
                            setImageSource(includeGallery = true, includeCamera = true)
                            setAspectRatio(2, 1)
                            setGuidelines(CropImageView.Guidelines.ON)
                            setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                            setRequestedSize(1000, 1000)
                        }
                    )
                }
            }
    }


    private fun getLiveData() = with(binding) {
        viewModel.apply {
            uploadArticleImage.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {
                        showLoading()
                        return@observe
                    }

                    is Resource.Success -> {
                        hideLoading()
                        return@observe
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(
                            activity,
                            resources.getText(R.string.error_occurred),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, response.message.toString())
                        return@observe
                    }

                    else -> Unit
                }
            }
            isSuccessPost.observe(requireActivity()) {
                findNavController().navigateUp()
                Toast.makeText(
                    requireContext(),
                    "Posting Artikel Berhasil",
                    Toast.LENGTH_SHORT
                ).show()
            }
            isSuccess.observe(viewLifecycleOwner) {
                findNavController().navigateUp()
            }
            updateArticle.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {
                        showLoading()
                        return@observe
                    }

                    is Resource.Success -> {
                        hideLoading()
                        findNavController().navigateUp()
                        viewModel.updateArticle.postValue(null)
                        viewModel.uploadArticleImage.postValue(null)
                        return@observe
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(
                            activity,
                            resources.getText(R.string.error_occurred),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, response.message.toString())
                        return@observe
                    }

                    else -> Unit
                }
            }
            error.observe(viewLifecycleOwner) {
                it?.let { Constant.handleErrorApi(requireActivity(), it) }
            }
            loading.observe(viewLifecycleOwner) {
                if (it) {
                    this@with?.shadow?.visibility = View.VISIBLE
                    this@with?.progressBar?.visibility = View.VISIBLE
                } else {
                    this@with?.shadow?.visibility = View.GONE
                    this@with?.progressBar?.visibility = View.GONE
                }
            }
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            isPicked = true
            imageUri = result.uriContent
            imageBody = saveImageBody(imageUri?.let { getFile(it) })

            imageUri = result.uriContent
            val imageStream = requireContext().contentResolver.openInputStream(imageUri!!)
            imageByteArray = imageStream?.readBytes()

            binding?.imageThumbnail?.let {
                GlideApp.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.profile_placeholder)
                    .into(it)
            }

        } else {
            // an error occurred
            val exception = result.error
            Toast.makeText(
                requireContext(),
                exception?.localizedMessage.toString(),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            //Log.e("Save File", ex.message.toString())
            //ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun getFile(uri: Uri): File {
        val destinationFilename = File(
            requireActivity().filesDir.path + File.separatorChar + Constant.getFileName(
                requireContext(),
                uri
            )
        )
        try {
            requireActivity().contentResolver.openInputStream(uri).use { ins ->
                ins?.let {
                    createFileFromStream(
                        it,
                        destinationFilename
                    )
                }
            }
        } catch (ex: Exception) {
            //Log.e("Save File", ex.message.toString())
            //ex.printStackTrace()
        }
        return destinationFilename
    }

    private fun saveImageBody(file: File?): MultipartBody.Part? {
        request = file?.asRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        return request?.let {
            MultipartBody.Part.createFormData(
                "photo", file?.name.toString(),
                it
            )
        }
    }

    private fun setRequestBody(body: String): RequestBody {
        return body.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun showLoading() {
        binding.apply {
            this!!.progressBar.visibility = View.VISIBLE
            this!!.buttonSave.visibility = View.INVISIBLE
        }
    }

    private fun hideLoading() {
        binding.apply {
            this!!.progressBar.visibility = View.GONE
            this!!.buttonSave.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}