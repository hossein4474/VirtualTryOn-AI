package com.virtualtryonai.app.data.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RemoveBgAPIService {

    @Multipart
    @POST("v1.0/removebg")
    suspend fun removeBackground(
        @Part("image_file") image: MultipartBody.Part,
        @Part("size") size: String = "auto",
        @Part("type") type: String = "auto",
        @Part("format") format: String = "auto",
        @Header("X-API-Key") apiKey: String
    ): Response<ResponseBody>
}
