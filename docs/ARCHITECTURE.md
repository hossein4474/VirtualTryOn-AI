# VirtualTryOn-AI - Architecture Overview 🏗️

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    ANDROID APP (UI Layer)                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Home Screen  │  │ Camera Screen│  │ Edit Screen  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────┬─────────────────────────────────────┬──────────┘
             │                                     │
             ▼                                     ▼
┌─────────────────────────────────────────────────────────────┐
│              VIEWMODEL & STATE MANAGEMENT                    │
│         (MVVM Pattern with Flow/StateFlow)                  │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         TryOnViewModel (Central State)              │   │
│  │  - Image Loading & Caching                         │   │
│  │  - API Request Management                          │   │
│  │  - UI State Updates                                │   │
│  └──────────────────────────────────────────────────────┘   │
└────────────┬─────────────────────────────────────┬──────────┘
             │                                     │
             ▼                                     ��
┌─────────────────────────────────────────────────────────────┐
│            REPOSITORY & DATA LAYER                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ TryOnRepository                                      │   │
│  │  - Coordinates multiple API calls                   │   │
│  │  - Caches results locally                           │   │
│  │  - Error handling & retry logic                     │   │
│  └──────────────────────────────────────────────────────┘   │
└────────────┬─────────────────────────────────────┬──────────┘
             │                                     │
             ▼                                     ▼
┌─────────────────────────────────────────────────────────────┐
│            API SERVICE LAYER (Retrofit)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ RunwayAPI    │  │ HuggingFaceAPI│ │ RemoveBgAPI  │      │
│  │ (Clothes &   │  │ (Face & Pose) │ │ (Background) │      │
│  │  Pose)       │  │               │  │              │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────┬─────────────────────────────────────┬──────────┘
             │                                     │
             ▼                                     ▼
┌─────────────────────────────────────────────────────────────┐
│          EXTERNAL CLOUD SERVICES (APIs)                     │
│  ┌──────────────┐  ┌���─────────────┐  ┌──────────────┐      │
│  │ RunwayML     │  │ Hugging Face │  │ Remove.bg    │      │
│  │ - Clothing   │  │ - Pose       │  │ - Background │      │
│  │ - Pose       │  │ - Face       │  │   Removal    │      │
│  │ - Style      │  │   Recognition│  │              │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

## Component Details

### 1. UI Layer (Jetpack Compose)

#### MainActivity.kt
- Entry point of the application
- Sets up navigation graph
- Manages app permissions

#### Screens
- **HomeScreen**: Initial UI with options to:
  - Upload photo
  - Take new photo
  - Select clothing style
  
- **CameraScreen**: 
  - Real-time camera preview
  - Capture image
  - Image preview before processing
  
- **EditScreen**:
  - Display original and processed images
  - Clothing color selector
  - Pose selector
  - Clothing removal toggle
  - Process/Export buttons

### 2. ViewModel Layer

#### TryOnViewModel.kt
```kotlin
class TryOnViewModel : ViewModel() {
    // State flows
    val originalImage: StateFlow<Bitmap?>
    val processedImage: StateFlow<Bitmap?>
    val isLoading: StateFlow<Boolean>
    val clothingStyle: StateFlow<ClothingStyle>
    val clothingColor: StateFlow<Color>
    val posePosition: StateFlow<PoseType>
    val removeClothing: StateFlow<Boolean>
    
    // Functions
    fun loadImage(uri: Uri)
    fun processImage()
    fun updateClothing(style: ClothingStyle)
    fun updateColor(color: Color)
    fun updatePose(pose: PoseType)
    fun toggleClothing()
    fun exportImage()
}
```

### 3. Repository Layer

#### TryOnRepository.kt
```kotlin
class TryOnRepository(
    private val runwayAPI: RunwayAPIService,
    private val huggingFaceAPI: HuggingFaceAPIService,
    private val removeBgAPI: RemoveBgAPIService,
    private val insightFaceAPI: InsightFaceAPIService
) {
    // Step 1: Extract pose and face
    suspend fun extractPoseAndFace(image: Bitmap): PoseData
    
    // Step 2: Remove background
    suspend fun removeBackground(image: Bitmap): Bitmap
    
    // Step 3: Generate clothing
    suspend fun generateClothing(
        image: Bitmap,
        style: ClothingStyle,
        color: Color
    ): Bitmap
    
    // Step 4: Apply pose transfer
    suspend fun transferPose(
        sourceImage: Bitmap,
        targetPose: PoseType
    ): Bitmap
    
    // Step 5: Composite and preserve face
    suspend fun compositeFinalImage(
        poseImage: Bitmap,
        originalFace: Bitmap,
        clothingImage: Bitmap
    ): Bitmap
}
```

