package com.android.client.ninjacat.screens.client

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.auth.AuthenticationActivity
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class ClientHomeActivity : DaggerAppCompatActivity(), NavListener {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_client)
    }

   override fun navigateToAuthenticationActivity() {
        val authIntent = Intent(this, AuthenticationActivity::class.java)
        startActivity(authIntent)
        finish()
    }
}
