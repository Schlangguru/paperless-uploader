package de.schlangguru.paperlessuploader.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.shockwave.pdfium.PdfiumCore
import de.schlangguru.paperlessuploader.ContextHolder
import java.io.File


fun imgFromPDF(
        pdfUri: Uri,
        context: Context = ContextHolder.appContext
): Bitmap {
    val fd = context.contentResolver.openFileDescriptor(pdfUri, "r")
    val pageNum = 0
    val pdfiumCore = PdfiumCore(context)

    val pdfDocument = pdfiumCore.newDocument(fd)
    pdfiumCore.openPage(pdfDocument, pageNum)

    val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum)
    val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum)

    // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
    // RGB_565 - little worse quality, twice less memory usage
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0, width, height)
    pdfiumCore.closeDocument(pdfDocument)

    return bitmap
}

fun filenameFromUri(
        uri: Uri,
        context: Context = ContextHolder.appContext
): String {
    val file = File(uri.path)
    return file.nameWithoutExtension
}

fun removeSpecialChars(str: String): String {
    var replaced = str
    replaced = replaced.replace(Regex("""[^\w\d\s\,\.]"""), "")
    replaced = replaced.replace(" - ", "-")

    return replaced
}