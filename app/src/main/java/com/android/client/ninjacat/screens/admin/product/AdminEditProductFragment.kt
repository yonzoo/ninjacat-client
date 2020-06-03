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
import androidx.navigation.fragment.navArgs
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.admin.AdminHomeActivity
import com.android.client.ninjacat.screens.admin.NavListener
import com.android.client.ninjacat.screens.auth.Status
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_product_admin.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import java.lang.Exception
import javax.inject.Inject

class AdminEditProductFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminEditProductViewModel: AdminEditProductViewModel

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
            inflater.inflate(R.layout.fragment_edit_product_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Изменение товара"
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
                adminEditProductViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: AdminEditProductFragmentArgs by navArgs()
        adminEditProductViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminEditProductViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.editProductLoading)
        animationView.visibility = View.GONE


        fun showLoading() {
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
        }

        val productData = adminEditProductViewModel.getProductDataFromDB(args.id)
        productData?.let {
            editProductNameTxtInput.setText(productData.name)
            editProductQuantityTxt.setText(productData.quantity.toString())
            editProductAmountTxt.setText(productData.amount.toString())
        }

        adminEditProductViewModel.editLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showEditProductFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        navigateToAdminProduct()
                    }
                }
            })

        adminEditProductViewModel.deleteLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showEditProductFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        navigateToAdminProduct()
                    }
                }
            })

        adminEditProductViewModel.adminValidationState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                if (it.nameError == null) {
                    editProductNameTxtLayout.error = null
                } else {
                    editProductNameTxtLayout.error = getString(it.nameError)
                }
                if (it.quantityError == null) {
                    editProductQuantityTxtLayout.error = null
                } else {
                    editProductQuantityTxtLayout.error = getString(it.quantityError)
                }
                if (it.amountError == null) {
                    editProductAmountTxtLayout.error = null
                } else {
                    editProductAmountTxtLayout.error = getString(it.amountError)
                }
            })

        editProductBtn.setOnClickListener {
            val name = editProductNameTxtInput.text.toString()
            val quantity = editProductQuantityTxt.text.toString()
            val amount = editProductAmountTxt.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                adminEditProductViewModel.editProduct(
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
                adminEditProductViewModel.productNameChanged(editProductNameTxtInput.text.toString())
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
                adminEditProductViewModel.productQuantityChanged(editProductQuantityTxt.text.toString())
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
                adminEditProductViewModel.productAmountChanged(editProductAmountTxt.text.toString())
            }
        }

        editProductNameTxtInput.addTextChangedListener(afterNameTextChangedListener)
        editProductQuantityTxt.addTextChangedListener(afterProductQuantityTextChangedListener)
        editProductAmountTxt.addTextChangedListener(afterProductAmountTextChangedListener)


        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToAdminProduct() {
        val action = AdminEditProductFragmentDirections
            .actionAdminEditProductFragmentToAdminProductFragment()
        NavHostFragment.findNavController(this@AdminEditProductFragment)
            .navigate(action)
    }

    private fun showEditProductFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}
