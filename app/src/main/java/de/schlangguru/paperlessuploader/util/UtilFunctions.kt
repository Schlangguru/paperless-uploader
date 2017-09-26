package de.schlangguru.paperlessuploader.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.shockwave.pdfium.PdfiumCore
import de.schlangguru.paperlessuploader.ContextHolder
import java.io.File

/**
 * Extracts an Bitmap image from the given pdf.
 *
 * @param pdfUri The uri of the pdf
 * @param context The application context
 *
 * @return An bitmap of the provided pdf.
 */
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

/**
 * Returns the real filename of a file.
 *
 * @param uri The Uri of the file.
 * @param context The application context.
 *
 * @return The filename of the file.
 */
fun filenameFromUri(
        uri: Uri,
        context: Context = ContextHolder.appContext
): String {
    val file = File(uri.path)
    return file.nameWithoutExtension
}

/**
 * Removes all special chars from a the input [str]
 * and returns a new string.
 *
 * The returned string will only contain word-characters, numbers and whitespace.
 * This method is used to format the filenames of documents since paperless
 * does not accept special chars in uploaded documents.
 */
fun removeSpecialChars(str: String): String {
    var replaced = str
    replaced = replaced.replace(Regex("""[^\w\d\s\,\.]"""), "")
    replaced = replaced.replace(" - ", "-")

    return replaced
}