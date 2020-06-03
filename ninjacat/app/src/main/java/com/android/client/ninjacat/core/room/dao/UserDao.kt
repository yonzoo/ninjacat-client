package com.android.client.ninjacat.core.room.dao

import androidx.room.*
import com.android.client.ninjacat.core.room.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userData: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userData: List<User>)

    @Delete
    fun delete(userData: User)

    @Update
    fun update(userData: User): Int

    @Query("DELETE FROM User")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(userData: User) {
        deleteAll()
        insert(userData)
    }

    @Transaction
    @Query("DELETE FROM User WHERE id = :id")
    fun deleteById(id: String)

    @Transaction
    @Query("SELECT * FROM User WHERE id = :id")
    fun getUserById(id: String): User?

    @Transaction
    @Query("SELECT * FROM User")
    fun getAll(): List<User>
}