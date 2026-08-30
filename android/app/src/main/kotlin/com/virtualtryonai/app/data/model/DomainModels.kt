package com.virtualtryonai.app.data.model

import android.graphics.Bitmap

data class ClothingStyle(
    val id: String,
    val name: String,
    val category: String,
    val imageUrl: String,
    val description: String
)

data class PoseType(
    val id: String,
    val name: String,
    val imageUrl: String,
    val keypoints: List<Keypoint>? = null
)

data class Keypoint(
    val x: Float,
    val y: Float,
    val confidence: Float,
    val name: String
)

data class PoseData(
    val keypoints: List<Keypoint>,
    val confidence: Float,
    val pose: PoseType? = null
)

enum class ClothingVisibility {
    FULLY_CLOTHED,
    PARTIALLY_CLOTHED,
    NUDE
}

data class TryOnRequest(
    val originalImage: Bitmap,
    val clothingStyle: ClothingStyle,
    val clothingColor: String,
    val targetPose: PoseType? = null,
    val clothingVisibility: ClothingVisibility = ClothingVisibility.FULLY_CLOTHED
)

data class TryOnResult(
    val originalImage: Bitmap,
    val processedImage: Bitmap,
    val poseData: PoseData? = null,
    val processingTimeMs: Long
)

sealed class TryOnState {
    object Idle : TryOnState()
    object Loading : TryOnState()
    data class Success(val result: TryOnResult) : TryOnState()
    data class Error(val message: String, val exception: Exception? = null) : TryOnState()
}
