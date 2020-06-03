package com.android.client.ninjacat.core.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Expense(@PrimaryKey var id: Long, var name: String)