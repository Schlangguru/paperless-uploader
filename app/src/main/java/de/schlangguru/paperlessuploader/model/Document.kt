package de.schlangguru.paperlessuploader.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.ObservableField
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import de.schlangguru.paperlessuploader.R

/**
 * Model Class for a Document.
 */
class Document(
        name: String = "",
        uri: Uri = Uri.EMPTY,
        correspondent: String = "",
        status: DocumentStatus = DocumentStatus.QUEUED
) {
    /** The name of the document. */
    val name = ObservableField<String>()
    /** The uri that points to the actual file. */
    val uri = ObservableField<Uri>()
    /** The correspondent of the document. */
    val correspondent = ObservableField<String>()
    /** The status of the document within the upload queue. */
    val status = ObservableField<DocumentStatus>()
    /** The background color reflecting the status. Used to visualize the status. */
    val backgroundColor = ObservableField<Int>()

    init {
        this.status.addOnPropertyChangedCallback(ChangeBackgroundColorCallback())

        this.name.set(name)
        this.uri.set(uri)
        this.correspondent.set(correspondent)
        this.status.set(status)
    }

    /**
     * Automatically changes the background color
     * as soon as the status changes.
     */
    private inner class ChangeBackgroundColorCallback : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            val color =  when (status.get()) {
                DocumentStatus.UPLOAD_SUCCESS -> R.color.document_status_success
                DocumentStatus.UPLOAD_ERROR -> R.color.document_status_error
                else -> R.color.document_status_neutral
            }

            backgroundColor.set(color)
        }
    }
}

