package com.example.gameshub.presentation.games.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gameshub.presentation.games.GenreFilter

@Composable
fun GenreSelector(
    selectedGenre: GenreFilter,
    availableGenres: List<GenreFilter>,
    onGenreSelected: (GenreFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        availableGenres.forEach { genre ->
            FilterChip(
                selected = genre.id == selectedGenre.id,
                label = genre.title,
                onClick = { onGenreSelected(genre) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

