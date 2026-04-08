package com.tidylauncher.autoorganizer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InstalledAppDao {
    @Query("SELECT * FROM installed_apps ORDER BY label COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps ORDER BY label COLLATE NOCASE ASC")
    suspend fun getAll(): List<InstalledAppEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(apps: List<InstalledAppEntity>)

    @Query("DELETE FROM installed_apps WHERE componentName NOT IN (:componentNames)")
    suspend fun deleteMissing(componentNames: List<String>)

    @Query("DELETE FROM installed_apps")
    suspend fun clear()
}

