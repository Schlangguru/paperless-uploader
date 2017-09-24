package de.schlangguru.paperlessuploader.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.ObservableField
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import de.schlangguru.paperlessuploader.R

class Document(
        name: String = "",
        uri: Uri = Uri.EMPTY,
        correspondent: String = "",
        status: DocumentStatus = DocumentStatus.QUEUED
) {
    val name = ObservableField<String>()
    val uri = ObservableField<Uri>()
    val correspondent = ObservableField<String>()
    val status = ObservableField<DocumentStatus>()
    val backgroundColor = ObservableField<Int>()

    init {
        this.status.addOnPropertyChangedCallback(ChangeBackgroundColorCallback())

        this.name.set(name)
        this.uri.set(uri)
        this.correspondent.set(correspondent)
        this.status.set(status)
    }

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

