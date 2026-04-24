package com.plcoding.bookpedia.book.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBookDao {

    @Upsert
    suspend fun upsertBook(book: BookEntity)

    @Query("SELECT * FROM bookentity")
    fun getFavoriteBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM bookentity WHERE id = :bookId")
    suspend fun getFavoriteBookById(bookId: String): BookEntity?

    @Query("DELETE FROM bookentity WHERE id = :bookId")
    suspend fun deleteFavoriteBook(bookId: String)
}