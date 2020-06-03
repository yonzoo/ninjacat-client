package com.android.client.ninjacat.core.room.dao

import androidx.room.*
import com.android.client.ninjacat.core.room.models.Expense
import com.android.client.ninjacat.core.room.models.Sale

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(expenseData: Expense)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(expenseData: List<Expense>)

    @Delete
    fun delete(expenseData: Expense)

    @Update
    fun update(expenseData: Expense): Int

    @Query("DELETE FROM Expense")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(expenseData: Expense) {
        deleteAll()
        insert(expenseData)
    }

    @Transaction
    fun deleteAllAndInsertAll(expenses: List<Expense>) {
        deleteAll()
        insertAll(expenses)
    }

    @Transaction
    @Query("DELETE FROM Expense WHERE id = :id")
    fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM Expense WHERE id = :id")
    fun getExpenseById(id: Long): Expense?

    @Transaction
    @Query("SELECT * FROM Expense")
    fun getAll(): List<Expense>
}