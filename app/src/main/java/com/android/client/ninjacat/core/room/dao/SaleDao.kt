package com.android.client.ninjacat.core.room.dao

import androidx.room.*
import com.android.client.ninjacat.core.room.models.Expense
import com.android.client.ninjacat.core.room.models.Sale

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(saleData: Sale)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(saleData: List<Sale>)

    @Delete
    fun delete(saleData: Sale)

    @Update
    fun update(saleData: Sale): Int

    @Query("DELETE FROM Sale")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(saleData: Sale) {
        deleteAll()
        insert(saleData)
    }

    @Transaction
    fun deleteAllAndInsertAll(sales: List<Sale>) {
        deleteAll()
        insertAll(sales)
    }

    @Transaction
    @Query("DELETE FROM Sale WHERE id = :id")
    fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM Sale WHERE id = :id")
    fun getSaleById(id: Long): Sale?

    @Transaction
    @Query("SELECT * FROM Sale")
    fun getAll(): List<Sale>
}