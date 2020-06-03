package com.android.client.ninjacat.screens.admin.expense

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
import kotlinx.android.synthetic.main.fragment_expense_item_admin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class AdminExpenseFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adminExpenseViewModel: AdminExpenseViewModel
    private lateinit var adapter: ExpenseAdapter

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
            inflater.inflate(R.layout.fragment_expense_item_admin, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.title = "Статьи расходов"
        val mainActivity = (activity as AppCompatActivity?)
        mainActivity?.setSupportActionBar(tb)
        val expenseScrollView = rootView.findViewById<NestedScrollView>(R.id.expenseScrollView)
        expenseScrollView.scrollTo(0, 0)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.quit -> {
                adminExpenseViewModel.logoutUser()
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adminExpenseViewModel =
            ViewModelProvider(this, viewModelFactory).get(AdminExpenseViewModel::class.java)

        val animationView = view.findViewById<GifImageView>(R.id.expenseLoading)
        val noExpensesTitle = view.findViewById<TextView>(R.id.noExpensesTitle)
        val noExpensesImage = view.findViewById<ImageView>(R.id.expenseImageView)
        animationView.visibility = View.GONE
        noExpensesTitle.visibility = View.GONE
        noExpensesImage.visibility = View.GONE

        adapter =
            ExpenseAdapter(
                context!!,
                listOf(),
                {
                    navigateToCharges(it.id, it.name)
                },
                {
                    lifecycleScope.launch(Dispatchers.IO) {
                        adminExpenseViewModel.deleteExpense(it.id)
                    }
                }
            )
        expenseRecyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            adminExpenseViewModel.getExpenses()
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        expenseRecyclerView.layoutManager = layoutManager
        expenseRecyclerView.isNestedScrollingEnabled = false

        fun showLoading() {
            noExpensesTitle.visibility = View.GONE
            noExpensesImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE
        }

        fun stopLoading() {
            animationView.visibility = View.GONE
            if (adapter.itemCount == 0) {
                noExpensesTitle.visibility = View.VISIBLE
                noExpensesImage.visibility = View.VISIBLE
            }
        }

        adminExpenseViewModel.expenseLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        adapter.setData(listOf())
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showGetExpensesFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            adapter.setData(it.data)
                        }
                        stopLoading()
                    }
                }
            })

        adminExpenseViewModel.deleteLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.ERROR -> {
                        it.error?.message?.let { errorMessage -> showGetExpensesFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            adminExpenseViewModel.getExpenses()
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
        val action = AdminExpenseFragmentDirections
            .actionAdminExpenseFragmentToAdminCreateChargeFragment3()
        NavHostFragment.findNavController(this@AdminExpenseFragment)
            .navigate(action)
    }

    private fun navigateToCharges(id: Long, name: String) {
        val action = AdminExpenseFragmentDirections
            .actionAdminExpenseFragmentToAdminChargeFragment(name, id)
        NavHostFragment.findNavController(this@AdminExpenseFragment)
            .navigate(action)
    }

    private fun showGetExpensesFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}