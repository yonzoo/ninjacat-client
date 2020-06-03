package com.android.client.ninjacat.core.service.api

import com.android.client.ninjacat.core.service.api.model.requests.Message
import com.android.client.ninjacat.core.service.api.model.requests.SaleBody
import com.android.client.ninjacat.core.service.api.model.requests.SaleResponse
import com.android.client.ninjacat.core.utils.Utils
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface SaleApiService {
    @GET(Utils.URL_GET_SALES)
    fun getSalesAsync(): Deferred<Response<List<SaleResponse>>>

    @POST(Utils.URL_ADD_SALE)
    fun addSaleAsync(@Body saleBody: SaleBody): Deferred<Response<SaleResponse>>

    @PUT(Utils.URL_EDIT_SALE)
    fun editSaleAsync(@Path("id") id: Long, @Body saleBody: SaleBody): Deferred<Response<SaleResponse>>

    @DELETE(Utils.URL_DELETE_SALE)
    fun deleteSaleAsync(@Path("id") id: Long): Deferred<Response<Message>>
}