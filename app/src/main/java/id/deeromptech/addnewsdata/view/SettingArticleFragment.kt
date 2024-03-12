package id.deeromptech.addnewsdata.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.deeromptech.addnewsdata.adapter.ArticlesSettingListAdapter
import id.deeromptech.addnewsdata.databinding.FragmentSettingArticleBinding
import id.deeromptech.addnewsdata.utils.Constant
import id.deeromptech.addnewsdata.utils.Resource
import id.deeromptech.addnewsdata.utils.ToastUtils
import id.deeromptech.addnewsdata.viewmodel.AddingDataViewModel
import id.deeromptech.addnewsdata.viewmodel.ArticleViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlin.properties.Delegates

class SettingArticleFragment : Fragment() {

    private var _binding: FragmentSettingArticleBinding? = null
    private val binding get() = _binding
    private var mAdapterNewArticle by Delegates.notNull<ArticlesSettingListAdapter>()
    private val viewModel: ArticleViewModel by viewModels()
    private val viewModel2: AddingDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingArticleBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getLiveData()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() = with(binding) {
        this!!.lineToolbar.visibility = View.GONE

        mAdapterNewArticle = ArticlesSettingListAdapter(requireActivity())

        this!!.layoutNews.rvNews.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = mAdapterNewArticle
        }

        viewModel.getAllArticle()
        this!!.swipeRefresh.setOnRefreshListener {
            viewModel.getAllArticle()
        }

        this?.BtnAddData?.setOnClickListener {
            val action = SettingArticleFragmentDirections.actionSettingArticleFragmentToAddArticleFragment()
            findNavController().navigate(action)
        }

        mAdapterNewArticle.onEditClick = {
            val article = it
            val action =
                SettingArticleFragmentDirections.actionSettingArticleFragmentToAddArticleFragment(
                    isEdit = true,
                    news = article
                )
            findNavController().navigate(action)
        }

        mAdapterNewArticle.onDeleteClick = {
            val alert = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage("kamu yakin ingin menghapusnya?")
                .setPositiveButton("Ya") { _, _ ->
                    viewModel2.deleteArticle(it.id.toString())
                }.setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
            alert.setCancelable(false)
            alert.create()
            alert.show()
        }
    }

    private fun getLiveData() = with(binding) {
        viewModel.apply {
            lifecycleScope.launchWhenStarted {
                newArticles.collectLatest {
                    when(it){
                        is Resource.Loading -> {
                            this@with?.swipeRefresh?.isRefreshing = true
                        }

                        is Resource.Success -> {
                            this@with?.swipeRefresh?.isRefreshing = false
                            Constant.handleData(
                                null, false, it.data, mAdapterNewArticle,
                                this@with?.layoutNews?.rvNews, this@with?.layoutNews?.messageNotArticle
                            )

                            mAdapterNewArticle.submitList(it.data)
                        }

                        is Resource.Error -> {
                            ToastUtils.showMessage(requireContext(), it.message.toString() )
                            this@with?.swipeRefresh?.isRefreshing = false
                        }
                        else -> Unit
                    }
                }
            }
            error.observe(requireActivity()) {
                Constant.handleErrorApi(requireActivity(), it)
            }
            loading.observe(requireActivity()) {
                this@with?.swipeRefresh?.isRefreshing = it
            }
        }
        viewModel2.apply {
            isDeletePost.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.getAllArticle()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}