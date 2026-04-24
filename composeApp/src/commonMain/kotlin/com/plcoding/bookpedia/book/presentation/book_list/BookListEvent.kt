package com.plcoding.bookpedia.book.presentation.book_list

import com.plcoding.bookpedia.book.domain.Book

sealed interface BookListEvent {

    data class RedirectToBookDetails(val book: Book): BookListEvent
}
