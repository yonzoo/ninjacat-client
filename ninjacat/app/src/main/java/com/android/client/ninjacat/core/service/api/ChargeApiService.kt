package com.android.client.ninjacat.core.service.api

import com.android.client.ninjacat.core.room.models.Charge
import com.android.client.ninjacat.core.service.api.model.requests.Message
import com.android.client.ninjacat.core.service.api.model.requests.ChargeBody
import com.android.client.ninjacat.core.service.api.model.requests.ChargeResponse
import com.android.client.ninjacat.core.utils.Utils
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface ChargeApiService {
    @GET(Utils.URL_GET_CHARGES_BY_EXPENSE_ID)
    fun getChargesByExpenseIdAsync(@Path("id") id: Long): Deferred<Response<List<ChargeResponse>>>

    @POST(Utils.URL_ADD_CHARGE)
    fun addChargeAsync(@Body chargeBody: ChargeBody): Deferred<Response<ChargeResponse>>

    @PUT(Utils.URL_EDIT_CHARGE)
    fun editChargeAsync(@Path("id") id: Long, @Body chargeBody: ChargeBody): Deferred<Response<ChargeResponse>>

    @DELETE(Utils.URL_DELETE_CHARGE)
    fun deleteChargeAsync(@Path("id") id: Long): Deferred<Response<Message>>
}