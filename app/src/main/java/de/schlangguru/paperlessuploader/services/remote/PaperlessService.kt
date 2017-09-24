package de.schlangguru.paperlessuploader.services.remote

import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface PaperlessService {

    @GET("/api")
    fun connect(): Single<ResponseBody>

    @GET("/api/documents")
    fun documents() : Single<JsonObject>

    @GET("/api/correspondents")
    fun correspondents() : Single<JsonObject>

    @POST("/push")
    fun pushDocument(@Body requestBody: RequestBody) : Single<ResponseBody>

}