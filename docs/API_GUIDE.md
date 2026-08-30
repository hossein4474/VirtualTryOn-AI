# VirtualTryOn-AI - API Integration Guide 🔌

## Overview

This guide explains how to integrate with each cloud API service for the virtual try-on functionality.

## 1. RunwayML API - Clothing & Pose Transfer

**Website**: https://www.runwayml.com/
**Documentation**: https://docs.runwayml.com/

### Setup

1. Create account at runwayml.com
2. Go to Settings → API Keys
3. Create new API key
4. Copy and save in `.env`

### Features Used

- **Clothing Transfer**: Transfer clothes between bodies
- **Pose Transfer**: Change body position
- **Style Transfer**: Apply clothing styles

### Implementation

```kotlin
// RetrofitClient.kt
object RetrofitClient {
    private const val RUNWAY_BASE_URL = "https://api.runwayml.com/v1/"
    
    private val runwayClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.RUNWAY_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()
    
    val runwayAPI: RunwayAPIService = Retrofit.Builder()
        .baseUrl(RUNWAY_BASE_URL)
        .client(runwayClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RunwayAPIService::class.java)
}

// RunwayAPIService.kt
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
}

// Data Classes
data class ClothingTransferRequest(
    val sourceImage: String, // Base64 or URL
    val clothingStyle: String,
    val clothingColor: String,
    val personSegmentation: String? = null
)

data class PoseTransferRequest(
    val sourceImage: String,
    val targetPose: String, // Pose keypoints JSON
    val preserveFace: Boolean = true
)

data class ImageResponse(
    val id: String,
    val status: String, // processing, completed, failed
    val output: String?, // Base64 output
    val error: String?
)
```

### API Endpoint Examples

```bash
# Clothing Transfer
curl -X POST https://api.runwayml.com/v1/image_to_image \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "sourceImage": "base64_image_data",
    "clothingStyle": "formal_shirt",
    "clothingColor": "blue"
  }'

# Pose Transfer
curl -X POST https://api.runwayml.com/v1/pose_transfer \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "sourceImage": "base64_image_data",
    "targetPose": "pose_keypoints_json",
    "preserveFace": true
  }'
```

## 2. Hugging Face API - Pose Detection & Face Recognition

**Website**: https://huggingface.co/
**Documentation**: https://huggingface.co/docs/api-inference
**Models Used**:
- `facebook/detr-resnet50-panoptic` - Object detection
- `openmmlab/mmpose-video-medium` - Pose estimation
- `Insightface/inswapper` - Face swap

### Setup

1. Create account at huggingface.co
2. Go to Settings → Access Tokens
3. Create read token
4. Copy and save in `.env`

### Implementation

```kotlin
// HuggingFaceAPIService.kt
interface HuggingFaceAPIService {
    
    @POST("inference")
    suspend fun poseDetection(
        @Body request: PoseDetectionRequest,
        @Header("Authorization") auth: String
    ): Response<PoseDetectionResponse>
    
    @POST("inference")
    suspend fun faceRecognition(
        @Body request: FaceRecognitionRequest,
        @Header("Authorization") auth: String
    ): Response<FaceRecognitionResponse>
}

data class PoseDetectionRequest(
    val inputs: String, // Base64 image or URL
    val model: String = "facebook/detr-resnet50-panoptic"
)

data class PoseDetectionResponse(
    val keypoints: List<List<Float>>, // [[x1,y1,conf1], [x2,y2,conf2], ...]
    val boxes: List<Box>,
    val score: Float
)

data class Box(
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
    val embeddings: List<Float>,
    val face_location: List<Int>, // [top, right, bottom, left]
    val confidence: Float
)
```

### API Usage Examples

```bash
# Pose Detection
curl -X POST https://api-inference.huggingface.co/models/facebook/detr-resnet50-panoptic \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --data-binary @image.jpg

# Face Detection
curl -X POST https://api-inference.huggingface.co/models/mediapipe/face_detector \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --data-binary @image.jpg
```

## 3. Remove.bg API - Background Removal

**Website**: https://remove.bg/
**Documentation**: https://remove.bg/api

### Setup

1. Go to https://remove.bg/api
2. Sign up for free or premium account
3. Copy API key from dashboard
4. Save in `.env`

### Implementation

```kotlin
// RemoveBgAPIService.kt
interface RemoveBgAPIService {
    
    @Multipart
    @POST("v1/removebg")
    suspend fun removeBackground(
        @Part("image_file") image: MultipartBody.Part,
        @Part("size") size: String = "auto",
        @Header("X-API-Key") apiKey: String
    ): Response<ResponseBody>
}

// Repository usage
class TryOnRepository(
    private val removeBgAPI: RemoveBgAPIService
) {
    suspend fun removeBackground(bitmap: Bitmap): Bitmap {
        val file = bitmapToFile(bitmap)
        val multipart = MultipartBody.Part.createFormData(
            "image_file",
            file.name,
            file.asRequestBody("image/png".toMediaType())
        )
        
        val response = removeBgAPI.removeBackground(multipart)
        
        return if (response.isSuccessful) {
            response.body()?.byteStream()?.use { 
                BitmapFactory.decodeStream(it)
            } ?: bitmap
        } else {
            bitmap
        }
    }
}
```

### API Endpoint

```bash
curl -X POST https://api.remove.bg/v1.0/removebg \
  -H "X-API-Key: YOUR_API_KEY" \
  -F "image_file=@/path/to/image.jpg"
```

### Response Options

