package com.android.client.ninjacat.core.service.api

import com.android.client.ninjacat.core.room.models.Product
import com.android.client.ninjacat.core.service.api.model.requests.Message
import com.android.client.ninjacat.core.service.api.model.requests.ProductBody
import com.android.client.ninjacat.core.utils.Utils
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {
    @GET(Utils.URL_GET_PRODUCTS)
    fun getProductsAsync(): Deferred<Response<List<Product>>>

    @POST(Utils.URL_ADD_PRODUCT)
    fun addProductAsync(@Body productBody: ProductBody): Deferred<Response<Product>>

    @PUT(Utils.URL_EDIT_PRODUCT)
    fun editProductAsync(@Path("id") id: Long, @Body productBody: ProductBody): Deferred<Response<Product>>

    @DELETE(Utils.URL_DELETE_PRODUCT)
    fun deleteProductAsync(@Path("id") id: Long): Deferred<Response<Message>>
}