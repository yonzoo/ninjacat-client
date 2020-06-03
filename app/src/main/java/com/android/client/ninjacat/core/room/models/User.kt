package com.android.client.ninjacat.core.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(var login: String, var role: String) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}