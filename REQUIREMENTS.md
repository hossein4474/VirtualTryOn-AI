# Requirements - نیازمندی‌های پروژه

## محیط توسعه
- **Android Studio:** 2023.3+
- **Android SDK:** 30+
- **Kotlin:** 1.9+
- **JDK:** 11+
- **Gradle:** 8.0+

## وابستگی‌های اصلی

### Android & Jetpack
```gradle
androidx.core:core-ktx:1.12.0
androidx.lifecycle:lifecycle-runtime-ktx:2.6.2
androidx.activity:activity-compose:1.8.1
androidx.navigation:navigation-compose:2.7.5
androidx.datastore:datastore-preferences:1.0.0
```

### Jetpack Compose
```gradle
androidx.compose:compose-bom:2023.10.01
androidx.compose.ui:ui
androidx.compose.ui:ui-graphics
androidx.compose.material3:material3
androidx.compose.material:material-icons-extended
```

### Networking
```gradle
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.retrofit2:converter-gson:2.9.0
com.squareup.okhttp3:okhttp:4.11.0
com.squareup.okhttp3:logging-interceptor:4.11.0
```

### Image Processing
```gradle
io.coil-kt:coil-compose:2.5.0
com.github.bumptech.glide:glide:4.16.0
```

### Dependency Injection
```gradle
com.google.dagger:hilt-android:2.48
androidx.hilt:hilt-navigation-compose:1.1.0
```

### Coroutines
```gradle
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3
```

### Serialization
```gradle
com.google.code.gson:gson:2.10.1
```

### Testing
```gradle
junit:junit:4.13.2
androidx.test.ext:junit:1.1.5
androidx.test.espresso:espresso-core:3.5.1
androidx.compose.ui:ui-test-junit4
```

## API Keys مورد نیاز

| API | لینک | هدف |
|-----|-----|------|
| RunwayML | https://runwayml.com | تعویض لباس و پوز |
| Hugging Face | https://huggingface.co | تشخیص پوز و چهره |
| Remove.bg | https://remove.bg | حذف پس‌زمینه |
| InsightFace | https://insightface.ai | حفاظت از چهره |

## مجوزهای اندروید

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_MEDIA_LOCATION" />
```

## مشخصات دستگاه توصیه‌شده

### حداقل
- RAM: 2GB
- Storage: 100MB
- Android: 12+

### توصیه‌شده
- RAM: 4GB+
- Storage: 500MB+
- Android: 13+
- Processor: Snapdragon 700+

## نرم‌افزارهای اختیاری

- **Git:** برای کنترل ورژن
- **Postman:** برای تست API
- **Figma:** برای طراحی UI
- **Android Emulator:** برای تست

---

**آخرین بروزرسانی:** 30 آگوست 2026
