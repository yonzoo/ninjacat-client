package com.android.client.ninjacat.screens.auth.login

import androidx.lifecycle.MutableLiveData
import com.android.client.ninjacat.R
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.User
import com.android.client.ninjacat.core.service.api.UserApiService
import com.android.client.ninjacat.screens.auth.Error
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.core.service.api.model.requests.LoginBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class LoginRepository(
    private val userDao: UserDao,
    private val userApiService: UserApiService
) {

    suspend fun loginUser(
        liveData: MutableLiveData<Event<User>>,
        login: String,
        password: String
    ) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = userApiService.loginAsync(LoginBody(login, password))

                val response = request.await()
                val responseStatus = response.code()
                val responseBody = response.body()
                val responseHeaders = response.headers()

                if (responseStatus == 200 && responseBody != null) {
                    val user = User(responseBody.username, responseBody.userRole)
                    userDao.insert(user)
                    AppController.prefs.accessToken = responseHeaders["Authorization"]
                    AppController.prefs.userLogin = user.login
                    liveData.postValue(Event.success(user))
                } else {
                    liveData.postValue(Event.error(Error(getLoginErrorMessage(responseStatus))))
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(R.string.failed_login))
                )
            }
        }
    }

    private fun getLoginErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            400 -> R.string.bad_request
            403 -> R.string.bad_login_data
            404 -> R.string.user_not_found
            else -> R.string.failed_login
        }
    }
}

