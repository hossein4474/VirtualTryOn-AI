package com.virtualtryonai.app.data.api

import com.virtualtryonai.app.data.model.ClothingTransferRequest
import com.virtualtryonai.app.data.model.ImageResponse
import com.virtualtryonai.app.data.model.PoseTransferRequest
import com.virtualtryonai.app.data.model.StyleTransferRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RunwayAPIService {

    @POST("image_to_image")
    suspend fun clothingTransfer(
        @Body request: ClothingTransferRequest
    ): Response<ImageResponse>

    @POST("pose_transfer")
    suspend fun transferPose(
        @Body request: PoseTransferRequest
    ): Response<ImageResponse>

    @POST("style_transfer")
    suspend fun styleTransfer(
        @Body request: StyleTransferRequest
    ): Response<ImageResponse>

    @POST("inpainting")
    suspend fun inpaint(
        @Body request: Map<String, String>
    ): Response<ImageResponse>
}
