package com.android.client.ninjacat.core.service.api

import com.android.client.ninjacat.core.room.models.Expense
import com.android.client.ninjacat.core.service.api.model.requests.Message
import com.android.client.ninjacat.core.utils.Utils
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface ExpenseApiService {
    @GET(Utils.URL_GET_EXPENSES)
    fun getExpensesAsync(): Deferred<Response<List<Expense>>>

    @DELETE(Utils.URL_DELETE_EXPENSE)
    fun deleteExpenseAsync(@Path("id") id: Long): Deferred<Response<Message>>
}