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
import androidx.navigation.fragment.navArgs
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.admin.AdminHomeActivity
import com.android.client.ninjacat.screens.admin.NavListener
import com.android.client.ninjacat.screens.auth.Status
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_charge_admin.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import java.lang.Exception
import javax.inject.Inject

class AdminEditChargeFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminEditChargeViewModel: AdminEditChargeViewModel

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
            inflater.inflate(R.layout.fragment_edit_charge_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Изменение расхода"
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
                adminEditChargeViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: AdminEditChargeFragmentArgs by navArgs()
        adminEditChargeViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminEditChargeViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.editChargeLoading)
        animationView.visibility = View.GONE


        fun showLoading() {
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
        }

        val chargeData = adminEditChargeViewModel.getChargeDataFromDB(args.id)
        chargeData?.let {
            editChargeNameTxtInput.setText(args.expenseName)
            editChargeAmountTxt.setText(chargeData.amount.toString())
        }

        adminEditChargeViewModel.editLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showEditChargeFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        navigateToAdminExpense()
                    }
                }
            })

        adminEditChargeViewModel.deleteLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showEditChargeFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        navigateToAdminExpense()
                    }
                }
            })

        adminEditChargeViewModel.adminValidationState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                if (it.nameError == null) {
                    editChargeNameTxtLayout.error = null
                } else {
                    editChargeNameTxtLayout.error = getString(it.nameError)
                }
                if (it.amountError == null) {
                    editChargeAmountTxtLayout.error = null
                } else {
                    editChargeAmountTxtLayout.error = getString(it.amountError)
                }
            })

        editChargeBtn.setOnClickListener {
            val name = editChargeNameTxtInput.text.toString()
            val amount = editChargeAmountTxt.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                adminEditChargeViewModel.editCharge(
                    args.id,
                    args.expenseName,
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
                adminEditChargeViewModel.chargeNameChanged(editChargeNameTxtInput.text.toString())
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
                adminEditChargeViewModel.chargeAmountChanged(editChargeAmountTxt.text.toString())
            }
        }

        editChargeNameTxtInput.addTextChangedListener(afterNameTextChangedListener)
        editChargeAmountTxt.addTextChangedListener(afterChargeAmountTextChangedListener)


        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToAdminExpense() {
        val action = AdminEditChargeFragmentDirections
            .actionAdminEditChargeFragmentToAdminExpenseFragment()
        NavHostFragment.findNavController(this@AdminEditChargeFragment)
            .navigate(action)
    }

    private fun showEditChargeFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}
