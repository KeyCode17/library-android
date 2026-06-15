package com.library.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** Room database for the on-device catalog cache (ADR 0002). */
@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
abstract class CatalogDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
