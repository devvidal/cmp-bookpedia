package com.plcoding.bookpedia.book.presentation.book_detail

import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.core.presentation.UiText

data class BookDetailState(
    val book: Book? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true
)