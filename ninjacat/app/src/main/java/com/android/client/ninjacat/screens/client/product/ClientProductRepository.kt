package com.android.client.ninjacat.screens.client.product

import androidx.lifecycle.MutableLiveData
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.core.room.dao.ProductDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Product
import com.android.client.ninjacat.core.service.api.ProductApiService
import com.android.client.ninjacat.screens.auth.Error
import com.android.client.ninjacat.screens.auth.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class ClientProductRepository(
    private val productDao: ProductDao,
    private val userDao: UserDao,
    private val productApiService: ProductApiService
) {

    suspend fun getProducts(
        liveData: MutableLiveData<Event<List<Product>>>
    ) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = productApiService.getProductsAsync()

                val response = request.await()
                val responseStatus = response.code()
                val responseBodyProducts = response.body()

                if (responseStatus == 200 && responseBodyProducts != null) {
                    productDao.insertAll(responseBodyProducts)
                    liveData.postValue(Event.success(responseBodyProducts))
                } else {
                    liveData.postValue(
                        Event.error(Error(getProductErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getProductErrorMessage(-1)))
                )
            }
        }
    }

    fun logoutUser(liveData: MutableLiveData<Boolean>) {
        userDao.deleteAll()
        productDao.deleteAll()
        AppController.prefs.accessToken = ""
        AppController.prefs.userLogin = ""
        liveData.postValue(true)
    }

    private fun getProductErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            404 -> R.string.products_not_found
            else -> R.string.failed_get_products
        }
    }

}