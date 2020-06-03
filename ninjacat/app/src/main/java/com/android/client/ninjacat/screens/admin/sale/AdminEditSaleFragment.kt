package com.android.client.ninjacat.screens.admin.sale

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
import kotlinx.android.synthetic.main.fragment_edit_sale_admin.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import java.lang.Exception
import javax.inject.Inject

class AdminEditSaleFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminEditSaleViewModel: AdminEditSaleViewModel

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
            inflater.inflate(R.layout.fragment_edit_sale_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Изменение продажи"
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
                adminEditSaleViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: AdminEditSaleFragmentArgs by navArgs()
        adminEditSaleViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminEditSaleViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.editSaleLoading)
        animationView.visibility = View.GONE


        fun showLoading() {
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
        }

        val saleData = adminEditSaleViewModel.getSaleDataFromDB(args.id)
        saleData?.let {
            editSaleNameTxtInput.setText(saleData.name)
            editSaleQuantityTxt.setText(saleData.quantity.toString())
            editSaleAmountTxt.setText(saleData.amount.toString())
        }

        adminEditSaleViewModel.editLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showEditSaleFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        navigateToAdminSale()
                    }
                }
            })

        adminEditSaleViewModel.deleteLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showEditSaleFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        navigateToAdminSale()
                    }
                }
            })

        adminEditSaleViewModel.adminValidationState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                if (it.nameError == null) {
                    editSaleNameTxtLayout.error = null
                } else {
                    editSaleNameTxtLayout.error = getString(it.nameError)
                }
                if (it.quantityError == null) {
                    editSaleQuantityTxtLayout.error = null
                } else {
                    editSaleQuantityTxtLayout.error = getString(it.quantityError)
                }
                if (it.amountError == null) {
                    editSaleAmountTxtLayout.error = null
                } else {
                    editSaleAmountTxtLayout.error = getString(it.amountError)
                }
            })

        editSaleBtn.setOnClickListener {
            val name = editSaleNameTxtInput.text.toString()
            val quantity = editSaleQuantityTxt.text.toString()
            val amount = editSaleAmountTxt.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                adminEditSaleViewModel.editSale(
                    args.id,
                    name,
                    quantity,
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
                adminEditSaleViewModel.saleNameChanged(editSaleNameTxtInput.text.toString())
            }
        }

        val afterSaleQuantityTextChangedListener = object : TextWatcher {
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
                adminEditSaleViewModel.saleQuantityChanged(editSaleQuantityTxt.text.toString())
            }
        }

        val afterSaleAmountTextChangedListener = object : TextWatcher {
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
                adminEditSaleViewModel.saleAmountChanged(editSaleAmountTxt.text.toString())
            }
        }

        editSaleNameTxtInput.addTextChangedListener(afterNameTextChangedListener)
        editSaleQuantityTxt.addTextChangedListener(afterSaleQuantityTextChangedListener)
        editSaleAmountTxt.addTextChangedListener(afterSaleAmountTextChangedListener)


        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToAdminSale() {
        val action = AdminEditSaleFragmentDirections
            .actionAdminEditSaleFragmentToAdminSaleFragment()
        NavHostFragment.findNavController(this@AdminEditSaleFragment)
            .navigate(action)
    }

    private fun showEditSaleFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}
