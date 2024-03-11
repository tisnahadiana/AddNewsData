package id.deeromptech.addnewsdata.firebase

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.deeromptech.addnewsdata.model.News
import java.util.UUID

class FirebaseDb {

    private val articleCollectionRef = Firebase.firestore.collection("News")

    private val firebaseAuth = Firebase.auth
    private val firebaseStorage = Firebase.storage.reference

    fun uploadArticleWithImage(
        image: ByteArray,
        idArticle: String,
        titleArticle: String,
        content: String,
        onResult: (News?, String?) -> Unit,
    ) {
        val imageRef = firebaseStorage.child("articleImages")
            .child(UUID.randomUUID().toString())

        imageRef.putBytes(image)
            .addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    imageRef.downloadUrl.addOnCompleteListener { urlTask ->
                        if (urlTask.isSuccessful) {
                            val imageUrl = urlTask.result.toString()
                            val article = News(
                                idArticle,
                                titleArticle,
                                content,
                                imageUrl,
                            )

                            Firebase.firestore.runTransaction { transaction ->
                                val userPath = articleCollectionRef.document(idArticle)
                                transaction.set(userPath, article)
                            }.addOnCompleteListener { firestoreTask ->
                                if (firestoreTask.isSuccessful) {
                                    onResult(article, null)
                                } else {
                                    onResult(null, firestoreTask.exception.toString())
                                }
                            }
                        } else {
                            onResult(null, urlTask.exception.toString())
                        }
                    }
                } else {
                    onResult(null, uploadTask.exception.toString())
                }
            }
    }

    fun editArticleWithImage(
        image: ByteArray,
        idArticle: String,
        titleArticle: String,
        content: String,
        onResult: (News?, String?) -> Unit
    ) {
        val articleRef = articleCollectionRef.document(idArticle)

        if (image != null) {
            val newImageRef = firebaseStorage.child("articleImages")
                .child(UUID.randomUUID().toString())

            newImageRef.putBytes(image)
                .addOnCompleteListener { uploadTask ->
                    if (uploadTask.isSuccessful) {
                        newImageRef.downloadUrl.addOnCompleteListener { urlTask ->
                            if (urlTask.isSuccessful) {
                                val newImageUrl = urlTask.result.toString()

                                articleRef.update("image", newImageUrl)
                            } else {
                                onResult(null, urlTask.exception.toString())
                                return@addOnCompleteListener
                            }
                        }
                    } else {
                        onResult(null, uploadTask.exception.toString())
                        return@addOnCompleteListener
                    }
                }
        }

        articleRef.update(
            "title", titleArticle,
            "content", content,
        )
            .addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful) {
                    articleRef.get()
                        .addOnSuccessListener { documentSnapshot ->
                            val updatedArticle = documentSnapshot.toObject(News::class.java)
                            onResult(updatedArticle, null)
                        }
                        .addOnFailureListener { exception ->
                            onResult(null, exception.toString())
                        }
                } else {
                    onResult(null, updateTask.exception.toString())
                }
            }
    }


    fun uploadArticle(
        idArticle: String,
        titleArticle: String,
        content: String,
        onResult: (News?, String?) -> Unit,
    ) {
        val article = News(
            idArticle,
            titleArticle,
            content,
            "",
        )

        Firebase.firestore.runTransaction { transaction ->
            val userPath = articleCollectionRef.document(idArticle)
            transaction.set(userPath, article)
        }.addOnCompleteListener { firestoreTask ->
            if (firestoreTask.isSuccessful) {
                onResult(article, null)
            } else {
                onResult(null, firestoreTask.exception.toString())
            }
        }
    }

    fun editArticle(
        idArticle: String,
        titleArticle: String,
        content: String,
        onResult: (News?, String?) -> Unit
    ) {
        val articleRef = articleCollectionRef.document(idArticle)

        articleRef.update(
            "title", titleArticle,
            "content", content,
        ).addOnCompleteListener { updateTask ->
            if (updateTask.isSuccessful) {
                articleRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        val updatedArticle = documentSnapshot.toObject(News::class.java)
                        onResult(updatedArticle, null)
                    }
                    .addOnFailureListener { exception ->
                        onResult(null, exception.toString())
                    }
            } else {
                onResult(null, updateTask.exception.toString())
            }
        }
    }

    fun deleteArticleById(id: String): Task<Void> =
        articleCollectionRef.whereEqualTo("id", id)
            .get()
            .continueWithTask { querySnapshot ->
                val tasks: MutableList<Task<Void>> = mutableListOf()

                for (document in querySnapshot.result!!) {
                    tasks.add(articleCollectionRef.document(document.id).delete())
                }

                Tasks.whenAll(tasks)
            }

    fun getArticlesPages() = articleCollectionRef

    fun getNewArticles() = articleCollectionRef.orderBy("date", Query.Direction.ASCENDING)
}