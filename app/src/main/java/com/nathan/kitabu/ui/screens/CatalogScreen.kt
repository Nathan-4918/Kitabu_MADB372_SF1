package com.nathan.kitabu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nathan.kitabu.data.local.BookEntity
import com.nathan.kitabu.ui.components.BookCard
import com.nathan.kitabu.ui.viewmodel.LibraryViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nathan.kitabu.ui.components.ReserveSheet

@Composable
fun CatalogScreen(
    viewModel: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }

    CatalogContent(
        books = books,
        query = query,
        onQueryChange = viewModel::onSearchChange,
        onBookClick = { book ->
            if (book.isAvailable) selectedBook = book
        },
        modifier = modifier
    )

    selectedBook?.let { book ->
        ReserveSheet(
            book = book,
            onDismiss = { selectedBook = null },
            onReserve = { name, days ->
                viewModel.reserve(book.bookId, name, days)
                selectedBook = null
            }
        )
    }
}

@Composable
fun CatalogContent(
    books: List<BookEntity>,
    query: String,
    onQueryChange: (String) -> Unit,
    onBookClick: (BookEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            placeholder = { Text("Search by title or author") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true
        )

        if (books.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No books found")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(books, key = { it.bookId }) { book ->
                    BookCard(book = book, onClick = { onBookClick(book) })
                }
            }
        }
    }
}