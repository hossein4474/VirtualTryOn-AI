package com.virtualtryonai.app.data.api

import com.virtualtryonai.app.data.model.FaceDetectionRequest
import com.virtualtryonai.app.data.model.FaceDetectionResponse
import com.virtualtryonai.app.data.model.FaceParseRequest
import com.virtualtryonai.app.data.model.FaceParseResponse
import com.virtualtryonai.app.data.model.FaceSwapRequest
import com.virtualtryonai.app.data.model.ImageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InsightFaceAPIService {

    @POST("inference/face_detection")
    suspend fun detectFaces(
        @Body request: FaceDetectionRequest
    ): Response<FaceDetectionResponse>

    @POST("inference/face_swap")
    suspend fun swapFaces(
        @Body request: FaceSwapRequest
    ): Response<ImageResponse>

    @POST("inference/face_parse")
    suspend fun parseFace(
        @Body request: FaceParseRequest
    ): Response<FaceParseResponse>
}
