package com.android.client.ninjacat.core.di.modules

import com.android.client.ninjacat.screens.client.product.ClientProductFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ClientFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeClientProductFragment(): ClientProductFragment
}