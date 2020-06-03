package com.android.client.ninjacat.screens.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.admin.AdminHomeActivity
import com.android.client.ninjacat.screens.client.ClientHomeActivity
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AuthenticationActivity : DaggerAppCompatActivity(), NavListener {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    override fun navigateToAdminHomeActivity() {
        val adminIntent = Intent(this, AdminHomeActivity::class.java)
        startActivity(adminIntent)
        finish()
    }

    override fun navigateToClientHomeActivity() {
        val clientIntent = Intent(this, ClientHomeActivity::class.java)
        startActivity(clientIntent)
        finish()
    }
}
