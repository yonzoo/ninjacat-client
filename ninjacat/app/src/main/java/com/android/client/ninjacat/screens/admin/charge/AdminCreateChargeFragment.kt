package com.android.client.ninjacat.screens.admin.charge

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.admin.AdminHomeActivity
import com.android.client.ninjacat.screens.admin.NavListener
import com.android.client.ninjacat.screens.auth.Status
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_create_charge_admin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class AdminCreateChargeFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminCreateChargeViewModel: AdminCreateChargeViewModel

    private var listener: NavListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        listener = activity as AdminHomeActivity?
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View =
            inflater.inflate(R.layout.fragment_create_charge_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Добавление расхода"
        val mainActivity = (activity as AppCompatActivity?)
        mainActivity?.setSupportActionBar(tb)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.quit -> {
                adminCreateChargeViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adminCreateChargeViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminCreateChargeViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.createChargeLoading)
        animationView.visibility = View.GONE


        fun showLoading() {
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
        }

        adminCreateChargeViewModel.createLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage ->
                            showCreateChargeFailed(
                                errorMessage
                            )
                        }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        navigateToAdminExpense()
                    }
                }
            })

        adminCreateChargeViewModel.adminValidationState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                if (it.nameError == null) {
                    createChargeNameTxtLayout.error = null
                } else {
                    createChargeNameTxtLayout.error = getString(it.nameError)
                }
                if (it.amountError == null) {
                    createChargeAmountTxtLayout.error = null
                } else {
                    createChargeAmountTxtLayout.error = getString(it.amountError)
                }
            })

        createChargeBtn.setOnClickListener {
            val name = createChargeNameTxtInput.text.toString()
            val amount = createChargeAmountTxt.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                adminCreateChargeViewModel.createCharge(
                    name,
                    amount
                )
            }
        }

        val afterNameTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                adminCreateChargeViewModel.chargeNameChanged(createChargeNameTxtInput.text.toString())
            }
        }

        val afterChargeAmountTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                adminCreateChargeViewModel.chargeAmountChanged(createChargeAmountTxt.text.toString())
            }
        }

        createChargeNameTxtInput.addTextChangedListener(afterNameTextChangedListener)
        createChargeAmountTxt.addTextChangedListener(afterChargeAmountTextChangedListener)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToAdminExpense() {
        val action = AdminCreateChargeFragmentDirections
            .actionAdminCreateChargeFragment3ToAdminExpenseFragment()
        NavHostFragment.findNavController(this@AdminCreateChargeFragment)
            .navigate(action)
    }

    private fun showCreateChargeFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}