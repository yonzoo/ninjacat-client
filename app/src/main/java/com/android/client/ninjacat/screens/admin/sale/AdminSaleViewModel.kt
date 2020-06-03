package com.android.client.ninjacat.screens.admin.sale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.SaleDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Sale
import com.android.client.ninjacat.core.service.api.SaleApiService
import com.android.client.ninjacat.screens.auth.Event
import javax.inject.Inject

class AdminSaleViewModel @Inject constructor(saleDao: SaleDao,
                                             userDao: UserDao,
                                             saleApiService: SaleApiService
) :
    ViewModel() {
    private val _saleLiveData = MutableLiveData<Event<List<Sale>>>()
    val saleLiveData: LiveData<Event<List<Sale>>> = _saleLiveData
    private val _deleteLiveData = MutableLiveData<Event<Boolean>>()
    val deleteLiveData: LiveData<Event<Boolean>> = _deleteLiveData

    private val adminSaleRepository: AdminSaleRepository =
        AdminSaleRepository(
            saleDao,
            userDao,
            saleApiService
        )

    suspend fun getSales() {
        adminSaleRepository.getSales(_saleLiveData)
    }

    suspend fun deleteSaleById(id: Long) {
        adminSaleRepository.deleteSale(_deleteLiveData, id)
    }

    fun logoutUser() {
        adminSaleRepository.logoutUser()
    }
}