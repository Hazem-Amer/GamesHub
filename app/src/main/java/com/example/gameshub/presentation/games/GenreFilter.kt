package com.example.gameshub.presentation.games

import com.example.gameshub.domain.model.Genre

data class GenreFilter(
    val title: String,
    val id: Int?
) {
    companion object {
        val All: GenreFilter = GenreFilter(title = "All", id = null)

        fun createFromGenre(genre: Genre): GenreFilter {
            return GenreFilter(title = genre.name, id = genre.id)
        }
    }
}

