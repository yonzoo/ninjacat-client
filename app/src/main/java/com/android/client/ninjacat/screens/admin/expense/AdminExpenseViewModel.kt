package com.android.client.ninjacat.screens.admin.expense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.ExpenseDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Expense
import com.android.client.ninjacat.core.service.api.ExpenseApiService
import com.android.client.ninjacat.screens.admin.expense.AdminExpenseRepository
import com.android.client.ninjacat.screens.auth.Event
import javax.inject.Inject

class AdminExpenseViewModel @Inject constructor(expenseDao: ExpenseDao,
                                                userDao: UserDao,
                                                expenseApiService: ExpenseApiService
) :
    ViewModel() {
    private val _expenseLiveData = MutableLiveData<Event<List<Expense>>>()
    val expenseLiveData: LiveData<Event<List<Expense>>> = _expenseLiveData
    private val _deleteLiveData = MutableLiveData<Event<Boolean>>()
    val deleteLiveData: LiveData<Event<Boolean>> = _deleteLiveData

    private val adminExpenseRepository: AdminExpenseRepository =
        AdminExpenseRepository(
            expenseDao,
            userDao,
            expenseApiService
        )

    suspend fun getExpenses() {
        adminExpenseRepository.getExpenses(_expenseLiveData)
    }

    suspend fun deleteExpense(id: Long) {
        adminExpenseRepository.deleteExpense(_deleteLiveData, id)
    }

    fun logoutUser() {
        adminExpenseRepository.logoutUser()
    }
}