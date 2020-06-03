package com.android.client.ninjacat.core.di.modules

import com.android.client.ninjacat.screens.auth.registration.RegistrationFragment
import com.android.client.ninjacat.screens.auth.login.LoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationFragment(): RegistrationFragment
}

