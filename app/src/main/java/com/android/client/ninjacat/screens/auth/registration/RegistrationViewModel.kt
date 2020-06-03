package com.android.client.ninjacat.screens.auth.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.User
import com.android.client.ninjacat.core.service.api.UserApiService
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.screens.helpers.Validators
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(userDao: UserDao, userApiService: UserApiService) :
    ViewModel() {
    private val _registrationValidationState = MutableLiveData<RegistrationValidationState>()
    val registrationValidationState: LiveData<RegistrationValidationState> = _registrationValidationState
    private val _userLiveData = MutableLiveData<Event<User>>()
    val userLiveData: LiveData<Event<User>> = _userLiveData

    private val registrationRepository: RegistrationRepository =
        RegistrationRepository(
            userDao,
            userApiService
        )

    suspend fun registerUser(login: String, password: String) {
        if (Validators.isLoginValid(login) == 0
            && Validators.isPasswordValid(password) == 0
        ) {
            registrationRepository.registerUser(_userLiveData, login, password)
        } else {
            loginChanged(login)
            passwordChanged(password)
        }
    }

    fun loginChanged(login: String?) {
        val loginValidationError: Int? = Validators.isLoginValid(login)
        loginValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                    loginError = errorMessage,
                    passwordError = _registrationValidationState.value?.passwordError
                )
                )
            } else {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                    loginError = null,
                    passwordError = _registrationValidationState.value?.passwordError
                )
                )
            }
        }
    }

    fun passwordChanged(password: String?) {
        val passwordValidationError: Int? = Validators.isPasswordValid(password)
        passwordValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                    loginError = _registrationValidationState.value?.loginError,
                    passwordError = errorMessage
                )
                )
            } else {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                    loginError = _registrationValidationState.value?.loginError,
                    passwordError = null
                )
                )
            }
        }
    }
}

