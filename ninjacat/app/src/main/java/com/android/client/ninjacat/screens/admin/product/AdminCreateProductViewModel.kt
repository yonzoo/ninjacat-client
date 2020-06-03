package com.android.client.ninjacat.screens.admin.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.ProductDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.Product
import com.android.client.ninjacat.core.service.api.ProductApiService
import com.android.client.ninjacat.screens.admin.AdminProductValidationState
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.screens.helpers.Validators
import javax.inject.Inject

class AdminCreateProductViewModel @Inject constructor(
    productDao: ProductDao,
    userDao: UserDao,
    productApiService: ProductApiService
) :
    ViewModel() {
    private val _createLiveData = MutableLiveData<Event<Product>>()
    val createLiveData: LiveData<Event<Product>> = _createLiveData
    private val _adminValidationState = MutableLiveData<AdminProductValidationState>()
    val adminValidationState: LiveData<AdminProductValidationState> = _adminValidationState

    private val adminProductRepository: AdminProductRepository =
        AdminProductRepository(
            productDao,
            userDao,
            productApiService
        )

    suspend fun createProduct(newName: String, newQuantity: String, newAmount: String) {
        if (Validators.isProductNameValid(newName) == 0
            && Validators.isProductQuantityValid(newQuantity) == 0
            && Validators.isProductAmountValid(newAmount) == 0
        ) {
            adminProductRepository.createProduct(
                _createLiveData,
                newName,
                Integer.parseInt(newQuantity),
                Integer.parseInt(newAmount)
            )
        } else {
            productNameChanged(newName)
            productQuantityChanged(newQuantity)
            productAmountChanged(newAmount)
        }
    }

    fun productNameChanged(newName: String?) {
        val productNameValidationError: Int? = Validators.isProductNameValid(newName)
        productNameValidationError?.let { errorMessage ->
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

    fun productQuantityChanged(newQuantity: String?) {
        val productQuantityValidationError: Int? = Validators.isProductQuantityValid(newQuantity)
        productQuantityValidationError?.let { errorMessage ->
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

    fun productAmountChanged(newAmount: String?) {
        val productAmountValidationError: Int? = Validators.isProductAmountValid(newAmount)
        productAmountValidationError?.let { errorMessage ->
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
        adminProductRepository.logoutUser()
    }
}