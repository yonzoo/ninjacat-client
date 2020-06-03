package com.android.client.ninjacat.screens.admin

data class AdminProductValidationState(
    val nameError: Int? = null,
    val quantityError: Int? = null,
    val amountError: Int? = null
)