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

class AdminCreateChargeViewModel @Inject constructor(
    chargeDao: ChargeDao,
    userDao: UserDao,
    chargeApiService: ChargeApiService
) :
    ViewModel() {
    private val _createLiveData = MutableLiveData<Event<Charge>>()
    val createLiveData: LiveData<Event<Charge>> = _createLiveData
    private val _adminValidationState = MutableLiveData<AdminChargeValidationState>()
    val adminValidationState: LiveData<AdminChargeValidationState> = _adminValidationState

    private val adminChargeRepository: AdminChargeRepository =
        AdminChargeRepository(
            chargeDao,
            userDao,
            chargeApiService
        )

    suspend fun createCharge(newName: String, newAmount: String) {
        if (Validators.isChargeNameValid(newName) == 0
            && Validators.isChargeAmountValid(newAmount) == 0
        ) {
            adminChargeRepository.createCharge(
                _createLiveData,
                newName,
                Integer.parseInt(newAmount)
            )
        } else {
            chargeNameChanged(newName)
            chargeAmountChanged(newAmount)
        }
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