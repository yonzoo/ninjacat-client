package com.android.client.ninjacat.screens.auth.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registrationViewModel =
            ViewModelProvider(this, viewModelFactory).get(RegistrationViewModel::class.java)

        val animationView =
            view.findViewById<pl.droidsonroids.gif.GifImageView>(R.id.regLoading)

        animationView.visibility = View.INVISIBLE

        registrationViewModel.registrationValidationState.observe(viewLifecycleOwner,
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

        fun showLoading() {
            animationView.visibility = View.VISIBLE

            regRegBtn.isEnabled = false
            regLoginTxt.isEnabled = false
            regPasswordTxt.isEnabled = false
        }

        fun stopLoading() {
            animationView.visibility = View.INVISIBLE

            regRegBtn.isEnabled = true
            regLoginTxt.isEnabled = true
            regPasswordTxt.isEnabled = true
        }


        val afterLoginTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                registrationViewModel.loginChanged(regLoginTxt.text.toString())
            }
        }

        val afterPasswordTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                registrationViewModel.passwordChanged(regPasswordTxt.text.toString())
            }
        }

        fun registrationBtnClickedListener() {
            lifecycleScope.launch(Dispatchers.IO) {
                registrationViewModel.registerUser(
                    regLoginTxt.text.toString(),
                    regPasswordTxt.text.toString()
                )
            }
        }

        fun transitionToLoginClickedListener() {
            val action = RegistrationFragmentDirections
                .actionRegistrationFragmentToLoginFragment()
            NavHostFragment.findNavController(this@RegistrationFragment)
                .navigate(action)
        }

        registrationViewModel.userLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showRegistrationFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        transitionToLoginClickedListener()
                    }
                }
            })

        // Setup listeners
        regLoginTxt.addTextChangedListener(afterLoginTextChangedListener)
        regPasswordTxt.addTextChangedListener(afterPasswordTextChangedListener)
        regRegBtn.setOnClickListener {
            registrationBtnClickedListener()
        }
        loginLink.setOnClickListener {
            transitionToLoginClickedListener();
        }
    }

    private fun showRegistrationFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}
