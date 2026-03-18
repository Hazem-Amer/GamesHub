package com.example.gameshub.presentation.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gameshub.R
import com.example.gameshub.presentation.games.components.EmptyStateContent
import com.example.gameshub.presentation.games.components.ErrorStateContent
import com.example.gameshub.presentation.games.components.GameCard
import com.example.gameshub.presentation.games.components.GameCardVariant
import com.example.gameshub.presentation.games.components.GenreSelector
import com.example.gameshub.presentation.games.components.InlineLoadingRow
import com.example.gameshub.presentation.games.components.LoadingGameCard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    viewModel: GamesViewModel,
    onGameClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isGridModeState = rememberSaveable { mutableStateOf(false) }
    val isGridMode = isGridModeState.value
    val retryLabel = stringResource(id = R.string.action_retry)
    LaunchedEffect(state.paginationErrorMessage) {
        val message = state.paginationErrorMessage ?: return@LaunchedEffect
        viewModel.executeConsumePaginationError()
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = retryLabel,
            duration = SnackbarDuration.Short
        )
        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
            viewModel.executeLoadNextPage()
        }
    }
    LaunchedEffect(listState, state.visibleGames.size, isGridMode) {
        if (!isGridMode) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .filter { index -> index != null }
                .map { index -> index ?: 0 }
                .distinctUntilChanged()
                .collect { lastVisibleIndex ->
                    val shouldLoadNextPage = lastVisibleIndex >= (state.visibleGames.size - 5)
                    if (shouldLoadNextPage) {
                        viewModel.executeLoadNextPage()
                    }
                }
        }
    }
    LaunchedEffect(gridState, state.visibleGames.size, isGridMode) {
        if (isGridMode) {
            snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .filter { index -> index != null }
                .map { index -> index ?: 0 }
                .distinctUntilChanged()
                .collect { lastVisibleIndex ->
                    val shouldLoadNextPage = lastVisibleIndex >= (state.visibleGames.size - 5)
                    if (shouldLoadNextPage) {
                        viewModel.executeLoadNextPage()
                    }
                }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.games_list_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.games_list_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            GenreSelector(
                selectedGenre = state.selectedGenre,
                availableGenres = state.availableGenres,
                onGenreSelected = { genre -> viewModel.executeSelectGenre(genre) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { query -> viewModel.executeSearchQueryChange(query) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(id = R.string.games_list_search_placeholder)) },
                singleLine = true,
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isGridMode) Icons.Filled.ViewList else Icons.Filled.GridView,
                        contentDescription = if (isGridMode) {
                            stringResource(id = R.string.games_list_toggle_to_list)
                        } else {
                            stringResource(id = R.string.games_list_toggle_to_grid)
                        },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { isGridModeState.value = !isGridModeState.value }
                    )
                    Text(
                        text = stringResource(id = R.string.games_list_showing_count, state.visibleGames.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            when (val uiState = state.uiState) {
                is GamesUiState.Loading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(count = 6) {
                            LoadingGameCard()
                        }
                    }
                }
                is GamesUiState.Error -> {
                    ErrorStateContent(
                        message = uiState.message,
                        onRetryClick = { viewModel.executeRefresh() }
                    )
                }
                GamesUiState.Empty -> {
                    EmptyStateContent(
                        message = if (state.searchQuery.isNotBlank()) {
                            stringResource(
                                id = R.string.games_list_empty_query,
                                state.searchQuery.trim()
                            )
                        } else {
                            stringResource(id = R.string.games_list_empty_generic)
                        },
                        onClearSearchClick = if (state.searchQuery.isNotBlank()) {
                            { viewModel.executeSearchQueryChange("") }
                        } else {
                            null
                        }
                    )
                }
                GamesUiState.PaginationLoading, GamesUiState.Success -> {
                    if (!isGridMode) {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                count = state.visibleGames.size,
                                key = { index -> state.visibleGames[index].id }
                            ) { index ->
                                val game = state.visibleGames[index]
                                GameCard(
                                    game = game,
                                    onClick = { onGameClick(game.id) },
                                    variant = GameCardVariant.List
                                )
                            }
                            if (state.uiState is GamesUiState.PaginationLoading) {
                                item(key = "pagination_loading") {
                                    InlineLoadingRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.visibleGames) { game ->
                                GameCard(
                                    game = game,
                                    onClick = { onGameClick(game.id) },
                                    variant = GameCardVariant.Grid
                                )
                            }
                            if (state.uiState is GamesUiState.PaginationLoading) {
                                item {
                                    InlineLoadingRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

