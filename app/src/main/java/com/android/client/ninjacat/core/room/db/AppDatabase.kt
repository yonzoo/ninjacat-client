package com.android.client.ninjacat.core.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.client.ninjacat.core.room.dao.*
import com.android.client.ninjacat.core.room.helpers.converters.Converters
import com.android.client.ninjacat.core.room.models.*

/**
 * Initialising app database
 */
@Database(
    entities = [User::class, Sale::class, Product::class, Expense::class, Charge::class], version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun chargeDao(): ChargeDao
    abstract fun expenseDao(): ExpenseDao
}

