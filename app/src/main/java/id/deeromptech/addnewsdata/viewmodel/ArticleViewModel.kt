package id.deeromptech.addnewsdata.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.deeromptech.addnewsdata.model.News
import id.deeromptech.addnewsdata.firebase.FirebaseDb
import id.deeromptech.addnewsdata.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArticleViewModel : ViewModel() {

    private val firebaseDatabase: FirebaseDb by lazy { FirebaseDb() }

    private val _newArticles = MutableStateFlow<Resource<List<News>>>(Resource.Unspecified())
    val newArticles = _newArticles.asStateFlow()

    private val _recommendArticles = MutableStateFlow<Resource<List<News>>>(Resource.Unspecified())
    val recommendArticles = _recommendArticles.asStateFlow()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    fun getAllArticle() {
        viewModelScope.launch {
            _recommendArticles.emit(Resource.Loading())
        }
        firebaseDatabase.getArticlesPages().get()
            .addOnSuccessListener {
                val data = it.toObjects(News::class.java)
                viewModelScope.launch {
                    _recommendArticles.emit(Resource.Success(data))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _recommendArticles.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun getNewsArticle() {
        viewModelScope.launch {
            _newArticles.emit(Resource.Loading())
        }
        firebaseDatabase.getNewArticles().get()
            .addOnSuccessListener {
                val data = it.toObjects(News::class.java)
                viewModelScope.launch {
                    _newArticles.emit(Resource.Success(data))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _newArticles.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun errorHandle(it: Throwable) {
        _loading.postValue(false)
        _error.postValue(it)
    }
}