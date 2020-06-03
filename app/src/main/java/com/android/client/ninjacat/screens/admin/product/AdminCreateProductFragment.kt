package com.android.client.ninjacat.screens.admin.product

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
import kotlinx.android.synthetic.main.fragment_create_product_admin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class AdminCreateProductFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminCreateProductViewModel: AdminCreateProductViewModel

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
            inflater.inflate(R.layout.fragment_create_product_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Добавление товара"
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
                adminCreateProductViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adminCreateProductViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminCreateProductViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.createSaleLoading)
        animationView.visibility = View.GONE


        fun showLoading() {
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
        }

        adminCreateProductViewModel.createLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage ->
                            showCreateProductFailed(
                                errorMessage
                            )
                        }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        navigateToAdminProduct()
                    }
                }
            })

        adminCreateProductViewModel.adminValidationState.observe(viewLifecycleOwner,
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
                adminCreateProductViewModel.createProduct(
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
                adminCreateProductViewModel.productNameChanged(createSaleNameTxtInput.text.toString())
            }
        }

        val afterProductQuantityTextChangedListener = object : TextWatcher {
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
                adminCreateProductViewModel.productQuantityChanged(createSaleQuantityTxt.text.toString())
            }
        }

        val afterProductAmountTextChangedListener = object : TextWatcher {
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
                adminCreateProductViewModel.productAmountChanged(createSaleAmountTxt.text.toString())
            }
        }

        createSaleNameTxtInput.addTextChangedListener(afterNameTextChangedListener)
        createSaleQuantityTxt.addTextChangedListener(afterProductQuantityTextChangedListener)
        createSaleAmountTxt.addTextChangedListener(afterProductAmountTextChangedListener)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToAdminProduct() {
        val action = AdminCreateProductFragmentDirections
            .actionAdminCreateProductFragmentToAdminProductFragment()
        NavHostFragment.findNavController(this@AdminCreateProductFragment)
            .navigate(action)
    }

    private fun showCreateProductFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}