package com.android.client.ninjacat.screens.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.client.ninjacat.R
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.client.ninjacat.screens.auth.AuthenticationActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AdminHomeActivity : DaggerAppCompatActivity(), NavListener {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    override fun navigateToAuthenticationActivity() {
        val authIntent = Intent(this, AuthenticationActivity::class.java)
        startActivity(authIntent)
        finish()
    }
}
