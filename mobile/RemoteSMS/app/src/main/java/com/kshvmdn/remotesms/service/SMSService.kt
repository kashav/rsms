package com.kshvmdn.remotesms.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

import com.kshvmdn.remotesms.receiver.SMSReceiver

class SMSService : Service() {
    val TAG = "SMSService"
    val smsReceiver = SMSReceiver()

    companion object {
        val ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }

    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_SMS_RECEIVED)
        registerReceiver(smsReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(smsReceiver)

        val intent = Intent("com.android.kshvmdn.permanent")
        sendBroadcast(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}