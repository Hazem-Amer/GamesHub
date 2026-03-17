package com.example.gameshub.presentation.games.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.gameshub.presentation.games.GenreFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreSelector(
    selectedGenre: GenreFilter,
    availableGenres: List<GenreFilter>,
    onGenreSelected: (GenreFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedGenre.title,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Genre") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            availableGenres.forEach { genre ->
                DropdownMenuItem(
                    text = { Text(text = genre.title) },
                    onClick = {
                        isExpanded = false
                        onGenreSelected(genre)
                    }
                )
            }
        }
    }
}

