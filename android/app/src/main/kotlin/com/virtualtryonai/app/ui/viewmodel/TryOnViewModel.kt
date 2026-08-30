package com.virtualtryonai.app.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualtryonai.app.data.model.ClothingStyle
import com.virtualtryonai.app.data.model.ClothingVisibility
import com.virtualtryonai.app.data.model.PoseType
import com.virtualtryonai.app.data.model.TryOnRequest
import com.virtualtryonai.app.data.model.TryOnState
import com.virtualtryonai.app.data.repository.TryOnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TryOnViewModel @Inject constructor(
    private val repository: TryOnRepository
) : ViewModel() {

    private val _originalImage = MutableStateFlow<Bitmap?>(null)
    val originalImage: StateFlow<Bitmap?> = _originalImage.asStateFlow()

    private val _processedImage = MutableStateFlow<Bitmap?>(null)
    val processedImage: StateFlow<Bitmap?> = _processedImage.asStateFlow()

    private val _tryOnState = MutableStateFlow<TryOnState>(TryOnState.Idle)
    val tryOnState: StateFlow<TryOnState> = _tryOnState.asStateFlow()

    private val _selectedClothing = MutableStateFlow<ClothingStyle?>(null)
    val selectedClothing: StateFlow<ClothingStyle?> = _selectedClothing.asStateFlow()

    private val _selectedColor = MutableStateFlow("#000000")
    val selectedColor: StateFlow<String> = _selectedColor.asStateFlow()

    private val _selectedPose = MutableStateFlow<PoseType?>(null)
    val selectedPose: StateFlow<PoseType?> = _selectedPose.asStateFlow()

    private val _clothingVisibility = MutableStateFlow(ClothingVisibility.FULLY_CLOTHED)
    val clothingVisibility: StateFlow<ClothingVisibility> = _clothingVisibility.asStateFlow()

    private val _processingProgress = MutableStateFlow(0)
    val processingProgress: StateFlow<Int> = _processingProgress.asStateFlow()

    fun setOriginalImage(bitmap: Bitmap) {
        _originalImage.value = bitmap
        _tryOnState.value = TryOnState.Idle
        _processedImage.value = null
    }

    fun setSelectedClothing(clothing: ClothingStyle) {
        _selectedClothing.value = clothing
    }

    fun setSelectedColor(color: String) {
        _selectedColor.value = color
    }

    fun setSelectedPose(pose: PoseType?) {
        _selectedPose.value = pose
    }

    fun setClothingVisibility(visibility: ClothingVisibility) {
        _clothingVisibility.value = visibility
    }

    fun processImage() {
        val original = _originalImage.value ?: run {
            _tryOnState.value = TryOnState.Error("No image selected")
            return
        }
        
        val clothing = _selectedClothing.value ?: run {
            _tryOnState.value = TryOnState.Error("No clothing selected")
            return
        }

        viewModelScope.launch {
            _tryOnState.value = TryOnState.Loading
            _processingProgress.value = 0

            try {
                val request = TryOnRequest(
                    originalImage = original,
                    clothingStyle = clothing,
                    clothingColor = _selectedColor.value,
                    targetPose = _selectedPose.value,
                    clothingVisibility = _clothingVisibility.value
                )

                _processingProgress.value = 25
                val result = repository.processTryOn(request)
                _processingProgress.value = 100

                _processedImage.value = result.processedImage
                _tryOnState.value = TryOnState.Success(result)
            } catch (e: Exception) {
                _tryOnState.value = TryOnState.Error(
                    message = e.message ?: "Unknown error occurred",
                    exception = e
                )
            }
        }
    }

    fun resetState() {
        _originalImage.value = null
        _processedImage.value = null
        _tryOnState.value = TryOnState.Idle
        _selectedClothing.value = null
        _selectedColor.value = "#000000"
        _selectedPose.value = null
        _clothingVisibility.value = ClothingVisibility.FULLY_CLOTHED
        _processingProgress.value = 0
    }

    fun getProcessedImage(): Bitmap? {
        return _processedImage.value
    }
}
