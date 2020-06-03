package com.android.client.ninjacat.screens.auth.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.auth.AuthenticationActivity
import com.android.client.ninjacat.screens.auth.NavListener
import com.android.client.ninjacat.screens.auth.Status
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class LoginFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var loginViewModel: LoginViewModel

    private var listener: NavListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        listener = activity as AuthenticationActivity?
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.loginLoading)

        animationView.visibility = View.GONE

        fun showLoading() {
            animationView.visibility = View.VISIBLE

            loginLoginBtn.isEnabled = false
            loginLoginTxtInput.isEnabled = false
            loginPasswordTxtInput.isEnabled = false
        }

        fun stopLoading() {
            animationView.visibility = View.GONE

            loginLoginBtn.isEnabled = true
            loginLoginTxtInput.isEnabled = true
            loginPasswordTxtInput.isEnabled = true
        }

        loginViewModel.loginValidationState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                if (it.loginError == null) {
                    regLoginTxtLayout.error = null
                } else {
                    regLoginTxtLayout.error = getString(it.loginError)
                }
                if (it.passwordError == null) {
                    regPasswordTxtLayout.error = null
                } else {
                    regPasswordTxtLayout.error = getString(it.passwordError)
                }
            })

        loginViewModel.userLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showLoginFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        if (it.data?.role == "CLIENT") {
                            listener?.navigateToClientHomeActivity()
                        } else if (it.data?.role == "ADMIN") {
                            listener?.navigateToAdminHomeActivity()
                        }
                    }
                }
            })

        // Create listeners
        fun loginBtnClickedListener() {
            lifecycleScope.launch(Dispatchers.IO) {
                loginViewModel.loginUser(
                    loginLoginTxtInput.text.toString(),
                    loginPasswordTxtInput.text.toString()
                )
            }
        }

        fun transitionToRegClickedListener() {
            val action = LoginFragmentDirections
                .actionLoginFragmentToRegistrationFragment()
            NavHostFragment.findNavController(this@LoginFragment)
                .navigate(action)
        }

        // Setup listeners
        loginLoginBtn.setOnClickListener {
            loginBtnClickedListener()
        }
        regLink.setOnClickListener {
            transitionToRegClickedListener();
        }
    }

    private fun showLoginFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}
