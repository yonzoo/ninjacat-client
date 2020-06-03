package com.android.client.ninjacat.screens.client.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.ProductDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Product
import com.android.client.ninjacat.core.service.api.ProductApiService
import com.android.client.ninjacat.screens.auth.Event
import javax.inject.Inject

class ClientProductViewModel @Inject constructor(productDao: ProductDao,
                                                 userDao: UserDao,
                                                 productApiService: ProductApiService) :
    ViewModel() {
    private val _productLiveData = MutableLiveData<Event<List<Product>>>()
    val productLiveData: LiveData<Event<List<Product>>> = _productLiveData
    private val _logoutLiveData = MutableLiveData<Boolean>()
    val logoutLiveData: LiveData<Boolean> = _logoutLiveData

    private val clientProductRepository: ClientProductRepository =
        ClientProductRepository(
            productDao,
            userDao,
            productApiService
        )

    suspend fun getProducts() {
      clientProductRepository.getProducts(_productLiveData)
    }

    fun logoutUser() {
        clientProductRepository.logoutUser(_logoutLiveData)
    }
}