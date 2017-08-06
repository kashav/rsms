package com.kshvmdn.remotesms

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon

import com.kshvmdn.remotesms.service.ServerService

class Notifications(val context: Context) {
    companion object {
        val SERVER_NOTIFICATION_ID = 1
    }

    fun serverNotification(message: String): Notification? {
        val intent = Intent(context, MainActivity::class.java)
        val stopIntent = Intent(context, ServerService::class.java)
        stopIntent.action = ServerService.ACTION_STOP

        val stopAction = Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_stop_black_24dp),
                "stop server",
                PendingIntent.getService(context, 0, stopIntent, 0)).build()

        val notification = Notification.Builder(context)
                .addAction(stopAction)
                .setContentTitle("Server")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_settings_remote_white_24dp)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
                .setVisibility(Notification.VISIBILITY_SECRET)
                .build()

        return notification
    }
}
