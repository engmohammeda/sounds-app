# 📋 التوثيق التقني الشامل

## هيكل الملفات

### 1. `MainActivity.kt` - نقطة الدخول
**المسار:** `app/src/main/java/com/agon/app/MainActivity.kt`

**الوظيفة:** نقطة دخول التطبيق الرئيسية. يحتوي على:
- `MainActivity` - النشاط الرئيسي مع Edge-to-Edge
- `MainApp()` - Composable الجذر مع NavHost و BottomNav
- `BottomNav()` - شريط التنقل السفلي مع 3 عناصر

**المسارات:**
| المسار | الشاشة | الوصف |
|--------|--------|-------|
| `home` | HomeScreen | الشاشة الرئيسية |
| `favorites` | FavoritesScreen | المفضلة |
| `settings` | SettingsScreen | الإعدادات |
| `category/{name}` | CategoryScreen | تصنيف محدد |
| `timer` | TimerScreen | مؤقت النوم |
| `mixer` | MixerScreen | خلاط الأصوات |

---

### 2. `AudioEngine.kt` - محرك الصوت
**المسار:** `app/src/main/java/com/agon/app/data/AudioEngine.kt`

**الوظيفة:** محرك التوليد الصوتي الأساسي. يستخدم `AudioTrack` API لتوليد الصوت في الوقت الفعلي.

**المعاملات:**
- `sampleRate = 44100` - معدل العينات
- `bufferSize` - حجم المخزن المؤقت (ديناميكي)
- `masterVolume` - مستوى الصوت الرئيسي
- `fadeMultiplier` - مضاعف التلاشي للمؤقت

**الدوال الرئيسية:**
```kotlin
fun play(sound: SoundItem, volume: Float)    // تشغيل صوت
fun stop(soundId: String)                     // إيقاف صوت
fun stopAll()                                 // إيقاف الكل
fun setVolume(soundId: String, volume: Float) // ضبط مستوى صوت
fun setMasterVolume(volume: Float)            // الصوت الرئيسي
fun setFadeMultiplier(mult: Float)            // مضاعف التلاشي
fun isPlaying(soundId: String): Boolean       // حالة التشغيل
fun getActiveSoundIds(): Set<String>          // الأصوات النشطة
fun release()                                 // تحرير الموارد
```

**خوارزميات التوليد:**

1. **Binaural Beats:** `sin(2π·f1·t) + sin(2π·f2·t)` حيث `|f1-f2|` = تردد النبض
2. **Pink Noise:** خوارزمية Voss-McCartney مع 7 مرشحات
3. **Brown Noise:** حركة براونية `x[n] = x[n-1] + 0.02·white`
4. **Ocean:** ضوضاء بنية × (موجتان جيبيتان بترددات مختلفة)
5. **Rain:** ضوضاء وردية + قطرات عشوائية
6. **Heartbeat:** `lub = sin(60t)·e^(-30t)` + `dub = sin(80t)·e^(-40t)`

---

### 3. `SoundModels.kt` - نماذج البيانات
**المسار:** `app/src/main/java/com/agon/app/data/SoundModels.kt`

**المحتويات:**

#### `SoundCategory` (Enum)
9 تصنيفات مع اسم عربي، أيقونة، لون، ووصف.

#### `WaveType` (Enum)
28 نوع موجة مدعوم.

#### `SoundItem` (Data Class)
```kotlin
data class SoundItem(
    val id: String,              // معرف فريد
    val name: String,            // الاسم بالعربية
    val nameEn: String,          // الاسم بالإنجليزية
    val category: SoundCategory, // التصنيف
    val waveType: WaveType,      // نوع الموجة
    val baseFrequency: Float,    // التردد الأساسي
    val secondaryFrequency: Float, // التردد الثانوي (للثنائي)
    val description: String,     // الوصف
    val icon: String,            // اسم الأيقونة
    val volume: Float,           // مستوى الصوت الافتراضي
    val modulationRate: Float,   // معدل التعديل
    val modulationDepth: Float,  // عمق التعديل
    val filterCutoff: Float,     // تردد القطع للمرشح
    val harmonics: Int,          // عدد التوافقيات
    val attackMs: Int,           // زمن الهجوم
    val releaseMs: Int           // زمن الإطلاق
)
```

#### `SoundLibrary` (Object)
- `timerPresets` - 11 خيار زمني
- `allSounds` - 81 صوت وتردد
- `getSoundsByCategory()` - فلترة حسب التصنيف

---

### 4. `SoundViewModel.kt` - إدارة الحالة
**المسار:** `app/src/main/java/com/agon/app/viewmodel/SoundViewModel.kt`

**الحالة (`SoundUiState`):**
```kotlin
data class SoundUiState(
    val activeSounds: Map<String, Float>,  // الأصوات النشطة + مستوياتها
    val isAnyPlaying: Boolean,             // هل يوجد صوت قيد التشغيل
    val timerMinutes: Int,                 // مدة المؤقت
    val timerRemainingSeconds: Long,       // الوقت المتبقي
    val isTimerRunning: Boolean,           // حالة المؤقت
    val masterVolume: Float,               // الصوت الرئيسي
    val selectedCategory: SoundCategory?,  // التصنيف المحدد
    val isMixerOpen: Boolean,              // حالة الخلاط
    val favoriteIds: Set<String>           // الأصوات المفضلة
)
```

---

### 5. الشاشات

#### `HomeScreen.kt`
- صورة خلفية مع تدرج
- أزرار سريعة (مؤقت + خلاط)
- شريط التشغيل الحالي
- شبكة التصنيفات (3×3)
- قائمة أفقية للأصوات المقترحة

#### `CategoryScreen.kt`
- عرض أصوات التصنيف في قائمة
- كل عنصر يعرض: أيقونة، اسم، وصف، تردد
- تحكم بالصوت لكل عنصر
- إضافة للمفضلة

#### `TimerScreen.kt`
- عرض دائري متحرك للوقت
- شبكة خيارات المؤقت (3×4)
- معلومات عن التلاشي التدريجي

#### `MixerScreen.kt`
- تحكم بالصوت الرئيسي
- قائمة الأصوات النشطة مع شرائح تحكم
- إزالة أصوات فردية

#### `FavoritesScreen.kt`
- قائمة الأصوات المفضلة
- حالة فارغة جميلة

#### `SettingsScreen.kt`
- معلومات التطبيق
- إحصائيات
- قائمة المميزات
- دليل الترددات

---

### 6. الثيم

#### `Color.kt`
ألوان مخصصة لنظام ليلي/نهاري + ألوان التصنيفات.

#### `Theme.kt`
ثيم Material 3 مع:
- `DarkColorScheme` - نظام ألوان ليلي (افتراضي)
- `LightColorScheme` - نظام ألوان نهاري
- `SleepTypography` - خطوط مخصصة

---

## المعمارية

```
UI Layer (Compose Screens)
    ↕ StateFlow
ViewModel Layer (SoundViewModel)
    ↕ Direct calls
Data Layer (AudioEngine + SoundLibrary)
    ↕ AudioTrack API
Android Audio System
```

## الأداء

- كل صوت يعمل في Thread مستقل
- حجم Buffer: 4096 عينة لتقليل التأخير
- معدل العينات: 44100 Hz (جودة CD)
- الخيوط daemon لمنع تسرب الموارد
