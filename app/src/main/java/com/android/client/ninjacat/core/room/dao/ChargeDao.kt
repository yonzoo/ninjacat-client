package com.android.client.ninjacat.core.room.dao

import androidx.room.*
import com.android.client.ninjacat.core.room.models.Charge
import com.android.client.ninjacat.core.room.models.Expense

@Dao
interface ChargeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chargeData: Charge)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(chargeData: List<Charge>)

    @Delete
    fun delete(chargeData: Charge)

    @Update
    fun update(chargeData: Charge): Int

    @Query("DELETE FROM Charge")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(chargeData: Charge) {
        deleteAll()
        insert(chargeData)
    }

    @Transaction
    fun deleteAllAndInsertAll(charges: List<Charge>) {
        deleteAll()
        insertAll(charges)
    }

    @Transaction
    @Query("DELETE FROM Charge WHERE id = :id")
    fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM Charge WHERE id = :id")
    fun getChargeById(id: Long): Charge?

    @Transaction
    @Query("SELECT * FROM Charge WHERE expenseId = :expenseId")
    fun getChargesByExpenseId(expenseId: Long): List<Charge>

    @Transaction
    @Query("SELECT * FROM Charge")
    fun getAll(): List<Charge>
}