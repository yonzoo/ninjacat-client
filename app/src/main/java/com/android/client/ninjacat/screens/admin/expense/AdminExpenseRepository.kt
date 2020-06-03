package com.android.client.ninjacat.screens.admin.expense

import androidx.lifecycle.MutableLiveData
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.core.room.dao.ExpenseDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Expense
import com.android.client.ninjacat.core.service.api.ExpenseApiService
import com.android.client.ninjacat.screens.auth.Error
import com.android.client.ninjacat.screens.auth.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class AdminExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val userDao: UserDao,
    private val expenseApiService: ExpenseApiService
) {

    suspend fun getExpenses(
        liveData: MutableLiveData<Event<List<Expense>>>
    ) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = expenseApiService.getExpensesAsync()

                val response = request.await()
                val responseStatus = response.code()
                val responseBodyExpenses = response.body()

                if (responseStatus == 200 && responseBodyExpenses != null) {
                    expenseDao.deleteAllAndInsertAll(responseBodyExpenses)
                    val resultExpenses = expenseDao.getAll()
                    liveData.postValue(Event.success(resultExpenses))
                } else {
                    liveData.postValue(
                        Event.error(Error(getExpenseErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getExpenseErrorMessage(-1)))
                )
            }
        }
    }

    suspend fun deleteExpense(liveData: MutableLiveData<Event<Boolean>>, id: Long) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = expenseApiService.deleteExpenseAsync(id)

                val response = request.await()
                val responseStatus = response.code()

                if (responseStatus == 200) {
                    expenseDao.deleteById(id)
                    liveData.postValue(Event.success(true))
                } else {
                    liveData.postValue(
                        Event.error(Error(getDeleteExpenseErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getDeleteExpenseErrorMessage(-1)))
                )
            }
        }
    }

    fun logoutUser() {
        userDao.deleteAll()
        expenseDao.deleteAll()
        AppController.prefs.accessToken = ""
        AppController.prefs.userLogin = ""
    }

    private fun getExpenseErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            404 -> R.string.expenses_not_found
            else -> R.string.failed_get_expenses
        }
    }

    private fun getDeleteExpenseErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            404 -> R.string.expense_not_found
            else -> R.string.failed_delete_expense
        }
    }

}