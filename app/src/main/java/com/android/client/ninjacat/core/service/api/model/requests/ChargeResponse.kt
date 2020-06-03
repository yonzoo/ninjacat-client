package com.android.client.ninjacat.core.service.api.model.requests

data class ChargeResponse(val id: Long, val expenseId: Long, val amount: Int, val chargeDate: String)