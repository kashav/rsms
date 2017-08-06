package com.kshvmdn.remotesms.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.SmsMessage
import android.util.Log
import com.kshvmdn.remotesms.APIServer

import com.kshvmdn.remotesms.Notifications
import com.kshvmdn.remotesms.SocketServer

import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.toast
import org.jetbrains.anko.wifiManager

import java.util.*
import kotlin.concurrent.thread

class ServerService : Service() {
    val TAG = "ServerService"

    private lateinit var notifications: Notifications

    companion object {
        val ACTION_STOP = "com.kshvmdn.remotesms.ACTION_STOP_SERVER"

        var running = false
        var token = ""

        var apiServer: APIServer? = null
        var apiPort = 38300

        var socketServer: SocketServer? = null
        var socketPort = 38301
    }

    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        apiServer = APIServer(applicationContext)
        socketServer = SocketServer(applicationContext)
        notifications = Notifications(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                Log.d(TAG, "stopping servers")
                notificationManager.cancel(Notifications.SERVER_NOTIFICATION_ID)
                stopSelf()
            }
            else -> restartServer()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        running = false
        apiServer?.stop()
        socketServer?.stop()
    }

    private fun restartServer() {
        synchronized(this, {
            if (running) return

            val ipAddr = wifiManager.connectionInfo.ipAddress
            val addrFmted = String.format(
                    Locale.ENGLISH,
                    "%d.%d.%d.%d",
                    ipAddr and 0xff,
                    ipAddr shr 8 and 0xff,
                    ipAddr shr 16 and 0xff,
                    ipAddr shr 24 and 0xff)

            try {
                apiServer?.stop()
                apiServer?.start()

                // TODO: Something weird is happening when we try to stop then start the socket
                // server, investigate.
                thread { socketServer?.start() }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                return
            }

            running = true

            val message = "Running at http://$addrFmted on :$apiPort (API) and :$socketPort (WebSocket)."
            toast(message)
            Log.i(TAG, message)
            startForeground(Notifications.SERVER_NOTIFICATION_ID,
                    notifications.serverNotification(message))
        })
    }
}
