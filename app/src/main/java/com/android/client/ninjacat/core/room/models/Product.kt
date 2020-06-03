package com.android.client.ninjacat.core.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(@PrimaryKey var id: Long, var name: String, var quantity: Int, var amount: Int)