```kotlin
// Parameters
@Multipart
@POST("v1/removebg")
suspend fun removeBackground(
    @Part("image_file") image: MultipartBody.Part,
    @Part("size") size: String = "auto", // auto, preview, small, medium, regular, hd, 4k
    @Part("type") type: String = "auto", // auto, person, product, car
    @Part("format") format: String = "auto", // auto, png, jpg, zip
    @Part("type_level") typeLevel: String = "none", // none, 1, 2
    @Part("channel") channel: String = "rgba", // rgba, alpha
    @Part("shadow") shadow: String = "false",
    @Header("X-API-Key") apiKey: String
): Response<ResponseBody>
```

## 4. InsightFace API - Advanced Face Operations

**Website**: https://insightface.ai/
**Documentation**: https://docs.insightface.ai/

### Features

- Face detection and recognition
- Face alignment
- Face parsing
- Face swapping
- Face quality assessment

### Implementation

```kotlin
// InsightFaceAPIService.kt
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

data class FaceDetectionRequest(
    val image: String, // Base64
    val return_face_data: Boolean = true
)

data class FaceDetectionResponse(
    val faces: List<Face>,
    val image_quality: Float
)

data class Face(
    val bbox: List<Float>, // [x1, y1, x2, y2]
    val kps: List<List<Float>>, // keypoints
    val det_score: Float
)

data class FaceSwapRequest(
    val source_image: String,
    val target_image: String,
    val source_face_index: Int = 0,
    val target_face_index: Int = 0
)

data class FaceParseRequest(
    val image: String
)

data class FaceParseResponse(
    val parsing_map: String, // Base64
    val face_regions: Map<String, String> // skin, hair, lips, etc.
)
```

## 5. Complete Example - Full Processing Pipeline

```kotlin
class TryOnRepository(
    private val runwayAPI: RunwayAPIService,
    private val huggingFaceAPI: HuggingFaceAPIService,
    private val removeBgAPI: RemoveBgAPIService,
    private val insightFaceAPI: InsightFaceAPIService
) {
    
    suspend fun processVirtualTryOn(
        originalImage: Bitmap,
        clothingStyle: ClothingStyle,
        clothingColor: Color,
        targetPose: PoseType,
        removeClothing: Boolean
    ): Bitmap {
        try {
            // Step 1: Detect pose and face
            val base64Image = bitmapToBase64(originalImage)
            val poseResponse = huggingFaceAPI.poseDetection(
                PoseDetectionRequest(base64Image),
                "Bearer ${BuildConfig.HUGGINGFACE_API_TOKEN}"
            )
            
            // Step 2: Remove background for cleaner processing
            val noBackground = removeBgAPI.removeBackground(originalImage)
            
            // Step 3: Generate new clothing
            val clothingRequest = ClothingTransferRequest(
                sourceImage = bitmapToBase64(noBackground),
                clothingStyle = clothingStyle.id,
                clothingColor = colorToHex(clothingColor)
            )
            val clothingResponse = runwayAPI.clothingTransfer(clothingRequest)
            val clothedImage = base64ToBitmap(clothingResponse.body()?.output ?: "")
            
            // Step 4: Transfer pose if different
            val poseTransferRequest = PoseTransferRequest(
                sourceImage = bitmapToBase64(clothedImage),
                targetPose = targetPose.id,
                preserveFace = true
            )
            val poseResponse = runwayAPI.transferPose(poseTransferRequest)
            val finalBodyImage = base64ToBitmap(poseResponse.body()?.output ?: "")
            
            // Step 5: Preserve original face
            val faceSwapRequest = FaceSwapRequest(
                source_image = bitmapToBase64(originalImage),
                target_image = bitmapToBase64(finalBodyImage)
            )
            val finalImage = if (!removeClothing) {
                val swapResponse = insightFaceAPI.swapFaces(faceSwapRequest)
                base64ToBitmap(swapResponse.body()?.output ?: "")
            } else {
                // Handle nude version if needed
                finalBodyImage
            }
            
            return finalImage
            
        } catch (e: Exception) {
            Log.e("TryOn", "Processing failed", e)
            throw e
        }
    }
}
```

## Rate Limits & Pricing

| Service | Free Tier | Rate Limit | Notes |
|---------|-----------|-----------|-------|
| RunwayML | 25 credits | - | Pay per credit |
| Hugging Face | Unlimited | 30 req/min | Free tier limited |
| Remove.bg | 50 API calls | Varies | $0.10 per call after free |
| InsightFace | Limited free | Varies | Contact for pricing |

## Error Handling

```kotlin
class APIErrorHandler {
    fun handleError(exception: Exception): APIError {
        return when (exception) {
            is HttpException -> APIError.ServerError(
                exception.code(),
                exception.message()
            )
            is IOException -> APIError.NetworkError(
                exception.message ?: "Network error"
            )
            else -> APIError.UnknownError(exception.message ?: "Unknown error")
        }
    }
}

sealed class APIError {
    data class ServerError(val code: Int, val message: String) : APIError()
    data class NetworkError(val message: String) : APIError()
    data class UnknownError(val message: String) : APIError()
}
```

## Testing APIs

### Using Postman

1. Import API collections from respective services
2. Set environment variables for API keys
3. Test endpoints individually
4. Use pre-defined requests for reference

### Using cURL

See examples above in each service section.

### Monitoring

- Use Postman or Insomnia for API testing
- Monitor API usage in respective dashboards
- Set up alerts for rate limit warnings
- Log all API requests for debugging

