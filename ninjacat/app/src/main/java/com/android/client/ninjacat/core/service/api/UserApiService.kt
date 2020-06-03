package com.android.client.ninjacat.core.service.api

import com.android.client.ninjacat.core.service.api.model.requests.AccountResponse
import com.android.client.ninjacat.core.utils.Utils
import com.android.client.ninjacat.core.service.api.model.requests.LoginBody
import com.android.client.ninjacat.core.service.api.model.requests.RegistrationBody
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST(Utils.URL_LOGIN)
    fun loginAsync(@Body loginBody: LoginBody): Deferred<Response<AccountResponse>>

    @POST(Utils.URL_REGISTER)
    fun registerAsync(@Body registrationBody: RegistrationBody): Deferred<Response<AccountResponse>>
}