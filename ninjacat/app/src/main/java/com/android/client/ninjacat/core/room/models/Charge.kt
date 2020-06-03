package com.android.client.ninjacat.core.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity
class Charge(@PrimaryKey var id: Long, var expenseId: Long, var amount: Int, var chargeDate: OffsetDateTime)