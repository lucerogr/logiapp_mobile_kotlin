package com.example.logiapplication.models

import com.google.gson.annotations.SerializedName


class User (
    val codigo: Int,

    val userUsername: String,

    val userPassword: String,

    var roleId:  Int,

    var personId: Int
)