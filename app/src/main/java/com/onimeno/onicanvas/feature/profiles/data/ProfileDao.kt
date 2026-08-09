package com.onimeno.onicanvas.feature.profiles.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY rowid ASC")
    fun observeAll(): Flow<List<ProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(profiles: List<ProfileEntity>)

    @Query("UPDATE profiles SET isActive = CASE WHEN id = :id THEN 1 ELSE 0 END")
    suspend fun setActive(id: String)

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun count(): Int
}
