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
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.admin.AdminHomeActivity
import com.android.client.ninjacat.screens.admin.NavListener
import com.android.client.ninjacat.screens.auth.Status
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_create_sale_admin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class AdminCreateSaleFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminCreateSaleViewModel: AdminCreateSaleViewModel

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
            inflater.inflate(R.layout.fragment_create_sale_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Добавление продажи"
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
                adminCreateSaleViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adminCreateSaleViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminCreateSaleViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.createSaleLoading)
        animationView.visibility = View.GONE


        fun showLoading() {
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
        }

        adminCreateSaleViewModel.createLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage ->
                            showCreateSaleFailed(
                                errorMessage
                            )
                        }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        navigateToAdminSale()
                    }
                }
            })

        adminCreateSaleViewModel.adminValidationState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                if (it.nameError == null) {
                    createSaleNameTxtLayout.error = null
                } else {
                    createSaleNameTxtLayout.error = getString(it.nameError)
                }
                if (it.quantityError == null) {
                    createSaleQuantityTxtLayout.error = null
                } else {
                    createSaleQuantityTxtLayout.error = getString(it.quantityError)
                }
                if (it.amountError == null) {
                    createSaleAmountTxtLayout.error = null
                } else {
                    createSaleAmountTxtLayout.error = getString(it.amountError)
                }
            })

        createSaleBtn.setOnClickListener {
            val name = createSaleNameTxtInput.text.toString()
            val quantity = createSaleQuantityTxt.text.toString()
            val amount = createSaleAmountTxt.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                adminCreateSaleViewModel.createSale(
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
                adminCreateSaleViewModel.saleNameChanged(createSaleNameTxtInput.text.toString())
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
                adminCreateSaleViewModel.saleQuantityChanged(createSaleQuantityTxt.text.toString())
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
                adminCreateSaleViewModel.saleAmountChanged(createSaleAmountTxt.text.toString())
            }
        }

        createSaleNameTxtInput.addTextChangedListener(afterNameTextChangedListener)
        createSaleQuantityTxt.addTextChangedListener(afterSaleQuantityTextChangedListener)
        createSaleAmountTxt.addTextChangedListener(afterSaleAmountTextChangedListener)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToAdminSale() {
        val action = AdminCreateSaleFragmentDirections
            .actionAdminCreateSaleFragmentToAdminSaleFragment()
        NavHostFragment.findNavController(this@AdminCreateSaleFragment)
            .navigate(action)
    }

    private fun showCreateSaleFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}