package com.android.client.ninjacat.screens.admin.charge

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.screens.admin.AdminHomeActivity
import com.android.client.ninjacat.screens.admin.NavListener
import com.android.client.ninjacat.screens.admin.product.AdminEditProductFragmentArgs
import com.android.client.ninjacat.screens.auth.Status
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_charge_admin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class AdminChargeFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminChargeViewModel: AdminChargeViewModel
    private lateinit var adapter: ChargeAdapter

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
        val rootView: View = inflater.inflate(R.layout.fragment_charge_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Расходы"
        val mainActivity = (activity as AppCompatActivity?)
        mainActivity?.setSupportActionBar(tb)
        val chargeScrollView = rootView.findViewById<NestedScrollView>(R.id.chargeScrollView)
        chargeScrollView.scrollTo(0, 0)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.quit -> {
                adminChargeViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: AdminChargeFragmentArgs by navArgs()
        adminChargeViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminChargeViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.chargeLoading)
        val noChargesTitle = view.findViewById<TextView>(R.id.noChargesTitle)
        val noChargesImage = view.findViewById<ImageView>(R.id.chargeImageView)
        animationView.visibility = View.GONE
        noChargesTitle.visibility = View.GONE
        noChargesImage.visibility = View.GONE

        adapter =
            ChargeAdapter(
                context!!,
                listOf(),
                {
                    navigateToEditCharge(it.id, args.expenseName, it.expenseId)
                },
                {
                    lifecycleScope.launch(Dispatchers.IO) {
                        adminChargeViewModel.deleteChargeById(it.id)
                    }
                }
            )
        chargeRecyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            adminChargeViewModel.getChargesByExpenseId(args.expenseId)
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        chargeRecyclerView.layoutManager = layoutManager
        chargeRecyclerView.isNestedScrollingEnabled = false

        fun showLoading() {
            noChargesTitle.visibility = View.GONE
            noChargesImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
            if (adapter.itemCount == 0) {
                noChargesTitle.visibility = View.VISIBLE
                noChargesImage.visibility = View.VISIBLE
            }
        }

        adminChargeViewModel.chargeLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        adapter.setData(listOf())
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showGetChargesFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            adapter.setData(it.data)
                        }
                        stopLoading()
                    }
                }
            })

        adminChargeViewModel.deleteLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.ERROR -> {
                        it.error?.message?.let { errorMessage -> showGetChargesFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            adminChargeViewModel.getChargesByExpenseId(args.expenseId)
                        }
                    }
                }
            })

        addBtn.setOnClickListener {
            navigateToCreateCharge()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToCreateCharge() {
        val action = AdminChargeFragmentDirections
            .actionAdminChargeFragmentToAdminCreateChargeFragment3()
        NavHostFragment.findNavController(this@AdminChargeFragment)
            .navigate(action)
    }

    private fun navigateToEditCharge(id: Long, expenseName: String, expenseId: Long) {
        val action = AdminChargeFragmentDirections
            .actionAdminChargeFragmentToAdminEditChargeFragment(id, expenseName, expenseId)
        NavHostFragment.findNavController(this@AdminChargeFragment)
            .navigate(action)
    }

    private fun showGetChargesFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}