### 4. API Service Layer

#### RunwayAPIService.kt
- Clothing transfer API
- Pose transfer API
- Style transfer API
- Image upscaling

#### HuggingFaceAPIService.kt
- Pose detection
- Face recognition
- Face swap (optional)
- Segmentation models

#### RemoveBgAPIService.kt
- Background removal
- Image segmentation

#### InsightFaceAPIService.kt
- Advanced face recognition
- Face preservation algorithms

### 5. Data Models

```kotlin
data class ClothingStyle(
    val id: String,
    val name: String,
    val category: String, // shirt, pants, dress, etc.
    val imageUrl: String,
    val description: String
)

data class PoseType(
    val id: String,
    val name: String,
    val imageUrl: String
)

data class PoseData(
    val keypoints: List<Keypoint>,
    val confidence: Float,
    val pose: PoseType
)

data class Keypoint(
    val x: Float,
    val y: Float,
    val confidence: Float,
    val name: String
)

enum class ClothingVisibility {
    FULLY_CLOTHED,
    PARTIALLY_CLOTHED,
    NUDE
}
```

## Processing Pipeline

### Step-by-Step Flow

```
1. USER UPLOADS IMAGE
   ↓
2. EXTRACT POSE & FACE
   - Use Hugging Face pose detection
   - Extract face landmarks with InsightFace
   - Store keypoints
   ↓
3. REMOVE BACKGROUND
   - Use Remove.bg API
   - Get clean body silhouette
   ↓
4. GENERATE NEW BODY WITH CLOTHING
   - Use RunwayML for clothing transfer
   - Apply selected color and style
   - Generate different poses if needed
   ↓
5. TRANSFER POSE (if different from original)
   - Use RunwayML pose transfer
   - Maintain body shape and clothing
   ↓
6. COMPOSITE FINAL IMAGE
   - Blend body with clothing
   - Swap original face back
   - Ensure natural appearance
   ↓
7. APPLY QUALITY ENHANCEMENTS
   - Upscale image if needed
   - Color correction
   - Smoothing edges
   ↓
8. RETURN TO USER
   - Display result
   - Allow export/sharing
```

## Data Flow (MVVM)

```
UI (Compose) 
    ↓ (user action)
ViewModel (receive action)
    ↓ (call repository)
Repository (coordinate APIs)
    ↓ (make HTTP requests)
API Services (Retrofit clients)
    ↓ (send to cloud)
Cloud APIs (RunwayML, HuggingFace, etc.)
    ↓ (return processed image)
API Services (parse response)
    ↓ (cache result)
Repository (return Bitmap/Data)
    ↓ (update state)
ViewModel (update StateFlow)
    ↓ (observe changes)
UI (render new image)
```

## Key Design Patterns

### 1. MVVM (Model-View-ViewModel)
- Separates UI logic from business logic
- ViewModel survives configuration changes
- StateFlow for reactive updates

### 2. Repository Pattern
- Single source of truth for data
- Abstracts API calls
- Enables easy testing with mocks

### 3. Dependency Injection
- Using Hilt for dependency management
- Easier testing and maintenance

### 4. Coroutines
- Async API calls
- Main thread remains responsive
- Easy error handling

## Performance Considerations

### Image Processing Optimization
1. **Caching**: Store processed images locally
2. **Compression**: Compress images before upload (≤5MB)
3. **Batch Processing**: Process multiple requests in parallel
4. **Rate Limiting**: Respect API rate limits
5. **Progress Updates**: Show progress to user during processing

### Memory Management
1. **Bitmap Recycling**: Recycle old bitmaps
2. **Streaming**: Use streaming for large files
3. **Resource Cleanup**: Close connections properly

### Network Optimization
1. **Connection Pooling**: Reuse HTTP connections
2. **Timeouts**: Set appropriate timeouts
3. **Retry Logic**: Implement exponential backoff
4. **Offline Support**: Cache recent results

## Security Considerations

1. **API Keys**: Store in secure SharedPreferences or Keystore
2. **HTTPS**: All connections must be encrypted
3. **Data Privacy**: Inform users about data usage
4. **Image Deletion**: Delete processed images after use
5. **Authentication**: Use OAuth 2.0 for APIs requiring it

## Future Enhancements

1. On-device ML models (TensorFlow Lite) for faster processing
2. Video support for try-on demonstrations
3. AR integration for real-time preview
4. Social sharing features
5. User accounts and history
6. Advanced body customization (height, weight, etc.)

