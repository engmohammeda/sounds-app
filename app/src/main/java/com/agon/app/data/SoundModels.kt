package com.agon.app.data

import androidx.compose.ui.graphics.Color
import com.agon.app.ui.theme.*

enum class SoundCategory(
    val displayName: String,
    val icon: String,
    val color: Color,
    val description: String
) {
    SLEEP("النوم", "bedtime", SleepColor, "أصوات هادئة تساعدك على الاسترخاء والنوم العميق"),
    FOCUS("التركيز", "center_focus_strong", FocusColor, "ترددات وأصوات تعزز التركيز والانتباه"),
    WORK("العمل", "work", WorkColor, "أصوات خلفية مثالية لبيئة العمل"),
    CALM("الهدوء", "spa", CalmColor, "أصوات مهدئة للاسترخاء وتخفيف التوتر"),
    NATURE("الطبيعة", "forest", NatureColor, "أصوات طبيعية من الغابات والمحيطات"),
    MEDITATION("التأمل", "self_improvement", MeditationColor, "ترددات وأصوات للتأمل العميق"),
    BINAURAL("ترددات ثنائية", "hearing", BinauralColor, "ترددات بينورال لتحفيز موجات الدماغ"),
    WHITE_NOISE("ضوضاء بيضاء", "graphic_eq", WhiteNoiseColor, "أنواع مختلفة من الضوضاء المحيطة"),
    AMBIENT("محيطي", "surround_sound", AmbientColor, "أصوات محيطية وفضائية")
}

enum class WaveType {
    SINE, SQUARE, TRIANGLE, SAWTOOTH, NOISE_WHITE, NOISE_PINK, NOISE_BROWN,
    BINAURAL, ISOCHRONIC, PULSE, HARMONIC, CHOIR, WIND, RAIN, OCEAN,
    THUNDER, CRICKETS, FIRE, STREAM, BIRDS, BELL, BOWL, DRONE, SWEEP,
    FM_SYNTH, AM_SYNTH, FILTERED_NOISE, HEARTBEAT
}

data class SoundItem(
    val id: String,
    val name: String,
    val nameEn: String,
    val category: SoundCategory,
    val waveType: WaveType,
    val baseFrequency: Float,
    val secondaryFrequency: Float = 0f,
    val description: String,
    val icon: String,
    val volume: Float = 0.7f,
    val modulationRate: Float = 0f,
    val modulationDepth: Float = 0f,
    val filterCutoff: Float = 0f,
    val harmonics: Int = 1,
    val attackMs: Int = 500,
    val releaseMs: Int = 500
)

data class ActiveSound(
    val sound: SoundItem,
    var volume: Float = 0.7f,
    var isPlaying: Boolean = true
)

data class TimerPreset(
    val label: String,
    val minutes: Int,
    val icon: String
)

object SoundLibrary {
    val timerPresets = listOf(
        TimerPreset("5 دقائق", 5, "timer"),
        TimerPreset("10 دقائق", 10, "timer"),
        TimerPreset("15 دقيقة", 15, "timer"),
        TimerPreset("20 دقيقة", 20, "timer"),
        TimerPreset("30 دقيقة", 30, "timer"),
        TimerPreset("45 دقيقة", 45, "timer"),
        TimerPreset("60 دقيقة", 60, "timer"),
        TimerPreset("90 دقيقة", 90, "timer"),
        TimerPreset("ساعتان", 120, "timer"),
        TimerPreset("3 ساعات", 180, "timer"),
        TimerPreset("بلا حدود", 0, "all_inclusive")
    )

