package com.nathan.kitabu.di

import android.content.Context
import com.nathan.kitabu.data.LibraryRepository
import com.nathan.kitabu.data.local.LibraryDatabase

class AppContainer(context: Context) {
    private val database = LibraryDatabase.getDatabase(context)
    val repository = LibraryRepository(database)
}