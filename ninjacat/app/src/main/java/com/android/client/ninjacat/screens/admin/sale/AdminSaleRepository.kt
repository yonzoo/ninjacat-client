package com.android.client.ninjacat.screens.admin.sale

import androidx.lifecycle.MutableLiveData
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.core.room.dao.SaleDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.helpers.converters.Converters
import com.android.client.ninjacat.core.room.models.Sale
import com.android.client.ninjacat.core.service.api.SaleApiService
import com.android.client.ninjacat.core.service.api.model.requests.SaleBody
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.screens.auth.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class AdminSaleRepository(
    private val saleDao: SaleDao,
    private val userDao: UserDao,
    private val saleApiService: SaleApiService
) {

    suspend fun getSales(
        liveData: MutableLiveData<Event<List<Sale>>>
    ) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = saleApiService.getSalesAsync()

                val response = request.await()
                val responseStatus = response.code()
                val responseBodySales = response.body()

                if (responseStatus == 200 && responseBodySales != null) {
                    val sales = responseBodySales.map {
                        Sale(
                            it.id,
                            it.name,
                            it.quantity,
                            it.amount,
                            Converters.toOffsetDateTime(it.saleDate)!!
                        )
                    }
                    saleDao.deleteAllAndInsertAll(sales)
                    val resultSales = saleDao.getAll()
                    liveData.postValue(Event.success(resultSales))
                } else {
                    liveData.postValue(
                        Event.error(Error(getSaleErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getSaleErrorMessage(-1)))
                )
            }
        }
    }

    fun getSaleDataFromDB(id: Long): Sale? {
        return saleDao.getSaleById(id)
    }

    suspend fun deleteSale(liveData: MutableLiveData<Event<Boolean>>, id: Long) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = saleApiService.deleteSaleAsync(id)

                val response = request.await()
                val responseStatus = response.code()

                if (responseStatus == 200) {
                    saleDao.deleteById(id)
                    liveData.postValue(Event.success(true))
                } else {
                    liveData.postValue(
                        Event.error(Error(getEditSaleErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getEditSaleErrorMessage(-1)))
                )
            }
        }
    }

    suspend fun editSale(liveData: MutableLiveData<Event<Sale>>, id: Long, newName: String, newQuantity: Int, newAmount: Int) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = saleApiService.editSaleAsync(id, SaleBody(newName, newQuantity, newAmount))

                val response = request.await()
                val responseStatus = response.code()
                val responseBodySale = response.body()

                if (responseStatus == 200 && responseBodySale != null) {
                    val sale = Sale(
                        responseBodySale.id,
                        responseBodySale.name,
                        responseBodySale.quantity,
                        responseBodySale.amount,
                        Converters.toOffsetDateTime(responseBodySale.saleDate)!!
                    )
                    saleDao.insert(sale)
                    liveData.postValue(Event.success(sale))
                } else {
                    liveData.postValue(
                        Event.error(Error(getEditSaleErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getEditSaleErrorMessage(-1)))
                )
            }
        }
    }

    suspend fun createSale(liveData: MutableLiveData<Event<Sale>>, name: String, quantity: Int, amount: Int) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = saleApiService.addSaleAsync(SaleBody(name, quantity, amount))

                val response = request.await()
                val responseStatus = response.code()
                val responseBodySale = response.body()

                val a = responseBodySale

                if (responseStatus == 200 && responseBodySale != null) {
                    val sale = Sale(
                        responseBodySale.id,
                        responseBodySale.name,
                        responseBodySale.quantity,
                        responseBodySale.amount,
                        Converters.toOffsetDateTime(responseBodySale.saleDate)!!
                    )
                    saleDao.insert(sale)
                    liveData.postValue(Event.success(sale))
                } else {
                    liveData.postValue(
                        Event.error(Error(getAddSaleErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getAddSaleErrorMessage(-1)))
                )
            }
        }
    }


    fun logoutUser() {
        userDao.deleteAll()
        saleDao.deleteAll()
        AppController.prefs.accessToken = ""
        AppController.prefs.userLogin = ""
    }

    private fun getSaleErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            404 -> R.string.sales_not_found
            else -> R.string.failed_get_sales
        }
    }

    private fun getAddSaleErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            409 -> R.string.sale_exists
            204 -> R.string.warehouse_no_content
            else -> R.string.failed_add_sale
        }
    }

    private fun getEditSaleErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            404 -> R.string.no_sale_found
            else -> R.string.failed_edit_sale
        }
    }

}