package com.kshvmdn.remotesms

data class CommonResponse(
        val success: Boolean,
        val message: String?,
        val data: Any?
)

data class SmsItem(
        val id: String?,
        val address: String,
        val name: String,
        val contact: Any?,
        val time: Long,
        val body: String,
        val type: String?
)
