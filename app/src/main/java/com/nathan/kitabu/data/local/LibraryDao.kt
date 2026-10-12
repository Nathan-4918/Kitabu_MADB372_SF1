package com.nathan.kitabu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert
    suspend fun insertBooking(booking: BookingEntity): Long

    // READ
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isAvailable = :available ORDER BY title ASC")
    fun getBooksByAvailability(available: Boolean): Flow<List<BookEntity>>

    @Query(
        "SELECT * FROM books " +
                "WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' " +
                "ORDER BY title ASC"
    )
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query(
        "SELECT b.bookingId, b.bookOwnerId, b.userName, b.bookingDate, " +
                "b.returnDeadline, b.status, k.title, k.author, k.category " +
                "FROM bookings b INNER JOIN books k ON b.bookOwnerId = k.bookId " +
                "WHERE b.status != 'RETURNED' " +
                "ORDER BY b.returnDeadline ASC"
    )
    fun getActiveBookings(): Flow<List<BookingWithBook>>

    // UPDATE
    @Query("UPDATE bookings SET returnDeadline = :newDeadline WHERE bookingId = :bookingId")
    suspend fun updateReturnDeadline(bookingId: Int, newDeadline: Long)

    @Query("UPDATE bookings SET status = :status WHERE bookingId = :bookingId")
    suspend fun updateBookingStatus(bookingId: Int, status: BookingStatus)

    @Query("UPDATE books SET isAvailable = :available WHERE bookId = :bookId")
    suspend fun setBookAvailability(bookId: Int, available: Boolean)

    // DELETE
    @Query("DELETE FROM bookings WHERE bookingId = :bookingId")
    suspend fun deleteBooking(bookingId: Int)
}