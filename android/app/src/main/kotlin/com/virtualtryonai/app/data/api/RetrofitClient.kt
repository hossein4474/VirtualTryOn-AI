package com.virtualtryonai.app.data.api

import com.virtualtryonai.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private fun createOkHttpClient(apiKey: String? = null): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)

        if (!apiKey.isNullOrEmpty()) {
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
        }

        return builder.build()
    }

    val runwayAPI: RunwayAPIService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.runwayml.com/v1/")
            .client(createOkHttpClient(BuildConfig.RUNWAY_API_KEY))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RunwayAPIService::class.java)
    }

    val huggingFaceAPI: HuggingFaceAPIService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-inference.huggingface.co/")
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceAPIService::class.java)
    }

    val removeBgAPI: RemoveBgAPIService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.remove.bg/")
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoveBgAPIService::class.java)
    }

    val insightFaceAPI: InsightFaceAPIService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.insightface.ai/")
            .client(createOkHttpClient(BuildConfig.INSIGHTFACE_API_KEY))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InsightFaceAPIService::class.java)
    }
}
