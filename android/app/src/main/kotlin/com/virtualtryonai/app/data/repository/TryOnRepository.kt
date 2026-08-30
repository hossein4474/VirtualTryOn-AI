package com.virtualtryonai.app.data.repository

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.virtualtryonai.app.BuildConfig
import com.virtualtryonai.app.data.api.HuggingFaceAPIService
import com.virtualtryonai.app.data.api.InsightFaceAPIService
import com.virtualtryonai.app.data.api.RemoveBgAPIService
import com.virtualtryonai.app.data.api.RunwayAPIService
import com.virtualtryonai.app.data.model.ClothingStyle
import com.virtualtryonai.app.data.model.ClothingTransferRequest
import com.virtualtryonai.app.data.model.ClothingVisibility
import com.virtualtryonai.app.data.model.FaceDetectionRequest
import com.virtualtryonai.app.data.model.FaceSwapRequest
import com.virtualtryonai.app.data.model.PoseDetectionRequest
import com.virtualtryonai.app.data.model.PoseTransferRequest
import com.virtualtryonai.app.data.model.PoseType
import com.virtualtryonai.app.data.model.TryOnRequest
import com.virtualtryonai.app.data.model.TryOnResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

class TryOnRepository @Inject constructor(
    private val runwayAPI: RunwayAPIService,
    private val huggingFaceAPI: HuggingFaceAPIService,
    private val removeBgAPI: RemoveBgAPIService,
    private val insightFaceAPI: InsightFaceAPIService
) {

    suspend fun processTryOn(
        request: TryOnRequest
    ): TryOnResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            Log.d("TryOnRepository", "Starting try-on process")
            
            // Step 1: Remove background for cleaner processing
            Log.d("TryOnRepository", "Step 1: Removing background")
            val noBackgroundImage = removeBackground(request.originalImage)
            
            // Step 2: Apply clothing transfer
            Log.d("TryOnRepository", "Step 2: Applying clothing transfer")
            val clothedImage = applyClothing(
                noBackgroundImage,
                request.clothingStyle,
                request.clothingColor
            )
            
            // Step 3: Transfer pose if specified
            val poseTransferredImage = if (request.targetPose != null) {
                Log.d("TryOnRepository", "Step 3: Transferring pose")
                transferPose(clothedImage, request.targetPose)
            } else {
                clothedImage
            }
            
            // Step 4: Preserve original face
            Log.d("TryOnRepository", "Step 4: Preserving face")
            val finalImage = if (request.clothingVisibility == ClothingVisibility.NUDE) {
                // For nude version, return body without face swap
                poseTransferredImage
            } else {
                preserveFace(request.originalImage, poseTransferredImage)
            }
            
            val processingTimeMs = System.currentTimeMillis() - startTime
            Log.d("TryOnRepository", "Try-on process completed in ${processingTimeMs}ms")
            
            TryOnResult(
                originalImage = request.originalImage,
                processedImage = finalImage,
                processingTimeMs = processingTimeMs
            )
        } catch (e: Exception) {
            Log.e("TryOnRepository", "Error during try-on processing", e)
            throw e
        }
    }

    private suspend fun removeBackground(image: Bitmap): Bitmap = withContext(Dispatchers.IO) {
        try {
            val file = bitmapToFile(image)
            val multipart = MultipartBody.Part.createFormData(
                "image_file",
                file.name,
                file.asRequestBody("image/png".toMediaType())
            )
            
            val response = removeBgAPI.removeBackground(
                multipart,
                apiKey = BuildConfig.REMOVE_BG_API_KEY
            )
            
            return@withContext if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    responseBody.byteStream().use { inputStream ->
                        android.graphics.BitmapFactory.decodeStream(inputStream)
                    } ?: image
                } else {
                    image
                }
            } else {
                Log.w("TryOnRepository", "Background removal failed: ${response.errorBody()}")
                image
            }
        } catch (e: Exception) {
            Log.e("TryOnRepository", "Error removing background", e)
            image
        }
    }

    private suspend fun applyClothing(
        image: Bitmap,
        clothingStyle: ClothingStyle,
        clothingColor: String
    ): Bitmap = withContext(Dispatchers.IO) {
        try {
            val base64Image = bitmapToBase64(image)
            val request = ClothingTransferRequest(
                sourceImage = base64Image,
                clothingStyle = clothingStyle.id,
                clothingColor = clothingColor
            )
            
            val response = runwayAPI.clothingTransfer(request)
            
            return@withContext if (response.isSuccessful && response.body()?.output != null) {
                base64ToBitmap(response.body()!!.output!!)
            } else {
                Log.w("TryOnRepository", "Clothing transfer failed: ${response.errorBody()}")
                image
            }
        } catch (e: Exception) {
            Log.e("TryOnRepository", "Error applying clothing", e)
            image
        }
    }

    private suspend fun transferPose(
        image: Bitmap,
        targetPose: PoseType
    ): Bitmap = withContext(Dispatchers.IO) {
        try {
            val base64Image = bitmapToBase64(image)
            val request = PoseTransferRequest(
                sourceImage = base64Image,
                targetPose = targetPose.id,
                preserveFace = true
            )
            
            val response = runwayAPI.transferPose(request)
            
            return@withContext if (response.isSuccessful && response.body()?.output != null) {
                base64ToBitmap(response.body()!!.output!!)
            } else {
                Log.w("TryOnRepository", "Pose transfer failed: ${response.errorBody()}")
                image
            }
        } catch (e: Exception) {
            Log.e("TryOnRepository", "Error transferring pose", e)
            image
        }
    }

    private suspend fun preserveFace(
        sourceImage: Bitmap,
        targetImage: Bitmap
    ): Bitmap = withContext(Dispatchers.IO) {
        try {
            val request = FaceSwapRequest(
                sourceImage = bitmapToBase64(sourceImage),
                targetImage = bitmapToBase64(targetImage)
            )
            
            val response = insightFaceAPI.swapFaces(request)
            
            return@withContext if (response.isSuccessful && response.body()?.output != null) {
                base64ToBitmap(response.body()!!.output!!)
            } else {
                Log.w("TryOnRepository", "Face preservation failed: ${response.errorBody()}")
                targetImage
            }
        } catch (e: Exception) {
            Log.e("TryOnRepository", "Error preserving face", e)
            targetImage
        }
    }

    suspend fun detectPose(image: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            val base64Image = bitmapToBase64(image)
            val request = PoseDetectionRequest(base64Image)
            val token = "Bearer ${BuildConfig.HUGGINGFACE_API_TOKEN}"
            
            val response = huggingFaceAPI.poseDetection(request, token)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.keypoints?.toString() ?: "")
            } else {
                Result.failure(Exception("Pose detection failed: ${response.errorBody()}"))
            }
        } catch (e: Exception) {
            Log.e("TryOnRepository", "Error detecting pose", e)
            Result.failure(e)
        }
    }

    suspend fun detectFace(image: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            val base64Image = bitmapToBase64(image)
            val request = FaceDetectionRequest(base64Image)
            
            val response = insightFaceAPI.detectFaces(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.imageQuality?.toString() ?: "")
            } else {
                Result.failure(Exception("Face detection failed: ${response.errorBody()}"))
            }
        } catch (e: Exception) {
            Log.e("TryOnRepository", "Error detecting face", e)
            Result.failure(e)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File.createTempFile("image", ".png")
        val outputStream = file.outputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()
        return file
    }
}
