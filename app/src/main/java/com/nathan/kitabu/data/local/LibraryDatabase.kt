package com.nathan.kitabu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [BookEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LibraryDatabase : RoomDatabase() {

    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile
        private var INSTANCE: LibraryDatabase? = null

        fun getDatabase(context: Context): LibraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibraryDatabase::class.java,
                    "kitabu_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getDatabase(context).libraryDao().insertBooks(sampleBooks())
                            }
                        }
                    })
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

private fun sampleBooks(): List<BookEntity> = listOf(
    BookEntity(title = "Clean Code", author = "Robert C. Martin", category = "Programming"),
    BookEntity(title = "Introduction to Algorithms", author = "Thomas H. Cormen", category = "Computer Science"),
    BookEntity(title = "Database System Concepts", author = "Abraham Silberschatz", category = "Databases"),
    BookEntity(title = "Kotlin in Action", author = "Dmitry Jemerov", category = "Programming"),
    BookEntity(title = "Things Fall Apart", author = "Chinua Achebe", category = "Literature"),
    BookEntity(title = "Long Walk to Freedom", author = "Nelson Mandela", category = "Biography"),
    BookEntity(title = "Cry, the Beloved Country", author = "Alan Paton", category = "Literature"),
    BookEntity(title = "Operating System Concepts", author = "Abraham Silberschatz", category = "Computer Science"),
    BookEntity(title = "The Pragmatic Programmer", author = "Andrew Hunt", category = "Programming"),
    BookEntity(title = "Design Patterns", author = "Erich Gamma", category = "Programming")
)