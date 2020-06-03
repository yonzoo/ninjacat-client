package com.android.client.ninjacat.screens.admin.charge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.ChargeDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Charge
import com.android.client.ninjacat.core.service.api.ChargeApiService
import com.android.client.ninjacat.screens.admin.AdminChargeValidationState
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.screens.helpers.Validators
import javax.inject.Inject

class AdminEditChargeViewModel @Inject constructor(chargeDao: ChargeDao,
                                                    userDao: UserDao,
                                                    chargeApiService: ChargeApiService
) :
    ViewModel() {
    private val _editLiveData = MutableLiveData<Event<Charge>>()
    val  editLiveData: LiveData<Event<Charge>> = _editLiveData
    private val _adminValidationState = MutableLiveData<AdminChargeValidationState>()
    val adminValidationState: LiveData<AdminChargeValidationState> = _adminValidationState
    private val _deleteLiveData = MutableLiveData<Event<Boolean>>()
    val deleteLiveData: LiveData<Event<Boolean>> = _deleteLiveData

    private val adminChargeRepository: AdminChargeRepository =
        AdminChargeRepository(
            chargeDao,
            userDao,
            chargeApiService
        )

    suspend fun editCharge(id: Long, newName: String, newAmount: String) {
        if (Validators.isChargeNameValid(newName) == 0
            && Validators.isChargeAmountValid(newAmount) == 0
        ) {
            adminChargeRepository.editCharge(_editLiveData, id, newName, Integer.parseInt(newAmount))
        } else {
            chargeNameChanged(newName)
            chargeAmountChanged(newAmount)
        }
    }

    fun getChargeDataFromDB(chargeId: Long): Charge? {
        return adminChargeRepository.getChargeDataFromDB(chargeId)
    }

    fun chargeNameChanged(newName: String?) {
        val chargeNameValidationError: Int? = Validators.isChargeNameValid(newName)
        chargeNameValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _adminValidationState.postValue(
                    AdminChargeValidationState(
                        nameError = errorMessage,
                        amountError = _adminValidationState.value?.amountError
                    )
                )
            } else {
                _adminValidationState.postValue(
                    AdminChargeValidationState(
                        nameError = null,
                        amountError = _adminValidationState.value?.amountError
                    )
                )
            }
        }
    }

    fun chargeAmountChanged(newAmount: String?) {
        val chargeAmountValidationError: Int? = Validators.isChargeAmountValid(newAmount)
        chargeAmountValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _adminValidationState.postValue(
                    AdminChargeValidationState(
                        nameError = _adminValidationState.value?.nameError,
                        amountError = errorMessage
                    )
                )
            } else {
                _adminValidationState.postValue(
                    AdminChargeValidationState(
                        nameError = _adminValidationState.value?.nameError,
                        amountError = null
                    )
                )
            }
        }
    }

    fun logoutUser() {
        adminChargeRepository.logoutUser()
    }
}