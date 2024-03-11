package id.deeromptech.addnewsdata.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.deeromptech.addnewsdata.model.News
import id.deeromptech.addnewsdata.firebase.FirebaseDb
import id.deeromptech.addnewsdata.utils.Resource
import kotlinx.coroutines.launch

class AddingDataViewModel : ViewModel() {

    private val firebaseDatabase: FirebaseDb by lazy { FirebaseDb() }

    val uploadArticleImage = MutableLiveData<Resource<String>>()
    val updateArticle = MutableLiveData<Resource<News>>()

    private val _isSuccessPost = MutableLiveData<Boolean>()
    val isSuccessPost: LiveData<Boolean> = _isSuccessPost

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isDeletePost = MutableLiveData<Boolean>()
    val isDeletePost: LiveData<Boolean> = _isDeletePost

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _successUpgrade = MutableLiveData<Boolean>()
    val successUpgrade: LiveData<Boolean> = _successUpgrade

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    fun uploadAndPostArticleImage(
        image: ByteArray,
        idArticle: String,
        titleArticle: String,
        content: String,
    ) {
        _loading.postValue(true)

        firebaseDatabase.uploadArticleWithImage(
            image, idArticle, titleArticle, content,
        ) { forum, error ->
            _loading.postValue(false)

            if (error == null) {
                _isSuccessPost.postValue(true)

            } else {
                _isSuccessPost.postValue(false)
                _error.postValue(Throwable("Failed to delete Article: ${error.toString()}"))
            }
        }
    }

    fun editPostArticleImage(
        image: ByteArray,
        idArticle: String,
        titleArticle: String,
        content: String,
    ) {
        _loading.postValue(true)

        firebaseDatabase.editArticleWithImage(
            image, idArticle, titleArticle, content,
        ) { forum, error ->
            _loading.postValue(false)

            if (error == null) {
                _isSuccessPost.postValue(true)

            } else {
                _isSuccessPost.postValue(false)
                _error.postValue(Throwable("Failed to delete Article: ${error.toString()}"))
            }
        }
    }

    fun uploadAndPostArticle(
        idArticle: String,
        titleArticle: String,
        content: String,
    ) {
        _loading.postValue(true)

        firebaseDatabase.uploadArticle(
            idArticle, titleArticle, content,
        ) { forum, error ->
            _loading.postValue(false)

            if (error == null) {
                _isSuccessPost.postValue(true)

            } else {
                _isSuccessPost.postValue(false)
                _error.postValue(Throwable("Failed to delete Article: ${error.toString()}"))
            }
        }
    }

    fun editPostArticle(
        idArticle: String,
        titleArticle: String,
        content: String,
    ) {
        _loading.postValue(true)

        firebaseDatabase.editArticle(
            idArticle, titleArticle, content,
        ) { forum, error ->
            _loading.postValue(false)

            if (error == null) {
                _isSuccessPost.postValue(true)

            } else {
                _isSuccessPost.postValue(false)
                _error.postValue(Throwable("Failed to edit Article: ${error.toString()}"))
            }
        }
    }

    fun deleteArticle(id: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                firebaseDatabase.deleteArticleById(id).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _isDeletePost.postValue(true)
                    } else {
                        _isDeletePost.postValue(false)
                        _error.postValue(Throwable("Failed to delete Article: ${task.exception?.message}"))
                    }
                }
            } catch (e: Exception) {
                errorHandle(e)
            }
        }
    }

    private fun errorHandle(it: Throwable) {
        _isSuccess.postValue(false)
        _successUpgrade.postValue(false)
        _loading.postValue(false)
        _error.postValue(it)
    }
}