package com.android.client.ninjacat.screens.admin.charge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.ChargeDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Charge
import com.android.client.ninjacat.core.service.api.ChargeApiService
import com.android.client.ninjacat.screens.auth.Event
import javax.inject.Inject

class AdminChargeViewModel @Inject constructor(chargeDao: ChargeDao,
                                                userDao: UserDao,
                                                chargeApiService: ChargeApiService
) :
    ViewModel() {
    private val _chargeLiveData = MutableLiveData<Event<List<Charge>>>()
    val chargeLiveData: LiveData<Event<List<Charge>>> = _chargeLiveData
    private val _deleteLiveData = MutableLiveData<Event<Boolean>>()
    val deleteLiveData: LiveData<Event<Boolean>> = _deleteLiveData

    private val adminChargeRepository: AdminChargeRepository =
        AdminChargeRepository(
            chargeDao,
            userDao,
            chargeApiService
        )

    suspend fun getChargesByExpenseId(expenseId: Long) {
        adminChargeRepository.getChargesByExpenseId(_chargeLiveData, expenseId)
    }

    suspend fun deleteChargeById(id: Long) {
        adminChargeRepository.deleteCharge(_deleteLiveData, id)
    }

    fun logoutUser() {
        adminChargeRepository.logoutUser()
    }
}