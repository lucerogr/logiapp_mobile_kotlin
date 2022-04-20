package com.example.logiapplication.models

import com.google.gson.annotations.SerializedName


class User (
    val codigo: Int,

    val userUsername: String,

    val userPassword: String,

    val roleId: Int,

    val personId: Int
)