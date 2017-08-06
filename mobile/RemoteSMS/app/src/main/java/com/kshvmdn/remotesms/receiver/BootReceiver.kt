package com.kshvmdn.remotesms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.kshvmdn.remotesms.service.SMSService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, SMSService::class.java))
    }
}
