package com.android.client.ninjacat.core.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity
data class Sale(
    @PrimaryKey var id: Long,
    var name: String,
    var quantity: Int,
    var amount: Int,
    val saleDate: OffsetDateTime
)