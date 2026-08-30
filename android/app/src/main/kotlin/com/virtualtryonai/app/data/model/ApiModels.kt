package com.virtualtryonai.app.data.model

import com.google.gson.annotations.SerializedName

// RunwayML Models
data class ClothingTransferRequest(
    @SerializedName("source_image")
    val sourceImage: String,
    @SerializedName("clothing_style")
    val clothingStyle: String,
    @SerializedName("clothing_color")
    val clothingColor: String
)

data class PoseTransferRequest(
    @SerializedName("source_image")
    val sourceImage: String,
    @SerializedName("target_pose")
    val targetPose: String,
    @SerializedName("preserve_face")
    val preserveFace: Boolean = true
)

data class StyleTransferRequest(
    @SerializedName("source_image")
    val sourceImage: String,
    @SerializedName("style_reference")
    val styleReference: String
)

data class ImageResponse(
    val id: String,
    val status: String,
    val output: String? = null,
    val error: String? = null
)

// Hugging Face Models
data class PoseDetectionRequest(
    val inputs: String
)

data class PoseDetectionResponse(
    val keypoints: List<List<Float>>? = null,
    val boxes: List<BoundingBox>? = null,
    val score: Float? = null
)

data class BoundingBox(
    val xmin: Float,
    val ymin: Float,
    val xmax: Float,
    val ymax: Float,
    val label: String
)

data class FaceRecognitionRequest(
    val inputs: String
)

data class FaceRecognitionResponse(
    val embeddings: List<Float>? = null,
    @SerializedName("face_location")
    val faceLocation: List<Int>? = null,
    val confidence: Float? = null
)

// Remove.bg Models
data class RemoveBgRequest(
    @SerializedName("image_file")
    val imageFile: String,
    val size: String = "auto",
    val type: String = "auto"
)

// InsightFace Models
data class FaceDetectionRequest(
    val image: String,
    @SerializedName("return_face_data")
    val returnFaceData: Boolean = true
)

data class FaceDetectionResponse(
    val faces: List<Face>? = null,
    @SerializedName("image_quality")
    val imageQuality: Float? = null
)

data class Face(
    val bbox: List<Float>,
    val kps: List<List<Float>>? = null,
    @SerializedName("det_score")
    val detScore: Float
)

data class FaceSwapRequest(
    @SerializedName("source_image")
    val sourceImage: String,
    @SerializedName("target_image")
    val targetImage: String,
    @SerializedName("source_face_index")
    val sourceFaceIndex: Int = 0,
    @SerializedName("target_face_index")
    val targetFaceIndex: Int = 0
)

data class FaceParseRequest(
    val image: String
)

data class FaceParseResponse(
    @SerializedName("parsing_map")
    val parsingMap: String? = null,
    @SerializedName("face_regions")
    val faceRegions: Map<String, String>? = null
)
