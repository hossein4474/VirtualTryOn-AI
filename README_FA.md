# Virtual Try-On AI 👔👗

**تطبیق اندروید هوشمند برای تعویض لباس و تغییر پوزیشن بدن**

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Status](https://img.shields.io/badge/Status-In%20Development-yellow.svg)]()
[![Android](https://img.shields.io/badge/Android-30%2B-green.svg)]()

## 🎯 ویژگی‌های اصلی

✅ **تعویض کامل لباس** - تغییر رنگ و مدل‌های مختلف پوشاک
✅ **تغییر پوزیشن بدن** - تغییر موقعیت و طراز ایستادن
✅ **حفاظت از چهره و هویت** - حفظ چهره اصلی در تصاویر پردازش شده
✅ **تنظیم پوشش** - امکان نمایش لباس یا بدون لباس
✅ **انتخاب رنگ متغیر** - طیف وسیع رنگ‌های لباس
✅ **پردازش ابری** - استفاده از AI های قدرتمند ابری
✅ **رابط کاربری سهل** - طراحی ساده و قابل فهم

## 🚀 شروع سریع

### پیش‌نیازها

- Android Studio (آخرین نسخه)
- Android SDK 30+
- JDK 11+
- Kotlin 1.9+
- اتصال اینترنت فعال

### نصب و راه‌اندازی

1. **کلون کردن مخزن:**
```bash
git clone https://github.com/hossein4474/VirtualTryOn-AI.git
cd VirtualTryOn-AI
```

2. **بازکردن در Android Studio:**
```
File → Open → انتخاب پوشه پروژه
```

3. **دریافت کلید API:**

به سایت‌های زیر مراجعه کنید و کلیدهای API خود را دریافت کنید:

- [RunwayML](https://app.runwayml.com) - برای تعویض لباس و تغییر پوز
- [Hugging Face](https://huggingface.co/settings/tokens) - برای تشخیص پوز �� چهره
- [Remove.bg](https://remove.bg/api) - برای حذف پس‌زمینه
- [InsightFace](https://insightface.ai) - برای تشخیص و حفاظت از چهره

4. **تنظیم کلیدها:**

فایل `android/local.properties` را ویرایش کنید:

```properties
RUNWAY_API_KEY=your_key_here
HUGGINGFACE_API_TOKEN=your_token_here
REMOVE_BG_API_KEY=your_key_here
INSIGHTFACE_API_KEY=your_key_here
```

5. **اجرای پروژه:**
```
Run → Run 'app'
```

## 📱 نحوه استفاده

### مرحله 1: انتخاب تصویر
- از صفحه اولیه، گزینه "دوربین" یا "ویرایش" را انتخاب کنید
- تصویر خود را بارگذاری کنید

### مرحله 2: انتخاب لباس
- یکی از گزینه‌های لباس را انتخاب کنید:
  - 👕 پیراهن
  - 👖 شلوار  
  - 👗 لباس

### مرحله 3: انتخاب رنگ
- لغزنده رنگ را حرکت دهید
- رنگ مورد نظر را انتخاب کنید

### مرحله 4: تغییر پوزیشن (اختیاری)
- موقعیت بدن را از بین گزینه‌ها انتخاب کنید:
  - 🧍 ایستاده
  - 🪑 نشسته
  - 🛏️ دراز کشیده

### مرحله 5: تنظیمات اضافی
- گزینه "بدون لباس" را فعال کنید (اختیاری)

### مرحله 6: پردازش
- دکمه "پردازش" را بزنید
- منتظر بمانید تا تصویر پردازش شود
- دکمه "ذخیره تصویر" را برای ذخیره کلیک کنید

## 🏗️ معماری پروژه

```
VirtualTryOn-AI/
├── android/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── kotlin/
│   │   │   │   ├── api/           # API Clients (Retrofit)
│   │   │   │   ├── data/          # مدل‌های داده و Repository
│   │   │   │   ├── di/            # Dependency Injection (Hilt)
│   │   │   │   ├── ui/            # UI Components (Compose)
│   │   │   │   └── MainActivity
│   │   │   └── res/               # Resources
│   │   └── build.gradle.kts
│   ├── settings.gradle.kts
│   └── local.properties
├── docs/
│   ├── API_GUIDE.md               # راهنمای API
│   ├── ARCHITECTURE.md            # معماری پروژه
│   └── SETUP.md                   # راهنمای نصب
├── README.md
└── README_FA.md                   # این فایل
```

## 🔧 فناوری‌های استفاده شده

### Frontend (Android)
- **Jetpack Compose** - UI Declarative
- **Kotlin** - زبان برنامه‌نویسی
- **Coroutines** - برنامه‌نویسی ناهمزمان
- **Flow/StateFlow** - مدیریت وضعیت
- **Hilt** - Dependency Injection

### API و پردازش
- **RunwayML** - تعویض لباس و پوز
- **Hugging Face** - تشخیص پوز و چهره
- **Remove.bg** - حذف پس‌زمینه
- **InsightFace** - حفاظت از چهره

### Networking
- **Retrofit** - HTTP Client
- **OkHttp** - HTTP Interceptors
- **Gson** - JSON Serialization

## 📊 فرآیند پردازش

```
1. بارگذاری تصویر
    ↓
2. تشخیص پوز و چهره
    ↓
3. حذف پس‌زمینه
    ↓
4. تعویض لباس و اعمال رنگ
    ↓
5. تغییر پوزیشن بدن (اگر لازم باشد)
    ↓
6. حفاظت و جایگذاری چهره
    ↓
7. پردازش نهایی و بهبود کیفیت
    ↓
8. نمایش و ذخیره نتیجه
```

## ⚙️ تنظیمات و خیارها

### متغیرهای محیطی

```bash
# API Keys
RUNWAY_API_KEY=xxx
HUGGINGFACE_API_TOKEN=xxx
REMOVE_BG_API_KEY=xxx
INSIGHTFACE_API_KEY=xxx

# اختیاری
DEBUG_MODE=true
LOG_LEVEL=INFO
```

### تنظیمات Android

```kotlin
// build.gradle.kts
android {
    compileSdk = 34
    minSdk = 30
    targetSdk = 34
}
```

## 🐛 حل مشکلات

### مشکل: "API Key not valid"
**راه حل:** 
- کلیدهای API را دوباره بررسی کنید
- مطمئن شوید `local.properties` درست تنظیم شده است
- دوباره compile کنید: `Build → Rebuild Project`

### مشکل: "Network error"
**راه حل:**
- اتصال اینترنت را بررسی کنید
- firewall را کنترل کنید
- VPN را امتحان کنید

### مشکل: "Permission denied"
**راه حل:**
- مجوزهای دوربین و دسترسی ذخیره‌سازی را بدهید
- تنظیمات → برنامه‌ها → VirtualTryOn → مجوزها

### مشکل: "Out of memory"
**راه حل:**
- تصاویر بزرگ‌تر از 5MB را کاهش دهید
- یک دستگاه قدرتمندتر استفاده کنید

## 📈 عملکرد و بهینه‌سازی

- **سرعت پردازش:** 30-120 ثانیه (بسته به سایز تصویر و API)
- **مصرف حافظه:** 200-500MB
- **اندازه APK:** ~30MB
- **سازگاری:** Android 12+

## 🔐 امنیت و حریم خصوصی

- ✅ تمام ارتباطات رمزگذاری‌شده (HTTPS)
- ✅ کلیدهای API در فایل محلی (بدون sync)
- ✅ داده‌های کاربری حذف می‌شوند
- ✅ بدون تجمیع داده‌های شخصی

## 📝 لایسنس

این پروژه تحت لایسنس MIT منتشر شده است. برای جزئیات بیشتر [LICENSE](LICENSE) را ببینید.

## 🤝 مشارکت

مشارکت شما خوش‌آمد است! لطفاً:

1. Fork کنید
2. یک branch جدید ایجاد کنید (`git checkout -b feature/AmazingFeature`)
3. تغییرات خود را commit کنید (`git commit -m 'Add AmazingFeature'`)
4. به branch push کنید (`git push origin feature/AmazingFeature`)
5. یک Pull Request باز کنید

## 📞 تماس و پشتیبانی

- **GitHub Issues:** برای گزارش مشکلات و پیشنهادات
- **Email:** hossein4474@example.com
- **Twitter:** @VirtualTryOnAI

## 🙏 تشکر

تشکر از تیم‌های:
- RunwayML
- Hugging Face
- Remove.bg
- InsightFace
- Android Jetpack Team

## 📚 منابع مفید

- [مستندات Android](https://developer.android.com)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [RunwayML API Docs](https://docs.runwayml.com)
- [Hugging Face API](https://huggingface.co/docs/api-inference)

## 🎓 درخت یادگیری

اگر می‌خواهید بیشتر یاد بگیرید:

1. **مبانی Compose:** [Google Codelab](https://developer.android.com/codelabs/jetpack-compose-basics)
2. **Coroutines:** [کتاب Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
3. **API Integration:** [Retrofit Guide](https://square.github.io/retrofit/)
4. **Machine Learning:** [ML Kit Google](https://developers.google.com/ml-kit)

---

**نسخه:** 1.0.0  
**آخرین به‌روز‌رسانی:** 30 آگوست 2026  
**نویسنده:** Hossein4474

⭐ اگر این پروژه مفید بود، ستاره بدهید!
