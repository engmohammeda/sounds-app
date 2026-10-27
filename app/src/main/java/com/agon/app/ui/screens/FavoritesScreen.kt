package com.agon.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agon.app.data.SoundLibrary
import com.agon.app.viewmodel.SoundViewModel

@Composable
fun FavoritesScreen(
    viewModel: SoundViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val favSounds = SoundLibrary.allSounds.filter { it.id in uiState.favoriteIds }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "المفضلة",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )

        if (favSounds.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "لا توجد أصوات مفضلة",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "اضغط على ❤️ لإضافة أصوات إلى المفضلة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favSounds) { sound ->
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
}
