package de.schlangguru.paperlessuploader.ui.main

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.google.gson.JsonObject
import de.schlangguru.paperlessuploader.BR
import de.schlangguru.paperlessuploader.R
import de.schlangguru.paperlessuploader.util.filenameFromUri
import de.schlangguru.paperlessuploader.model.Document
import de.schlangguru.paperlessuploader.model.DocumentStatus
import de.schlangguru.paperlessuploader.util.removeSpecialChars
import de.schlangguru.paperlessuploader.services.remote.PaperlessService
import de.schlangguru.paperlessuploader.services.remote.PaperlessServiceProvider
import de.schlangguru.paperlessuploader.util.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.tatarka.bindingcollectionadapter2.ItemBinding
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.net.SocketTimeoutException


class MainViewModel(
        private val paperless: PaperlessService = PaperlessServiceProvider.createService()
) : ViewModel() {

    private val LOG_TAG: String = MainViewModel::class.java.simpleName

    val uploadQueue = ObservableArrayList<Document>()
    val uploadQueueItemBinding: ItemBinding<Document> = ItemBinding.of(BR.document, R.layout.document_item)
    val connecting = ObservableBoolean(true)
    val isConnected = ObservableBoolean(false)
    val serverDocCount = ObservableInt(0)
    val correspondents = ObservableArrayList<String>()
    val showCorrespondentsEvent = SingleLiveEvent<Document>()

    init {
        uploadQueueItemBinding.bindExtra(BR.vm, this)
    }

    fun connectPaperless() {
        connecting.set(true)
        paperless.connect()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { response ->
                            connecting.set(false)
                            isConnected.set(true)
                            response.close()

                            loadCorrespondents()
                            loadDocuments()
                        },
                        onError = {
                            connecting.set(false)
                            isConnected.set(false)
                            val msg = "Error while loading the documents"
                            Log.e(LOG_TAG, msg, it)
                        }
                )

    }

    fun uploadQueue() {
        for (document in uploadQueue) {
            document.status.set(DocumentStatus.UPLOADING)
            val file = File(document.uri.get().path)
            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("correspondent", document.correspondent.get())
                    .addFormDataPart("title", document.name.get())
                    .addFormDataPart("document", document.name.get(), RequestBody.create(MediaType.parse("application/pdf"), file))
                    .build()

            paperless.pushDocument(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = { response ->
                                document.status.set(DocumentStatus.UPLOAD_SUCCESS)
                                response.close()
                                Handler().postDelayed({
                                    uploadQueue.remove(document)
                                    serverDocCount.set(serverDocCount.get() + 1)
                                }, 1000)
                            },
                            onError = {
                                document.status.set(DocumentStatus.UPLOAD_ERROR)
                                val msg = when(it) {
                                    is HttpException -> it.response().message()
                                    is SocketTimeoutException -> "Connection Timeout"
                                    else -> "Error while uploading the document"
                                }
                                // TODO better error message
                                Log.e(LOG_TAG, msg, it)
                            }
                    )
        }
    }

    fun enqueueDocument(uri: Uri) {
        val name = removeSpecialChars(filenameFromUri(uri))
        val doc = Document(name = name,  uri = uri, status = DocumentStatus.QUEUED)
        uploadQueue.add(doc)
    }

    fun onDocumentClick(doc: Document) {
        showCorrespondentsEvent.value = doc
    }

    private fun loadDocuments() {
        paperless.documents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { json ->
                            serverDocCount.set(json.get("count").asInt)
                        },
                        onError = {
                            val msg = "Error while loading the documents"
                            Log.e(LOG_TAG, msg, it)
                        }
                )
    }

    private fun loadCorrespondents() {
        paperless.correspondents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { json ->
                            createCorrespondentList(json)
                        },
                        onError = {
                            val msg = "Error while loading the corresponents"
                            Log.e(LOG_TAG, msg, it)
                        }
                )
    }

    private fun createCorrespondentList(json: JsonObject) {
        correspondents.clear()
        val results = json.get("results").asJsonArray
        results.forEach { element ->
            val correspondent = element.asJsonObject.get("name").asString
            correspondents.add(correspondent)
        }
    }

}