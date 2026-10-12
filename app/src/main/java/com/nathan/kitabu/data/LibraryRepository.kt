package com.nathan.kitabu.data

import androidx.room.withTransaction
import com.nathan.kitabu.data.local.BookEntity
import com.nathan.kitabu.data.local.BookingEntity
import com.nathan.kitabu.data.local.BookingStatus
import com.nathan.kitabu.data.local.BookingWithBook
import com.nathan.kitabu.data.local.LibraryDatabase
import kotlinx.coroutines.flow.Flow

class LibraryRepository(private val database: LibraryDatabase) {

    private val dao = database.libraryDao()

    val allBooks: Flow<List<BookEntity>> = dao.getAllBooks()
    val activeBookings: Flow<List<BookingWithBook>> = dao.getActiveBookings()

    fun searchBooks(query: String): Flow<List<BookEntity>> = dao.searchBooks(query.trim())

    suspend fun reserveBook(bookId: Int, userName: String, days: Int): Result<Unit> {
        val cleanName = userName.trim().replace(Regex("\\s+"), " ")
        if (cleanName.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your name"))
        }
        if (cleanName.length > MAX_NAME_LENGTH) {
            return Result.failure(IllegalArgumentException("Name is too long"))
        }
        if (days !in 1..MAX_RENTAL_DAYS) {
            return Result.failure(IllegalArgumentException("Choose a rental duration"))
        }

        val now = System.currentTimeMillis()
        database.withTransaction {
            dao.insertBooking(
                BookingEntity(
                    bookOwnerId = bookId,
                    userName = cleanName,
                    bookingDate = now,
                    returnDeadline = now + days * DAY_MS,
                    status = BookingStatus.PENDING
                )
            )
            dao.setBookAvailability(bookId, false)
        }
        return Result.success(Unit)
    }

    suspend fun collectBooking(booking: BookingWithBook) {
        dao.updateBookingStatus(booking.bookingId, BookingStatus.ACTIVE)
    }

    suspend fun renewBooking(booking: BookingWithBook, extraDays: Int): Result<Unit> {
        if (booking.status != BookingStatus.ACTIVE) {
            return Result.failure(IllegalStateException("Only active rentals can be renewed"))
        }
        if (extraDays < 1) {
            return Result.failure(IllegalArgumentException("Renewal must be at least 1 day"))
        }
        dao.updateReturnDeadline(booking.bookingId, booking.returnDeadline + extraDays * DAY_MS)
        return Result.success(Unit)
    }

    suspend fun returnBooking(booking: BookingWithBook) {
        database.withTransaction {
            dao.updateBookingStatus(booking.bookingId, BookingStatus.RETURNED)
            dao.setBookAvailability(booking.bookOwnerId, true)
        }
    }

    suspend fun cancelBooking(booking: BookingWithBook): Result<Unit> {
        if (booking.status != BookingStatus.PENDING) {
            return Result.failure(IllegalStateException("Only pending reservations can be cancelled"))
        }
        database.withTransaction {
            dao.deleteBooking(booking.bookingId)
            dao.setBookAvailability(booking.bookOwnerId, true)
        }
        return Result.success(Unit)
    }

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
        const val MAX_NAME_LENGTH = 40
        const val MAX_RENTAL_DAYS = 30
    }
}