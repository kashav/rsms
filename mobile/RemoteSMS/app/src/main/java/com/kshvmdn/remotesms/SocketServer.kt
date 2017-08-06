package com.kshvmdn.remotesms

import android.content.Context
import android.util.Log

import com.kshvmdn.remotesms.service.ServerService
import com.kshvmdn.remotesms.utils.toJson

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer

import java.lang.Exception
import java.net.InetSocketAddress

class SocketServer(context: Context) : WebSocketServer(InetSocketAddress(ServerService.socketPort)) {
    val TAG = "SocketServer"

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "Disconnected from ${conn?.remoteSocketAddress}")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        Log.e(TAG, ex?.message, ex)
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        Log.d(TAG, "Got message from ${conn?.remoteSocketAddress}: ${message}")
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        Log.d(TAG, "New connection to ${conn?.remoteSocketAddress}")
    }

    override fun onStart() {
    }

    fun send(messages: List<SmsItem>?) {
        try {
            val con = connections()
            synchronized(this, {
                messages?.forEach { message ->
                    for (c in con) c.send(toJson(message))
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }
}
