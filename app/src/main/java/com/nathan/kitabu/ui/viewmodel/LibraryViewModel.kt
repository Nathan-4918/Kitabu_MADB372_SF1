package com.nathan.kitabu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nathan.kitabu.data.LibraryRepository
import com.nathan.kitabu.data.local.BookEntity
import com.nathan.kitabu.data.local.BookingWithBook
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<BookEntity>> = _searchQuery
        .flatMapLatest { query -> repository.searchBooks(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookings: StateFlow<List<BookingWithBook>> = repository.activeBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun reserve(bookId: Int, userName: String, days: Int) {
        viewModelScope.launch {
            repository.reserveBook(bookId, userName, days)
                .onSuccess { _message.value = "Book reserved" }
                .onFailure { _message.value = it.message }
        }
    }

    fun collect(booking: BookingWithBook) {
        viewModelScope.launch {
            repository.collectBooking(booking)
            _message.value = "Marked as collected"
        }
    }

    fun renew(booking: BookingWithBook, extraDays: Int = 7) {
        viewModelScope.launch {
            repository.renewBooking(booking, extraDays)
                .onSuccess { _message.value = "Renewed for $extraDays more days" }
                .onFailure { _message.value = it.message }
        }
    }

    fun returnBook(booking: BookingWithBook) {
        viewModelScope.launch {
            repository.returnBooking(booking)
            _message.value = "Book returned"
        }
    }

    fun cancel(booking: BookingWithBook) {
        viewModelScope.launch {
            repository.cancelBooking(booking)
                .onSuccess { _message.value = "Reservation cancelled" }
                .onFailure { _message.value = it.message }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}

class LibraryViewModelFactory(
    private val repository: LibraryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(repository) as T
    }
}