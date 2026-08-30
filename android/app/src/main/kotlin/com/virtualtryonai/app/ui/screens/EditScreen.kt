package com.virtualtryonai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.virtualtryonai.app.data.model.ClothingStyle
import com.virtualtryonai.app.data.model.ClothingVisibility
import com.virtualtryonai.app.data.model.PoseType
import com.virtualtryonai.app.data.model.TryOnState
import com.virtualtryonai.app.ui.viewmodel.TryOnViewModel

@Composable
fun EditScreen(
    navController: NavController,
    viewModel: TryOnViewModel = hiltViewModel()
) {
    val tryOnState by viewModel.tryOnState.collectAsState()
    val processedImage by viewModel.processedImage.collectAsState()
    val processingProgress by viewModel.processingProgress.collectAsState()
    val selectedClothing by viewModel.selectedClothing.collectAsState()
    val selectedColor by viewModel.selectedColor.collectAsState()
    val selectedPose by viewModel.selectedPose.collectAsState()
    val clothingVisibility by viewModel.clothingVisibility.collectAsState()

    var colorHue by remember { mutableStateOf(0f) }
    var showNudeOption by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "ویرایش و تعویض لباس",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Image Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                when (tryOnState) {
                    is TryOnState.Loading -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("$processingProgress%")
                        }
                    }
                    is TryOnState.Success -> {
                        Text("✓ پردازش انجام شد")
                    }
                    is TryOnState.Error -> {
                        Text(
                            text = (tryOnState as TryOnState.Error).message,
                            color = Color.Red
                        )
                    }
                    else -> Text("تصویر در اینجا نمایش داده می‌شود")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Clothing Selection
        Text(
            text = "انتخاب لباس",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Shirt
            ClothingButton(
                label = "پیراهن",
                isSelected = selectedClothing?.category == "shirt",
                onClick = {
                    viewModel.setSelectedClothing(
                        ClothingStyle(
                            id = "shirt_001",
                            name = "کلاسیک",
                            category = "shirt",
                            imageUrl = "",
                            description = "پیراهن کلاسیک"
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
            
            // Pants
            ClothingButton(
                label = "شلوار",
                isSelected = selectedClothing?.category == "pants",
                onClick = {
                    viewModel.setSelectedClothing(
                        ClothingStyle(
                            id = "pants_001",
                            name = "کلاسیک",
                            category = "pants",
                            imageUrl = "",
                            description = "شلوار کلاسیک"
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
            
            // Dress
            ClothingButton(
                label = "لباس",
                isSelected = selectedClothing?.category == "dress",
                onClick = {
                    viewModel.setSelectedClothing(
                        ClothingStyle(
                            id = "dress_001",
                            name = "کلاسیک",
                            category = "dress",
                            imageUrl = "",
                            description = "لباس کلاسیک"
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Color Selection
        Text(
            text = "رنگ لباس",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Slider(
            value = colorHue,
            onValueChange = { 
                colorHue = it
                viewModel.setSelectedColor(hueToColor(it))
            },
            valueRange = 0f..360f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        Text(
            text = "رنگ انتخاب شده: $selectedColor",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pose Selection
        Text(
            text = "تغییر پوزیشن بدن",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PoseButton(
                label = "ایستاده",
                isSelected = selectedPose?.id == "pose_standing",
                onClick = {
                    viewModel.setSelectedPose(
                        PoseType(
                            id = "pose_standing",
                            name = "Standing",
                            imageUrl = ""
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
            
            PoseButton(
                label = "نشسته",
                isSelected = selectedPose?.id == "pose_sitting",
                onClick = {
                    viewModel.setSelectedPose(
                        PoseType(
                            id = "pose_sitting",
                            name = "Sitting",
                            imageUrl = ""
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
            
            PoseButton(
                label = "دراز کشیده",
                isSelected = selectedPose?.id == "pose_lying",
                onClick = {
                    viewModel.setSelectedPose(
                        PoseType(
                            id = "pose_lying",
                            name = "Lying",
                            imageUrl = ""
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clothing Visibility Option
        Text(
            text = "وضعیت پوشش",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("بدون لباس")
            Switch(
                checked = clothingVisibility == ClothingVisibility.NUDE,
                onCheckedChange = { isNude ->
                    viewModel.setClothingVisibility(
                        if (isNude) ClothingVisibility.NUDE else ClothingVisibility.FULLY_CLOTHED
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Process Button
        Button(
            onClick = { viewModel.processImage() },
            enabled = tryOnState !is TryOnState.Loading && selectedClothing != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (tryOnState is TryOnState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "پردازش",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Export Button
        Button(
            onClick = { /* Export image */ },
            enabled = processedImage != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text("ذخیره تصویر")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Back Button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("بازگشت")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ClothingButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
    ) {
        Text(label)
    }
}

@Composable
fun PoseButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp)
    ) {
        Text(label)
    }
}

fun hueToColor(hue: Float): String {
    val rgb = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
    return String.format("#%06X", 0xFFFFFF and rgb)
}
