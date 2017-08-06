package com.kshvmdn.remotesms.controller

import android.content.Context
import android.os.AsyncTask
import android.util.Log

import com.kshvmdn.remotesms.utils.getMessages

class SMSController(val context: Context) : AsyncTask<Any, Any, Any>() {
    val TAG = "SMSController"
    val PHONE_NUMBER_RE = "(\\s|\\(|\\)|-|_|(\\+1))+".toRegex()

    override fun doInBackground(vararg phoneNumbers: Any?): Any {
        val senders = HashMap<String, Boolean>()

        return getMessages(context.contentResolver).filter { message ->
            val sender = message.address
                    .toLowerCase()
                    .replace(PHONE_NUMBER_RE, "")

            if (!senders.containsKey(sender)) {
                senders[sender] = phoneNumbers.any {
                    number ->
                    val n = number.toString().trim()
                    n.contains(sender) || sender.contains(n)
                }
            }

            senders[sender] ?: false
        }
    }
}
