package com.android.client.ninjacat.screens.admin.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.ProductDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Product
import com.android.client.ninjacat.core.service.api.ProductApiService
import com.android.client.ninjacat.screens.auth.Event
import javax.inject.Inject

class AdminProductViewModel @Inject constructor(productDao: ProductDao,
                                                userDao: UserDao,
                                                productApiService: ProductApiService
) :
    ViewModel() {
    private val _productLiveData = MutableLiveData<Event<List<Product>>>()
    val productLiveData: LiveData<Event<List<Product>>> = _productLiveData
    private val _deleteLiveData = MutableLiveData<Event<Boolean>>()
    val deleteLiveData: LiveData<Event<Boolean>> = _deleteLiveData

    private val adminProductRepository: AdminProductRepository =
        AdminProductRepository(
            productDao,
            userDao,
            productApiService
        )

    suspend fun getProducts() {
        adminProductRepository.getProducts(_productLiveData)
    }

    suspend fun deleteProduct(id: Long) {
        adminProductRepository.deleteProduct(_deleteLiveData, id)
    }

    fun logoutUser() {
        adminProductRepository.logoutUser()
    }
}