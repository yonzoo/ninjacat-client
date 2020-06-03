package com.android.client.ninjacat.screens.auth.registration

/**
 * Data validation state of the login form.
 */
data class RegistrationValidationState(
    val loginError: Int? = null,
    val passwordError: Int? = null
)

