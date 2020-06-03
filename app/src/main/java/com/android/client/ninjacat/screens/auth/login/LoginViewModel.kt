package com.android.client.ninjacat.screens.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.User
import com.android.client.ninjacat.core.service.api.UserApiService
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.screens.helpers.Validators
import javax.inject.Inject

class LoginViewModel @Inject constructor(userDao: UserDao, userApiService: UserApiService) :
    ViewModel() {
    private val _loginValidationState = MutableLiveData<LoginValidationState>()
    val loginValidationState: LiveData<LoginValidationState> = _loginValidationState
    private val _userLiveData = MutableLiveData<Event<User>>()
    val userLiveData: LiveData<Event<User>> = _userLiveData

    private val loginRepository: LoginRepository =
        LoginRepository(
            userDao,
            userApiService
        )

    suspend fun loginUser(login: String, password: String) {
        if (Validators.loginLightCheck(login) == 0
            && Validators.passwordLightCheck(password) == 0
        ) {
            loginRepository.loginUser(_userLiveData, login, password)
        } else {
            loginChanged(login)
            passwordChanged(password)
        }
    }

    fun loginChanged(login: String?) {
        val loginValidationError: Int? = Validators.isLoginValid(login)
        loginValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _loginValidationState.postValue(
                    LoginValidationState(
                    loginError = errorMessage,
                    passwordError = _loginValidationState.value?.passwordError
                )
                )
            } else {
                _loginValidationState.postValue(
                    LoginValidationState(
                    loginError = null,
                    passwordError = _loginValidationState.value?.passwordError
                )
                )
            }
        }
    }

    fun passwordChanged(password: String?) {
        val passwordValidationError: Int? = Validators.isPasswordValid(password)
        passwordValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _loginValidationState.postValue(
                    LoginValidationState(
                    loginError = _loginValidationState.value?.loginError,
                    passwordError = errorMessage
                )
                )
            } else {
                _loginValidationState.postValue(
                    LoginValidationState(
                    loginError = _loginValidationState.value?.loginError,
                    passwordError = null
                )
                )
            }
        }
    }
}

