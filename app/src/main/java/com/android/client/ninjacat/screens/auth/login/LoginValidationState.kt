package com.android.client.ninjacat.screens.auth.login

/**
 * Data validation state of the login form.
 */
data class LoginValidationState(
    val loginError: Int? = null,
    val passwordError: Int? = null
)

