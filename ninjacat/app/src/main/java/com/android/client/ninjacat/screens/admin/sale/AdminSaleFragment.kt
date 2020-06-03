package com.android.client.ninjacat.screens.admin.sale

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
import kotlinx.android.synthetic.main.fragment_sale_admin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class AdminSaleFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminSaleViewModel: AdminSaleViewModel
    private lateinit var adapter: SaleAdapter

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
        val rootView: View = inflater.inflate(R.layout.fragment_sale_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Продажи"
        val mainActivity = (activity as AppCompatActivity?)
        mainActivity?.setSupportActionBar(tb)
        val saleScrollView = rootView.findViewById<NestedScrollView>(R.id.expenseScrollView)
        saleScrollView.scrollTo(0, 0)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.quit -> {
                adminSaleViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adminSaleViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminSaleViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.expenseLoading)
        val noSalesTitle = view.findViewById<TextView>(R.id.noExpensesTitle)
        val noSalesImage = view.findViewById<ImageView>(R.id.expenseImageView)
        animationView.visibility = View.GONE
        noSalesTitle.visibility = View.GONE
        noSalesImage.visibility = View.GONE

        adapter =
            SaleAdapter(
                context!!,
                listOf(),
                {
                    navigateToEditSale(it.id)
                },
                {
                    lifecycleScope.launch(Dispatchers.IO) {
                        adminSaleViewModel.deleteSaleById(it.id)
                    }
                }
            )
        expenseRecyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            adminSaleViewModel.getSales()
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        expenseRecyclerView.layoutManager = layoutManager
        expenseRecyclerView.isNestedScrollingEnabled = false

        fun showLoading() {
            noSalesTitle.visibility = View.GONE
            noSalesImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
            if (adapter.itemCount == 0) {
                noSalesTitle.visibility = View.VISIBLE
                noSalesImage.visibility = View.VISIBLE
            }
        }

        adminSaleViewModel.saleLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        adapter.setData(listOf())
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showGetSalesFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            adapter.setData(it.data)
                        }
                        stopLoading()
                    }
                }
            })

        adminSaleViewModel.deleteLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showGetSalesFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            adminSaleViewModel.getSales()
                        }
                        stopLoading()
                    }
                }
            })

        addBtn.setOnClickListener {
            navigateToCreateSale()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToCreateSale() {
        val action = AdminSaleFragmentDirections
            .actionAdminSaleFragmentToAdminCreateSaleFragment()
        NavHostFragment.findNavController(this@AdminSaleFragment)
            .navigate(action)
    }

    private fun navigateToEditSale(id: Long) {
        val action = AdminSaleFragmentDirections
            .actionAdminSaleFragmentToAdminEditSaleFragment(id)
        NavHostFragment.findNavController(this@AdminSaleFragment)
            .navigate(action)
    }

    private fun showGetSalesFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}