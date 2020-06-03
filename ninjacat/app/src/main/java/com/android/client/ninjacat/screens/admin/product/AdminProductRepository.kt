package com.android.client.ninjacat.screens.admin.product

import androidx.lifecycle.MutableLiveData
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.core.room.dao.ProductDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Product
import com.android.client.ninjacat.core.service.api.ProductApiService
import com.android.client.ninjacat.core.service.api.model.requests.ProductBody
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.screens.auth.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class AdminProductRepository(
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
                    productDao.deleteAllAndInsertAll(responseBodyProducts)
                    val resultProducts = productDao.getAll()
                    liveData.postValue(Event.success(resultProducts))
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

    fun getProductDataFromDB(id: Long): Product? {
        return productDao.getProductById(id)
    }

    suspend fun deleteProduct(liveData: MutableLiveData<Event<Boolean>>, id: Long) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = productApiService.deleteProductAsync(id)

                val response = request.await()
                val responseStatus = response.code()

                if (responseStatus == 200) {
                    productDao.deleteById(id)
                    liveData.postValue(Event.success(true))
                } else {
                    liveData.postValue(
                        Event.error(Error(getEditProductErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getEditProductErrorMessage(-1)))
                )
            }
        }
    }

    suspend fun editProduct(liveData: MutableLiveData<Event<Product>>, id: Long, newName: String, newQuantity: Int, newAmount: Int) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = productApiService.editProductAsync(id, ProductBody(newName, newQuantity, newAmount))

                val response = request.await()
                val responseStatus = response.code()
                val responseBodyProduct = response.body()

                if (responseStatus == 200 && responseBodyProduct != null) {
                    productDao.insert(responseBodyProduct)
                    liveData.postValue(Event.success(responseBodyProduct))
                } else {
                    liveData.postValue(
                        Event.error(Error(getEditProductErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getEditProductErrorMessage(-1)))
                )
            }
        }
    }

    suspend fun createProduct(liveData: MutableLiveData<Event<Product>>, name: String, quantity: Int, amount: Int) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = productApiService.addProductAsync(ProductBody(name, quantity, amount))

                val response = request.await()
                val responseStatus = response.code()
                val responseBodyProduct = response.body()

                if (responseStatus == 200 && responseBodyProduct != null) {
                    productDao.insert(responseBodyProduct)
                    liveData.postValue(Event.success(responseBodyProduct))
                } else {
                    liveData.postValue(
                        Event.error(Error(getAddProductErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getAddProductErrorMessage(-1)))
                )
            }
        }
    }


    fun logoutUser() {
        userDao.deleteAll()
        productDao.deleteAll()
        AppController.prefs.accessToken = ""
        AppController.prefs.userLogin = ""
    }

    private fun getProductErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            404 -> R.string.products_not_found
            else -> R.string.failed_get_products
        }
    }

    private fun getAddProductErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            409 -> R.string.product_exists
            else -> R.string.failed_add_product
        }
    }

    private fun getEditProductErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            404 -> R.string.no_product_found
            else -> R.string.failed_edit_product
        }
    }

}