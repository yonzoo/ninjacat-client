package com.android.client.ninjacat.screens.admin.sale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.SaleDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Sale
import com.android.client.ninjacat.core.service.api.SaleApiService
import com.android.client.ninjacat.screens.admin.AdminProductValidationState
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.screens.helpers.Validators
import javax.inject.Inject

class AdminCreateSaleViewModel @Inject constructor(
    saleDao: SaleDao,
    userDao: UserDao,
    saleApiService: SaleApiService
) :
    ViewModel() {
    private val _createLiveData = MutableLiveData<Event<Sale>>()
    val createLiveData: LiveData<Event<Sale>> = _createLiveData
    private val _adminValidationState = MutableLiveData<AdminProductValidationState>()
    val adminValidationState: LiveData<AdminProductValidationState> = _adminValidationState

    private val adminSaleRepository: AdminSaleRepository =
        AdminSaleRepository(
            saleDao,
            userDao,
            saleApiService
        )

    suspend fun createSale(newName: String, newQuantity: String, newAmount: String) {
        if (Validators.isProductNameValid(newName) == 0
            && Validators.isProductQuantityValid(newQuantity) == 0
            && Validators.isProductAmountValid(newAmount) == 0
        ) {
            adminSaleRepository.createSale(
                _createLiveData,
                newName,
                Integer.parseInt(newQuantity),
                Integer.parseInt(newAmount)
            )
        } else {
            saleNameChanged(newName)
            saleQuantityChanged(newQuantity)
            saleAmountChanged(newAmount)
        }
    }

    fun saleNameChanged(newName: String?) {
        val saleNameValidationError: Int? = Validators.isProductNameValid(newName)
        saleNameValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _adminValidationState.postValue(
                    AdminProductValidationState(
                        nameError = errorMessage,
                        quantityError = _adminValidationState.value?.quantityError,
                        amountError = _adminValidationState.value?.amountError
                    )
                )
            } else {
                _adminValidationState.postValue(
                    AdminProductValidationState(
                        nameError = null,
                        quantityError = _adminValidationState.value?.quantityError,
                        amountError = _adminValidationState.value?.amountError
                    )
                )
            }
        }
    }

    fun saleQuantityChanged(newQuantity: String?) {
        val saleQuantityValidationError: Int? = Validators.isProductQuantityValid(newQuantity)
        saleQuantityValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _adminValidationState.postValue(
                    AdminProductValidationState(
                        nameError = _adminValidationState.value?.nameError,
                        quantityError = errorMessage,
                        amountError = _adminValidationState.value?.amountError
                    )
                )
            } else {
                _adminValidationState.postValue(
                    AdminProductValidationState(
                        nameError = _adminValidationState.value?.nameError,
                        quantityError = null,
                        amountError = _adminValidationState.value?.amountError
                    )
                )
            }
        }
    }

    fun saleAmountChanged(newAmount: String?) {
        val saleAmountValidationError: Int? = Validators.isProductAmountValid(newAmount)
        saleAmountValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _adminValidationState.postValue(
                    AdminProductValidationState(
                        nameError = _adminValidationState.value?.nameError,
                        quantityError = _adminValidationState.value?.quantityError,
                        amountError = errorMessage
                    )
                )
            } else {
                _adminValidationState.postValue(
                    AdminProductValidationState(
                        nameError = _adminValidationState.value?.nameError,
                        quantityError = _adminValidationState.value?.quantityError,
                        amountError = null
                    )
                )
            }
        }
    }

    fun logoutUser() {
        adminSaleRepository.logoutUser()
    }
}