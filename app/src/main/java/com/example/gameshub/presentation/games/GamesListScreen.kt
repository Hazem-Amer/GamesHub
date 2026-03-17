package com.example.gameshub.presentation.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gameshub.presentation.games.components.EmptyStateContent
import com.example.gameshub.presentation.games.components.ErrorStateContent
import com.example.gameshub.presentation.games.components.GameCard
import com.example.gameshub.presentation.games.components.GenreSelector
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
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.paginationErrorMessage) {
        val message = state.paginationErrorMessage ?: return@LaunchedEffect
        viewModel.executeConsumePaginationError()
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = "Retry",
            duration = SnackbarDuration.Short
        )
        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
            viewModel.executeLoadNextPage()
        }
    }
    LaunchedEffect(listState, state.visibleGames.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { it != null }
            .map { it ?: 0 }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                val shouldLoadNextPage = lastVisibleIndex >= (state.visibleGames.size - 5)
                if (shouldLoadNextPage) {
                    viewModel.executeLoadNextPage()
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
                text = "GamesHub",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Discover and track amazing games",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                placeholder = { Text(text = "Search loaded games...") },
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
                        imageVector = Icons.Filled.GridView,
                        contentDescription = "List layout",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Showing ${state.visibleGames.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Filters",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                            "No results for \"${state.searchQuery.trim()}\""
                        } else {
                            "No games found."
                        },
                        onClearSearchClick = if (state.searchQuery.isNotBlank()) {
                            { viewModel.executeSearchQueryChange("") }
                        } else {
                            null
                        }
                    )
                }
                GamesUiState.PaginationLoading, GamesUiState.Success -> {
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
                                onClick = { onGameClick(game.id) }
                            )
                        }
                        if (state.uiState is GamesUiState.PaginationLoading) {
                            item(key = "pagination_loading") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Loading more…",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

