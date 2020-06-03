package com.android.client.ninjacat.core.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule constructor(private val context: Context) {
    /*
    * The method returns app context
    * */
    @Provides
    @Singleton
    fun provideAppContext() = context
}

