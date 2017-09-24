package de.schlangguru.paperlessuploader

import android.app.Application

/**
 * Created by sebastian on 19.09.17.
 */
class PaperlessUploaderApp: Application() {
    override fun onCreate() {
        ContextHolder.appContext = applicationContext
    }
}