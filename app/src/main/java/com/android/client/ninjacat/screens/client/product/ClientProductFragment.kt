package com.android.client.ninjacat.screens.client.product

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.auth.Status
import com.android.client.ninjacat.screens.client.ClientHomeActivity
import com.android.client.ninjacat.screens.client.NavListener
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_product_client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class ClientProductFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var clientProductViewModel: ClientProductViewModel
    private lateinit var adapter: ProductAdapter

    private var listener: NavListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        listener = activity as ClientHomeActivity?
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_product_client, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Товары"
        val mainActivity = (activity as AppCompatActivity?)
        mainActivity?.setSupportActionBar(tb)
        val productScrollView = rootView.findViewById<NestedScrollView>(R.id.expenseScrollView)
        productScrollView.scrollTo(0, 0)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.quit -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    clientProductViewModel.logoutUser()
                }
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clientProductViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClientProductViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.expenseLoading)
        val noProductsTitle = view.findViewById<TextView>(R.id.noExpensesTitle)
        val noProductsImage = view.findViewById<ImageView>(R.id.expenseImageView)
        animationView.visibility = View.GONE
        noProductsTitle.visibility = View.GONE
        noProductsImage.visibility = View.GONE

        adapter =
            ProductAdapter(
                context!!,
                listOf()
            ) {}
        expenseRecyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            clientProductViewModel.getProducts()
        }

        if (adapter.itemCount == 0) {
            noProductsTitle.visibility = View.VISIBLE
            noProductsImage.visibility = View.VISIBLE
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        expenseRecyclerView.layoutManager = layoutManager
        expenseRecyclerView.isNestedScrollingEnabled = false

        fun showLoading() {
            noProductsTitle.visibility = View.GONE
            noProductsImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
        }

        clientProductViewModel.productLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showGetProductsFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        if (it.data != null) {
                            adapter.setData(it.data)
                        }
                    }
                }
                if (adapter.itemCount == 0) {
                    noProductsTitle.visibility = View.VISIBLE
                    noProductsImage.visibility = View.VISIBLE
                } else {
                    if (noProductsTitle.visibility == View.VISIBLE) {
                        noProductsTitle.visibility = View.GONE
                    }
                    if (noProductsImage.visibility == View.VISIBLE) {
                        noProductsImage.visibility = View.GONE
                    }
                }
            })

        clientProductViewModel.logoutLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                } else if (it == true) {
                    listener?.navigateToAuthenticationActivity()
                }
            })

        super.onViewCreated(view, savedInstanceState)
    }

    private fun showGetProductsFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}