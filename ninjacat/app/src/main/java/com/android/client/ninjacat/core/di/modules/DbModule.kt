package com.android.client.ninjacat.core.di.modules

import android.app.Application
import androidx.room.Room
import com.android.client.ninjacat.core.room.dao.*
import com.android.client.ninjacat.core.room.db.AppDatabase
import com.android.client.ninjacat.core.utils.Utils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {

    @Provides
    @Singleton
    internal fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application, AppDatabase::class.java, Utils.DATABASE_NAME)
            .allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    internal fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    internal fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    internal fun provideSaleDao(appDatabase: AppDatabase): SaleDao {
        return appDatabase.saleDao()
    }

    @Provides
    @Singleton
    internal fun provideChargeDao(appDatabase: AppDatabase): ChargeDao {
        return appDatabase.chargeDao()
    }

    @Provides
    @Singleton
    internal fun provideExpenseDao(appDatabase: AppDatabase): ExpenseDao {
        return appDatabase.expenseDao()
    }
}

