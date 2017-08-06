package com.kshvmdn.remotesms.utils

import android.util.Log

import com.google.gson.GsonBuilder

private val gson = GsonBuilder().create()

fun toJson(data: Any?): String {
    return gson.toJson(data)
}