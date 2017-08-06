package com.kshvmdn.remotesms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

import com.kshvmdn.remotesms.service.SMSService
import com.kshvmdn.remotesms.service.ServerService
import com.kshvmdn.remotesms.SmsItem
import com.kshvmdn.remotesms.utils.getContactName

class SMSReceiver : BroadcastReceiver() {
    val TAG = "SMSReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action?.equals(SMSService.ACTION_SMS_RECEIVED) ?: false) {
            val bundle = intent?.extras
            val pdus = bundle?.get("pdus") as? Array<Any>
            val messages = pdus?.map {
                SmsMessage.createFromPdu(it as? ByteArray, bundle?.getString("format"))
            }
            onSmsReceived(context, messages)
        }
    }

    private fun onSmsReceived(context: Context?, messages: List<SmsMessage>?) {
        if (!ServerService.running) return

        ServerService.socketServer?.send(messages?.map {
            val address = it.displayOriginatingAddress ?: ""
            val name = getContactName(context?.contentResolver, it.displayOriginatingAddress) ?: ""
            SmsItem(
                    id = it.indexOnIcc.toString(),
                    address = address,
                    name = name,
                    body = it.messageBody,
                    contact = null,
                    time = it.timestampMillis,
                    type = "1")
        })
    }
}
