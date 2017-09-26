package de.schlangguru.paperlessuploader

import android.content.Context

/**
 * Singleton.
 * Single point of access for the application context.
 */
object ContextHolder {

    /** Will be initialized when the app starts. */
    lateinit var appContext: Context
}