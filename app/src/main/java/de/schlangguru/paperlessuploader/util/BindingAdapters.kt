package de.schlangguru.paperlessuploader.util

import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import de.schlangguru.paperlessuploader.ContextHolder

/**
 * Binding Adapter to generate a bitmap based on the Uri of a PDF file.
 */
@BindingAdapter("bind:pdf_thumbnail_uri")
fun loadBitmapFromPDFintoImageView(iv: ImageView, uri: Uri) {
    val bitmap = imgFromPDF(uri, ContextHolder.appContext)
    iv.setImageBitmap(bitmap)
}