    val allSounds: List<SoundItem> = buildList {
        // ============ SLEEP SOUNDS ============
        add(SoundItem("sleep_delta_1", "موجات دلتا 1Hz", "Delta 1Hz", SoundCategory.SLEEP, WaveType.BINAURAL, 100f, 101f, "تردد دلتا للنوم العميق جداً", "waves"))
        add(SoundItem("sleep_delta_2", "موجات دلتا 2Hz", "Delta 2Hz", SoundCategory.SLEEP, WaveType.BINAURAL, 150f, 152f, "تردد دلتا لتعزيز النوم", "waves"))
        add(SoundItem("sleep_delta_3", "موجات دلتا 3Hz", "Delta 3Hz", SoundCategory.SLEEP, WaveType.BINAURAL, 200f, 203f, "موجات دلتا للاسترخاء العميق", "waves"))
        add(SoundItem("sleep_delta_05", "دلتا عميق 0.5Hz", "Deep Delta 0.5Hz", SoundCategory.SLEEP, WaveType.BINAURAL, 120f, 120.5f, "أعمق مستوى من النوم", "hotel_class"))
        add(SoundItem("sleep_lullaby", "تهويدة هادئة", "Gentle Lullaby", SoundCategory.SLEEP, WaveType.FM_SYNTH, 262f, 0f, "لحن هادئ كتهويدة نوم", "music_note", modulationRate = 0.5f, modulationDepth = 50f))
        add(SoundItem("sleep_drone", "درون النوم", "Sleep Drone", SoundCategory.SLEEP, WaveType.DRONE, 55f, 0f, "صوت درون عميق ومستمر", "surround_sound", harmonics = 5))
        add(SoundItem("sleep_heartbeat", "نبضات القلب", "Heartbeat", SoundCategory.SLEEP, WaveType.HEARTBEAT, 1.0f, 0f, "صوت نبضات قلب مهدئ", "favorite"))
        add(SoundItem("sleep_womb", "أصوات الرحم", "Womb Sounds", SoundCategory.SLEEP, WaveType.FILTERED_NOISE, 0f, 0f, "محاكاة أصوات داخل الرحم", "child_care", filterCutoff = 200f))
        add(SoundItem("sleep_sweep", "مسح ترددي", "Frequency Sweep", SoundCategory.SLEEP, WaveType.SWEEP, 200f, 80f, "انتقال سلس بين الترددات", "trending_down", modulationRate = 0.02f))
        add(SoundItem("sleep_soft_pad", "وسادة صوتية", "Soft Pad", SoundCategory.SLEEP, WaveType.HARMONIC, 130.81f, 0f, "صوت ناعم متعدد التوافقيات", "cloud", harmonics = 8))

        // ============ FOCUS SOUNDS ============
        add(SoundItem("focus_beta_14", "موجات بيتا 14Hz", "Beta 14Hz", SoundCategory.FOCUS, WaveType.BINAURAL, 200f, 214f, "تعزيز التركيز والانتباه", "psychology"))
        add(SoundItem("focus_beta_18", "موجات بيتا 18Hz", "Beta 18Hz", SoundCategory.FOCUS, WaveType.BINAURAL, 250f, 268f, "تحسين الأداء الذهني", "psychology"))
        add(SoundItem("focus_beta_20", "بيتا عالي 20Hz", "High Beta 20Hz", SoundCategory.FOCUS, WaveType.BINAURAL, 300f, 320f, "أقصى تركيز وانتباه", "bolt"))
        add(SoundItem("focus_gamma_40", "موجات غاما 40Hz", "Gamma 40Hz", SoundCategory.FOCUS, WaveType.BINAURAL, 400f, 440f, "تعزيز الوعي والإدراك", "lightbulb"))
        add(SoundItem("focus_iso_10", "نبضات إيزوكرونيك 10Hz", "Isochronic 10Hz", SoundCategory.FOCUS, WaveType.ISOCHRONIC, 200f, 0f, "نبضات منتظمة للتركيز", "graphic_eq", modulationRate = 10f))
        add(SoundItem("focus_iso_12", "نبضات إيزوكرونيك 12Hz", "Isochronic 12Hz", SoundCategory.FOCUS, WaveType.ISOCHRONIC, 250f, 0f, "نبضات ألفا عالية للتعلم", "school", modulationRate = 12f))
        add(SoundItem("focus_coffee", "ضجيج المقهى", "Coffee Shop", SoundCategory.FOCUS, WaveType.FILTERED_NOISE, 0f, 0f, "محاكاة أجواء مقهى هادئ", "local_cafe", filterCutoff = 800f))
        add(SoundItem("focus_pink", "ضوضاء وردية للتركيز", "Focus Pink Noise", SoundCategory.FOCUS, WaveType.NOISE_PINK, 0f, 0f, "ضوضاء وردية لتعزيز التركيز", "graphic_eq"))

        // ============ WORK SOUNDS ============
        add(SoundItem("work_alpha_10", "موجات ألفا 10Hz", "Alpha 10Hz", SoundCategory.WORK, WaveType.BINAURAL, 200f, 210f, "تردد مثالي للعمل الإبداعي", "palette"))
        add(SoundItem("work_alpha_8", "موجات ألفا 8Hz", "Alpha 8Hz", SoundCategory.WORK, WaveType.BINAURAL, 180f, 188f, "استرخاء واعي أثناء العمل", "work"))
        add(SoundItem("work_alpha_12", "ألفا نشط 12Hz", "Active Alpha 12Hz", SoundCategory.WORK, WaveType.BINAURAL, 220f, 232f, "حالة تدفق للعمل المنتج", "trending_up"))
        add(SoundItem("work_white", "ضوضاء بيضاء للعمل", "Work White Noise", SoundCategory.WORK, WaveType.NOISE_WHITE, 0f, 0f, "ضوضاء بيضاء لحجب الإزعاج", "noise_aware"))
        add(SoundItem("work_brown", "ضوضاء بنية للعمل", "Work Brown Noise", SoundCategory.WORK, WaveType.NOISE_BROWN, 0f, 0f, "ضوضاء بنية عميقة ومريحة", "blur_on"))
        add(SoundItem("work_typing", "صوت الكتابة", "Typing Sound", SoundCategory.WORK, WaveType.PULSE, 800f, 0f, "محاكاة صوت لوحة المفاتيح", "keyboard", modulationRate = 6f))
        add(SoundItem("work_office", "أجواء المكتب", "Office Ambience", SoundCategory.WORK, WaveType.FILTERED_NOISE, 0f, 0f, "ضوضاء خلفية مكتبية", "business", filterCutoff = 1200f))

        // ============ CALM SOUNDS ============
        add(SoundItem("calm_theta_6", "موجات ثيتا 6Hz", "Theta 6Hz", SoundCategory.CALM, WaveType.BINAURAL, 150f, 156f, "استرخاء عميق وتأمل", "spa"))
        add(SoundItem("calm_theta_4", "موجات ثيتا 4Hz", "Theta 4Hz", SoundCategory.CALM, WaveType.BINAURAL, 180f, 184f, "حالة هدوء عميقة", "self_improvement"))
        add(SoundItem("calm_theta_7", "ثيتا مريح 7Hz", "Relaxing Theta 7Hz", SoundCategory.CALM, WaveType.BINAURAL, 200f, 207f, "تخفيف التوتر والقلق", "mood"))
        add(SoundItem("calm_rain_light", "مطر خفيف", "Light Rain", SoundCategory.CALM, WaveType.RAIN, 0f, 0f, "صوت مطر خفيف ومهدئ", "water_drop"))
        add(SoundItem("calm_wind", "نسيم هادئ", "Gentle Wind", SoundCategory.CALM, WaveType.WIND, 0f, 0f, "صوت رياح خفيفة ومريحة", "air"))
        add(SoundItem("calm_singing_bowl", "وعاء تبتي", "Tibetan Bowl", SoundCategory.CALM, WaveType.BELL, 256f, 0f, "صوت وعاء غنائي تبتي", "notifications", harmonics = 6, attackMs = 10, releaseMs = 3000))
        add(SoundItem("calm_chime", "أجراس الرياح", "Wind Chimes", SoundCategory.CALM, WaveType.BELL, 523f, 0f, "صوت أجراس رياح هادئة", "notifications_active", harmonics = 4, attackMs = 5, releaseMs = 2000))
        add(SoundItem("calm_purr", "خرخرة قطة", "Cat Purr", SoundCategory.CALM, WaveType.FM_SYNTH, 26f, 0f, "محاكاة صوت خرخرة قطة", "pets", modulationRate = 26f, modulationDepth = 15f))

        // ============ NATURE SOUNDS ============
        add(SoundItem("nature_ocean", "أمواج المحيط", "Ocean Waves", SoundCategory.NATURE, WaveType.OCEAN, 0f, 0f, "صوت أمواج البحر المتكسرة", "waves"))
        add(SoundItem("nature_rain_heavy", "مطر غزير", "Heavy Rain", SoundCategory.NATURE, WaveType.RAIN, 0f, 0f, "صوت مطر غزير ومستمر", "thunderstorm", filterCutoff = 3000f))
        add(SoundItem("nature_thunder", "رعد بعيد", "Distant Thunder", SoundCategory.NATURE, WaveType.THUNDER, 40f, 0f, "صوت رعد بعيد مع مطر", "flash_on"))
        add(SoundItem("nature_forest", "غابة استوائية", "Tropical Forest", SoundCategory.NATURE, WaveType.CRICKETS, 0f, 0f, "أصوات حشرات وطيور الغابة", "forest"))
        add(SoundItem("nature_stream", "جدول مائي", "Mountain Stream", SoundCategory.NATURE, WaveType.STREAM, 0f, 0f, "صوت جدول ماء جبلي", "water"))
        add(SoundItem("nature_birds", "تغريد الطيور", "Bird Songs", SoundCategory.NATURE, WaveType.BIRDS, 0f, 0f, "أصوات طيور صباحية", "flutter"))
        add(SoundItem("nature_fire", "نار المخيم", "Campfire", SoundCategory.NATURE, WaveType.FIRE, 0f, 0f, "صوت طقطقة نار المخيم", "local_fire_department"))
        add(SoundItem("nature_wind_strong", "رياح قوية", "Strong Wind", SoundCategory.NATURE, WaveType.WIND, 0f, 0f, "صوت رياح قوية عبر الأشجار", "air", filterCutoff = 500f))
        add(SoundItem("nature_night", "ليل هادئ", "Quiet Night", SoundCategory.NATURE, WaveType.CRICKETS, 0f, 0f, "أصوات الليل الهادئة مع صراصير", "dark_mode", filterCutoff = 100f))

        // ============ MEDITATION SOUNDS ============
        add(SoundItem("med_om", "تردد أوم 136.1Hz", "Om 136.1Hz", SoundCategory.MEDITATION, WaveType.HARMONIC, 136.1f, 0f, "تردد أوم الكوني للتأمل", "self_improvement", harmonics = 7))
        add(SoundItem("med_crown", "شاكرا التاج 963Hz", "Crown 963Hz", SoundCategory.MEDITATION, WaveType.SINE, 963f, 0f, "تردد شاكرا التاج", "light_mode"))
        add(SoundItem("med_third_eye", "العين الثالثة 852Hz", "Third Eye 852Hz", SoundCategory.MEDITATION, WaveType.SINE, 852f, 0f, "تردد شاكرا العين الثالثة", "visibility"))
        add(SoundItem("med_throat", "شاكرا الحلق 741Hz", "Throat 741Hz", SoundCategory.MEDITATION, WaveType.SINE, 741f, 0f, "تردد شاكرا الحلق", "record_voice_over"))
        add(SoundItem("med_heart", "شاكرا القلب 639Hz", "Heart 639Hz", SoundCategory.MEDITATION, WaveType.SINE, 639f, 0f, "تردد شاكرا القلب", "favorite"))
        add(SoundItem("med_solar", "الضفيرة الشمسية 528Hz", "Solar Plexus 528Hz", SoundCategory.MEDITATION, WaveType.SINE, 528f, 0f, "تردد الحب والشفاء 528Hz", "wb_sunny"))
        add(SoundItem("med_sacral", "شاكرا العجز 417Hz", "Sacral 417Hz", SoundCategory.MEDITATION, WaveType.SINE, 417f, 0f, "تردد التغيير والتحول", "change_circle"))
        add(SoundItem("med_root", "شاكرا الجذر 396Hz", "Root 396Hz", SoundCategory.MEDITATION, WaveType.SINE, 396f, 0f, "تردد التحرر من الخوف", "shield"))
        add(SoundItem("med_schumann", "تردد شومان 7.83Hz", "Schumann 7.83Hz", SoundCategory.MEDITATION, WaveType.BINAURAL, 200f, 207.83f, "تردد الأرض الطبيعي", "public"))
        add(SoundItem("med_bowl_c", "وعاء كريستالي C", "Crystal Bowl C", SoundCategory.MEDITATION, WaveType.BELL, 261.63f, 0f, "وعاء كريستالي نوتة دو", "music_note", harmonics = 8, attackMs = 10, releaseMs = 4000))
        add(SoundItem("med_choir", "كورال تأملي", "Meditation Choir", SoundCategory.MEDITATION, WaveType.CHOIR, 220f, 0f, "أصوات كورال تأملية", "groups", harmonics = 5))

        // ============ BINAURAL BEATS ============
        add(SoundItem("bin_delta_1", "دلتا 1Hz (نوم عميق)", "Delta 1Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 100f, 101f, "أعمق مستوى من الاسترخاء", "airline_seat_flat"))
        add(SoundItem("bin_delta_2", "دلتا 2Hz (شفاء)", "Delta 2Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 130f, 132f, "تعزيز الشفاء الجسدي", "healing"))
        add(SoundItem("bin_delta_3", "دلتا 3Hz (تجديد)", "Delta 3Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 160f, 163f, "تجديد الخلايا", "autorenew"))
        add(SoundItem("bin_theta_4", "ثيتا 4Hz (إبداع)", "Theta 4Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 200f, 204f, "تعزيز الإبداع", "palette"))
        add(SoundItem("bin_theta_6", "ثيتا 6Hz (تأمل)", "Theta 6Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 220f, 226f, "تأمل عميق", "self_improvement"))
        add(SoundItem("bin_alpha_8", "ألفا 8Hz (استرخاء)", "Alpha 8Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 250f, 258f, "استرخاء واعي", "spa"))
        add(SoundItem("bin_alpha_10", "ألفا 10Hz (تعلم)", "Alpha 10Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 280f, 290f, "تحسين التعلم", "school"))
        add(SoundItem("bin_alpha_12", "ألفا 12Hz (تدفق)", "Alpha 12Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 300f, 312f, "حالة التدفق", "stream"))
        add(SoundItem("bin_beta_15", "بيتا 15Hz (تركيز)", "Beta 15Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 330f, 345f, "تركيز حاد", "center_focus_strong"))
        add(SoundItem("bin_beta_20", "بيتا 20Hz (يقظة)", "Beta 20Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 350f, 370f, "يقظة وانتباه", "visibility"))
        add(SoundItem("bin_gamma_40", "غاما 40Hz (وعي)", "Gamma 40Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 400f, 440f, "وعي فائق", "lightbulb"))
        add(SoundItem("bin_gamma_50", "غاما 50Hz (إدراك)", "Gamma 50Hz", SoundCategory.BINAURAL, WaveType.BINAURAL, 420f, 470f, "إدراك فائق", "psychology"))

        // ============ WHITE NOISE VARIANTS ============
        add(SoundItem("wn_white", "ضوضاء بيضاء", "White Noise", SoundCategory.WHITE_NOISE, WaveType.NOISE_WHITE, 0f, 0f, "كل الترددات بقوة متساوية", "graphic_eq"))
        add(SoundItem("wn_pink", "ضوضاء وردية", "Pink Noise", SoundCategory.WHITE_NOISE, WaveType.NOISE_PINK, 0f, 0f, "ترددات متوازنة طبيعياً", "graphic_eq"))
        add(SoundItem("wn_brown", "ضوضاء بنية", "Brown Noise", SoundCategory.WHITE_NOISE, WaveType.NOISE_BROWN, 0f, 0f, "ترددات منخفضة عميقة", "graphic_eq"))
        add(SoundItem("wn_fan", "صوت المروحة", "Fan Sound", SoundCategory.WHITE_NOISE, WaveType.FILTERED_NOISE, 0f, 0f, "محاكاة صوت مروحة كهربائية", "mode_fan", filterCutoff = 400f))
        add(SoundItem("wn_ac", "مكيف الهواء", "Air Conditioner", SoundCategory.WHITE_NOISE, WaveType.FILTERED_NOISE, 0f, 0f, "صوت مكيف هواء مستمر", "ac_unit", filterCutoff = 600f))
        add(SoundItem("wn_vacuum", "مكنسة كهربائية", "Vacuum Cleaner", SoundCategory.WHITE_NOISE, WaveType.FILTERED_NOISE, 0f, 0f, "صوت مكنسة كهربائية بعيدة", "cleaning_services", filterCutoff = 1500f))
        add(SoundItem("wn_dryer", "مجفف الملابس", "Clothes Dryer", SoundCategory.WHITE_NOISE, WaveType.FILTERED_NOISE, 0f, 0f, "صوت مجفف ملابس مستمر", "local_laundry_service", filterCutoff = 350f, modulationRate = 0.8f, modulationDepth = 0.1f))
        add(SoundItem("wn_static", "تشويش تلفزيون", "TV Static", SoundCategory.WHITE_NOISE, WaveType.NOISE_WHITE, 0f, 0f, "تشويش تلفزيون كلاسيكي", "tv", filterCutoff = 5000f))

        // ============ AMBIENT SOUNDS ============
        add(SoundItem("amb_space", "فضاء كوني", "Cosmic Space", SoundCategory.AMBIENT, WaveType.DRONE, 40f, 0f, "أصوات فضائية محيطية", "rocket", harmonics = 10, modulationRate = 0.05f, modulationDepth = 5f))
        add(SoundItem("amb_underwater", "تحت الماء", "Underwater", SoundCategory.AMBIENT, WaveType.FILTERED_NOISE, 0f, 0f, "أصوات تحت سطح الماء", "scuba_diving", filterCutoff = 300f, modulationRate = 0.2f, modulationDepth = 100f))
        add(SoundItem("amb_crystal", "كهف كريستالي", "Crystal Cave", SoundCategory.AMBIENT, WaveType.FM_SYNTH, 440f, 0f, "أصوات كهف كريستالي سحري", "diamond", modulationRate = 2f, modulationDepth = 200f))
        add(SoundItem("amb_aurora", "شفق قطبي", "Aurora Borealis", SoundCategory.AMBIENT, WaveType.SWEEP, 80f, 300f, "أصوات الشفق القطبي", "auto_awesome", modulationRate = 0.01f))
        add(SoundItem("amb_temple", "معبد قديم", "Ancient Temple", SoundCategory.AMBIENT, WaveType.HARMONIC, 110f, 0f, "أصوات معبد قديم مع صدى", "temple_hindu", harmonics = 12))
        add(SoundItem("amb_train", "قطار ليلي", "Night Train", SoundCategory.AMBIENT, WaveType.FILTERED_NOISE, 0f, 0f, "صوت قطار يسير في الليل", "train", filterCutoff = 250f, modulationRate = 1.5f, modulationDepth = 0.15f))
        add(SoundItem("amb_city_rain", "مدينة ممطرة", "Rainy City", SoundCategory.AMBIENT, WaveType.RAIN, 0f, 0f, "صوت مطر في مدينة ليلية", "location_city", filterCutoff = 2000f))
        add(SoundItem("amb_tibetan", "دير تبتي", "Tibetan Monastery", SoundCategory.AMBIENT, WaveType.CHOIR, 130.81f, 0f, "أصوات ترتيل من دير تبتي", "temple_buddhist", harmonics = 6))
    }

    fun getSoundsByCategory(category: SoundCategory): List<SoundItem> {
        return allSounds.filter { it.category == category }
    }
}
