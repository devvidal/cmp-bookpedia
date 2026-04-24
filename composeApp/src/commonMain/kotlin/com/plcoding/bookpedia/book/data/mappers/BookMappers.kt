package com.plcoding.bookpedia.book.data.mappers

import com.plcoding.bookpedia.book.data.database.BookEntity
import com.plcoding.bookpedia.book.data.dto.SearchedBookDto
import com.plcoding.bookpedia.book.domain.Book

fun SearchedBookDto.toDomain(): Book {
    val imageUrlPrefix = if (coverKey != null) "olid/$coverKey" else "id/$coverAlternativeKey"

    return Book(
        id = id.substringAfterLast("/"),
        title = title,
        imageUrl = "https://covers.openlibrary.org/b/$imageUrlPrefix-L.jpg",
        authors = authorNames ?: emptyList(),
        description = null,
        languages = languages ?: emptyList(),
        firstPublishYear = firstPublishYear?.toString(),
        averageRating = ratingAverage,
        ratingsCount = ratingCount,
        numPages = numPagesMedian,
        numEditions = numEditions
    )
}

fun Book.toBookEntity(): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        languages = languages,
        authors = authors,
        firstPublishYear = firstPublishYear,
        ratingsAverage = averageRating,
        ratingsCount = ratingsCount,
        numberOfPagesMedian = numPages,
        numEdition = numEditions
    )
}

fun BookEntity.toBook(): Book {
    return Book(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        languages = languages,
        authors = authors,
        firstPublishYear = firstPublishYear,
        averageRating = ratingsAverage,
        ratingsCount = ratingsCount,
        numPages = numberOfPagesMedian,
        numEditions = numEdition
    )
}