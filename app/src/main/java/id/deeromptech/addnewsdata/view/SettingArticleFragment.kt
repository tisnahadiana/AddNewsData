package id.deeromptech.addnewsdata.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import id.deeromptech.addnewsdata.adapter.ArticlesSettingListAdapter
import id.deeromptech.addnewsdata.databinding.FragmentSettingArticleBinding
import id.deeromptech.addnewsdata.viewmodel.AddingDataViewModel
import id.deeromptech.addnewsdata.viewmodel.ArticleViewModel
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}