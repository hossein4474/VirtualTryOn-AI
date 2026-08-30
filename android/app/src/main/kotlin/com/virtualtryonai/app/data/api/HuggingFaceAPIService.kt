package com.virtualtryonai.app.data.api

import com.virtualtryonai.app.data.model.FaceDetectionRequest
import com.virtualtryonai.app.data.model.FaceDetectionResponse
import com.virtualtryonai.app.data.model.FaceRecognitionRequest
import com.virtualtryonai.app.data.model.FaceRecognitionResponse
import com.virtualtryonai.app.data.model.PoseDetectionRequest
import com.virtualtryonai.app.data.model.PoseDetectionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface HuggingFaceAPIService {

    @POST("models/facebook/detr-resnet50-panoptic")
    suspend fun poseDetection(
        @Body request: PoseDetectionRequest,
        @Header("Authorization") auth: String
    ): Response<PoseDetectionResponse>

    @POST("models/dlib-community/dlib_face_recognition_resnet_model_v1")
    suspend fun faceRecognition(
        @Body request: FaceRecognitionRequest,
        @Header("Authorization") auth: String
    ): Response<FaceRecognitionResponse>

    @POST("models/mediapipe/face_detector")
    suspend fun faceDetection(
        @Body request: FaceDetectionRequest,
        @Header("Authorization") auth: String
    ): Response<FaceDetectionResponse>
}
