package com.android.client.ninjacat.screens.admin.product

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
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.admin.AdminHomeActivity
import com.android.client.ninjacat.screens.admin.NavListener
import com.android.client.ninjacat.screens.auth.Status
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_product_admin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class AdminProductFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminProductViewModel: AdminProductViewModel
    private lateinit var adapter: ProductAdapter

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
        val rootView: View = inflater.inflate(R.layout.fragment_product_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Товары"
        val mainActivity = (activity as AppCompatActivity?)
        mainActivity?.setSupportActionBar(tb)
        val productScrollView = rootView.findViewById<NestedScrollView>(R.id.productScrollView)
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
                adminProductViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adminProductViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminProductViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.productLoading)
        val noProductsTitle = view.findViewById<TextView>(R.id.noProductsTitle)
        val noProductsImage = view.findViewById<ImageView>(R.id.productImageView)
        animationView.visibility = View.GONE
        noProductsTitle.visibility = View.GONE
        noProductsImage.visibility = View.GONE

        adapter =
            ProductAdapter(
                context!!,
                listOf(),
                {
                    navigateToEditProduct(it.id)
                },
                {
                    lifecycleScope.launch(Dispatchers.IO) {
                        adminProductViewModel.deleteProduct(it.id)
                    }
                }
            )
        productRecyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            adminProductViewModel.getProducts()
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        productRecyclerView.layoutManager = layoutManager
        productRecyclerView.isNestedScrollingEnabled = false

        fun showLoading() {
            noProductsTitle.visibility = View.GONE
            noProductsImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
            if (adapter.itemCount == 0) {
                noProductsTitle.visibility = View.VISIBLE
                noProductsImage.visibility = View.VISIBLE
            }
        }

        adminProductViewModel.productLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        adapter.setData(listOf())
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showGetProductsFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            adapter.setData(it.data)
                        }
                        stopLoading()
                    }
                }
            })

        adminProductViewModel.deleteLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.ERROR -> {
                        it.error?.message?.let { errorMessage -> showGetProductsFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            adminProductViewModel.getProducts()
                        }
                    }
                }
            })

        addBtn.setOnClickListener {
            navigateToCreateProduct()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToCreateProduct() {
        val action = AdminProductFragmentDirections
            .actionAdminProductFragmentToAdminCreateProductFragment()
        NavHostFragment.findNavController(this@AdminProductFragment)
            .navigate(action)
    }

    private fun navigateToEditProduct(id: Long) {
        val action = AdminProductFragmentDirections
            .actionAdminProductFragmentToAdminEditProductFragment(id)
        NavHostFragment.findNavController(this@AdminProductFragment)
            .navigate(action)
    }

    private fun showGetProductsFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}