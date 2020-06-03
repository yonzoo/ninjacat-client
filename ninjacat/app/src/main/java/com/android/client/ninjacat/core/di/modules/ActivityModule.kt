package com.android.client.ninjacat.core.di.modules

import com.android.client.ninjacat.screens.admin.AdminHomeActivity
import com.android.client.ninjacat.screens.auth.AuthenticationActivity
import com.android.client.ninjacat.screens.client.ClientHomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [AuthFragmentModule::class])
    abstract fun contributeAuthenticationActivity(): AuthenticationActivity

    @ContributesAndroidInjector(modules = [AdminFragmentModule::class])
    abstract fun contributeAdminHomeActivity(): AdminHomeActivity

    @ContributesAndroidInjector(modules = [ClientFragmentModule::class])
    abstract fun contributeClientHomeActivity(): ClientHomeActivity
}

