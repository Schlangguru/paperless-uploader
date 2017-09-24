package de.schlangguru.paperlessuploader.services.remote

import de.schlangguru.paperlessuploader.BuildConfig
import de.schlangguru.paperlessuploader.services.local.Settings
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PaperlessServiceProvider {
    companion object {
        fun createService (
                settings: Settings = Settings(),
                baseURL: String = settings.apiURL
        ) : PaperlessService {

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

            val client = OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(BasicAuthInterceptor(settings))
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(baseURL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(PaperlessService::class.java)
        }
    }

    class BasicAuthInterceptor(
            private val settings: Settings
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val credentials = Credentials.basic(settings.username, settings.password)
            val authRequest = chain.request().newBuilder().addHeader("Authorization", credentials).build()

            return chain.proceed(authRequest)
        }
    }
}