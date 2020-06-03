package com.android.client.ninjacat.screens.auth.registration

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.client.ninjacat.R
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.models.User
import com.android.client.ninjacat.core.service.api.UserApiService
import com.android.client.ninjacat.core.service.api.model.requests.RegistrationBody
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.screens.auth.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

/**
 * This class is responsible for authorization logic of signing in user
 */
@Singleton
class RegistrationRepository(
    private val userDao: UserDao,
    private val userApiService: UserApiService
) {

    suspend fun registerUser(
        liveData: MutableLiveData<Event<User>>,
        login: String,
        password: String
    ) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = userApiService.registerAsync(
                    RegistrationBody(
                        login,
                        password
                    )
                )

                val response = request.await()
                val responseStatus = response.code()
                val responseBody = response.body()

                if (responseStatus == 200 && responseBody != null) {
                    withContext(Dispatchers.Default) {
                        userDao.insert(
                            User(responseBody.username, responseBody.userRole)
                        )
                    }
                    liveData.postValue(Event.success(null))
                } else {
                    liveData.postValue(
                        Event.error(
                            Error(getRegistrationErrorMessage(responseStatus))
                        )
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(
                        Error(getRegistrationErrorMessage(-1))
                    )
                )
            }
        }
    }


    private fun getRegistrationErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            400 -> R.string.bad_request
            409 -> R.string.user_exists
            else -> R.string.failed_registration
        }
    }
}

