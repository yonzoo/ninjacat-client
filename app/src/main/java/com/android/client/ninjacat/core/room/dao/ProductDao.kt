package com.android.client.ninjacat.core.room.dao

import androidx.room.*
import com.android.client.ninjacat.core.room.models.Product

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productData: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(productData: List<Product>)

    @Delete
    fun delete(productData: Product)

    @Update
    fun update(productData: Product): Int

    @Query("DELETE FROM Product")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(productData: Product) {
        deleteAll()
        insert(productData)
    }

    @Transaction
    fun deleteAllAndInsertAll(products: List<Product>) {
        deleteAll()
        insertAll(products)
    }

    @Transaction
    @Query("DELETE FROM Product WHERE id = :id")
    fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM Product WHERE id = :id")
    fun getProductById(id: Long): Product?

    @Transaction
    @Query("SELECT * FROM Product")
    fun getAll(): List<Product>
}