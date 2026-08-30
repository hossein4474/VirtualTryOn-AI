package com.virtualtryonai.app.di

import com.virtualtryonai.app.data.api.HuggingFaceAPIService
import com.virtualtryonai.app.data.api.InsightFaceAPIService
import com.virtualtryonai.app.data.api.RemoveBgAPIService
import com.virtualtryonai.app.data.api.RetrofitClient
import com.virtualtryonai.app.data.api.RunwayAPIService
import com.virtualtryonai.app.data.repository.TryOnRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRunwayAPIService(): RunwayAPIService {
        return RetrofitClient.runwayAPI
    }

    @Singleton
    @Provides
    fun provideHuggingFaceAPIService(): HuggingFaceAPIService {
        return RetrofitClient.huggingFaceAPI
    }

    @Singleton
    @Provides
    fun provideRemoveBgAPIService(): RemoveBgAPIService {
        return RetrofitClient.removeBgAPI
    }

    @Singleton
    @Provides
    fun provideInsightFaceAPIService(): InsightFaceAPIService {
        return RetrofitClient.insightFaceAPI
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideTryOnRepository(
        runwayAPI: RunwayAPIService,
        huggingFaceAPI: HuggingFaceAPIService,
        removeBgAPI: RemoveBgAPIService,
        insightFaceAPI: InsightFaceAPIService
    ): TryOnRepository {
        return TryOnRepository(
            runwayAPI = runwayAPI,
            huggingFaceAPI = huggingFaceAPI,
            removeBgAPI = removeBgAPI,
            insightFaceAPI = insightFaceAPI
        )
    }
}
