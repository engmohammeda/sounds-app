package com.agon.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.R
import com.agon.app.data.SoundCategory
import com.agon.app.data.SoundLibrary
import com.agon.app.viewmodel.SoundUiState
import com.agon.app.viewmodel.SoundViewModel
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SoundViewModel,
    onNavigateToCategory: (SoundCategory) -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToMixer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.header_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "مرحباً بك في",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "عالم الأصوات والترددات",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Quick Actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "مؤقت النوم",
                    icon = Icons.Filled.Timer,
                    color = MaterialTheme.colorScheme.tertiary,
                    subtitle = if (uiState.isTimerRunning) formatTime(uiState.timerRemainingSeconds) else "اضبط المؤقت",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTimer
                )
                QuickActionCard(
                    title = "الخلاط",
                    icon = Icons.Filled.Tune,
                    color = MaterialTheme.colorScheme.primary,
                    subtitle = if (uiState.activeSounds.isNotEmpty()) "${uiState.activeSounds.size} صوت" else "امزج الأصوات",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToMixer
                )
            }
        }

        // Now Playing Mini
        if (uiState.isAnyPlaying) {
            item {
                NowPlayingMini(
                    activeSoundsCount = uiState.activeSounds.size,
                    isTimerRunning = uiState.isTimerRunning,
                    timerRemaining = uiState.timerRemainingSeconds,
                    onStopAll = { viewModel.stopAll() },
                    onOpenMixer = onNavigateToMixer
                )
            }
        }

        // Section Title
        item {
            Text(
                text = "مكتبة الأصوات",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Categories Grid
        item {
            CategoryGrid(
                categories = SoundCategory.entries.toList(),
                onCategoryClick = onNavigateToCategory
            )
        }

        // Popular Sounds
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "أصوات مقترحة",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val featured = listOf(
                    SoundLibrary.allSounds.first { it.id == "nature_ocean" },
                    SoundLibrary.allSounds.first { it.id == "sleep_delta_2" },
                    SoundLibrary.allSounds.first { it.id == "med_solar" },
                    SoundLibrary.allSounds.first { it.id == "calm_rain_light" },
                    SoundLibrary.allSounds.first { it.id == "wn_brown" },
                    SoundLibrary.allSounds.first { it.id == "focus_beta_14" },
                    SoundLibrary.allSounds.first { it.id == "amb_space" },
                )
                items(featured) { sound ->
                    val isPlaying = uiState.activeSounds.containsKey(sound.id)
                    FeaturedSoundCard(
                        name = sound.name,
                        description = sound.description,
                        categoryColor = sound.category.color,
                        categoryName = sound.category.displayName,
                        isPlaying = isPlaying,
                        onClick = { viewModel.toggleSound(sound) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = color
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NowPlayingMini(
    activeSoundsCount: Int,
    isTimerRunning: Boolean,
    timerRemaining: Long,
    onStopAll: () -> Unit,
    onOpenMixer: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.GraphicEq,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "تشغيل الآن • $activeSoundsCount صوت",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (isTimerRunning) {
                    Text(
                        text = "المؤقت: ${formatTime(timerRemaining)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            IconButton(onClick = onOpenMixer) {
                Icon(Icons.Filled.Tune, contentDescription = "الخلاط", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onStopAll) {
                Icon(Icons.Filled.Stop, contentDescription = "إيقاف", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun CategoryGrid(
    categories: List<SoundCategory>,
    onCategoryClick: (SoundCategory) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { category ->
                    CategoryCard(
                        category = category,
                        soundCount = SoundLibrary.getSoundsByCategory(category).size,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(category) }
                    )
                }
                // Fill remaining space if row is not complete
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: SoundCategory,
    soundCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
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

    Card(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$soundCount صوت",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FeaturedSoundCard(
    name: String,
    description: String,
    categoryColor: Color,
    categoryName: String,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .height(130.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying)
                categoryColor.copy(alpha = 0.25f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isPlaying) BorderStroke(1.dp, categoryColor.copy(alpha = 0.5f)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = categoryColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                if (isPlaying) {
                    Icon(
                        Icons.Filled.PlayCircle,
                        contentDescription = null,
                        tint = categoryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp
                )
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format("%d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}
