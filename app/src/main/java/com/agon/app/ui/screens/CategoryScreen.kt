package com.agon.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agon.app.data.SoundCategory
import com.agon.app.data.SoundItem
import com.agon.app.data.SoundLibrary
import com.agon.app.viewmodel.SoundViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    category: SoundCategory,
    viewModel: SoundViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sounds = SoundLibrary.getSoundsByCategory(category)

    val icon = when (category) {
        SoundCategory.SLEEP -> Icons.Filled.Bedtime
        SoundCategory.FOCUS -> Icons.Filled.CenterFocusStrong
        SoundCategory.WORK -> Icons.Filled.Work
        SoundCategory.CALM -> Icons.Filled.Spa
        SoundCategory.NATURE -> Icons.Filled.Forest
        SoundCategory.MEDITATION -> Icons.Filled.SelfImprovement
        SoundCategory.BINAURAL -> Icons.Filled.Hearing
        SoundCategory.WHITE_NOISE -> Icons.Filled.GraphicEq
        SoundCategory.AMBIENT -> Icons.Filled.SurroundSound
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = category.color,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(category.displayName)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Category Description
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = category.color.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = category.color,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "${sounds.size} صوت متاح",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(sounds) { sound ->
                val isPlaying = uiState.activeSounds.containsKey(sound.id)
                val isFavorite = uiState.favoriteIds.contains(sound.id)
                SoundListItem(
                    sound = sound,
                    isPlaying = isPlaying,
                    isFavorite = isFavorite,
                    volume = uiState.activeSounds[sound.id] ?: 0.7f,
                    onToggle = { viewModel.toggleSound(sound) },
                    onVolumeChange = { viewModel.setSoundVolume(sound.id, it) },
                    onFavoriteToggle = { viewModel.toggleFavorite(sound.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SoundListItem(
    sound: SoundItem,
    isPlaying: Boolean,
    isFavorite: Boolean,
    volume: Float,
    onToggle: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onFavoriteToggle: () -> Unit
) {
    var showVolume by remember { mutableStateOf(false) }

    val iconForSound = when (sound.icon) {
        "waves" -> Icons.Filled.Waves
        "hotel_class" -> Icons.Filled.HotelClass
        "music_note" -> Icons.Filled.MusicNote
        "surround_sound" -> Icons.Filled.SurroundSound
        "favorite" -> Icons.Filled.Favorite
        "child_care" -> Icons.Filled.ChildCare
        "trending_down" -> Icons.Filled.TrendingDown
        "cloud" -> Icons.Filled.Cloud
        "psychology" -> Icons.Filled.Psychology
        "bolt" -> Icons.Filled.Bolt
        "lightbulb" -> Icons.Filled.Lightbulb
        "graphic_eq" -> Icons.Filled.GraphicEq
        "school" -> Icons.Filled.School
        "local_cafe" -> Icons.Filled.LocalCafe
        "palette" -> Icons.Filled.Palette
        "work" -> Icons.Filled.Work
        "trending_up" -> Icons.Filled.TrendingUp
        "noise_aware" -> Icons.Filled.NoiseAware
        "blur_on" -> Icons.Filled.BlurOn
        "keyboard" -> Icons.Filled.Keyboard
        "business" -> Icons.Filled.Business
        "spa" -> Icons.Filled.Spa
        "self_improvement" -> Icons.Filled.SelfImprovement
        "mood" -> Icons.Filled.Mood
        "water_drop" -> Icons.Filled.WaterDrop
        "air" -> Icons.Filled.Air
        "notifications" -> Icons.Filled.Notifications
        "notifications_active" -> Icons.Filled.NotificationsActive
        "pets" -> Icons.Filled.Pets
        "thunderstorm" -> Icons.Filled.Thunderstorm
        "flash_on" -> Icons.Filled.FlashOn
        "forest" -> Icons.Filled.Forest
        "water" -> Icons.Filled.Water
        "flutter" -> Icons.Filled.FlutterDash
        "local_fire_department" -> Icons.Filled.LocalFireDepartment
        "dark_mode" -> Icons.Filled.DarkMode
        "light_mode" -> Icons.Filled.LightMode
        "visibility" -> Icons.Filled.Visibility
        "record_voice_over" -> Icons.Filled.RecordVoiceOver
        "wb_sunny" -> Icons.Filled.WbSunny
        "change_circle" -> Icons.Filled.ChangeCircle
        "shield" -> Icons.Filled.Shield
        "public" -> Icons.Filled.Public
        "groups" -> Icons.Filled.Groups
        "airline_seat_flat" -> Icons.Filled.AirlineSeatFlat
        "autorenew" -> Icons.Filled.Autorenew
        "stream" -> Icons.Filled.Stream
        "center_focus_strong" -> Icons.Filled.CenterFocusStrong
        "hearing" -> Icons.Filled.Hearing
        "mode_fan" -> Icons.Filled.ModeFanOff
        "ac_unit" -> Icons.Filled.AcUnit
        "cleaning_services" -> Icons.Filled.CleaningServices
        "local_laundry_service" -> Icons.Filled.LocalLaundryService
        "tv" -> Icons.Filled.Tv
        "rocket" -> Icons.Filled.RocketLaunch
        "scuba_diving" -> Icons.Filled.ScubaDiving
        "diamond" -> Icons.Filled.Diamond
        "auto_awesome" -> Icons.Filled.AutoAwesome
        "temple_hindu" -> Icons.Filled.TempleHindu
        "train" -> Icons.Filled.Train
        "location_city" -> Icons.Filled.LocationCity
        "temple_buddhist" -> Icons.Filled.TempleBuddhist
        else -> Icons.Filled.MusicNote
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying)
                sound.category.color.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isPlaying) BorderStroke(1.dp, sound.category.color.copy(alpha = 0.3f)) else null
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isPlaying) sound.category.color.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconForSound,
                        contentDescription = null,
                        tint = if (isPlaying) sound.category.color else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sound.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(
                        text = sound.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    if (sound.baseFrequency > 0) {
                        Text(
                            text = if (sound.secondaryFrequency > 0)
                                "${sound.baseFrequency.toInt()}Hz - ${sound.secondaryFrequency.toInt()}Hz"
                            else
                                "${sound.baseFrequency.toInt()}Hz",
                            style = MaterialTheme.typography.labelSmall,
                            color = sound.category.color.copy(alpha = 0.8f)
                        )
                    }
                }

                // Favorite
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Volume toggle
                if (isPlaying) {
                    IconButton(
                        onClick = { showVolume = !showVolume },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.VolumeUp,
                            contentDescription = null,
                            tint = sound.category.color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Play/Stop
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.StopCircle else Icons.Filled.PlayCircle,
                        contentDescription = if (isPlaying) "إيقاف" else "تشغيل",
                        tint = if (isPlaying) sound.category.color else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Volume Slider
            AnimatedVisibility(visible = isPlaying && showVolume) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.VolumeDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = volume,
                        onValueChange = onVolumeChange,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = sound.category.color,
                            activeTrackColor = sound.category.color
                        )
                    )
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
