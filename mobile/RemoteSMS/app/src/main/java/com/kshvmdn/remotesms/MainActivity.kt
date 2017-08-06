package com.kshvmdn.remotesms

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.Button

import com.kshvmdn.remotesms.service.SMSService
import com.kshvmdn.remotesms.service.ServerService
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById<Toolbar>(R.id.appbar))

        val serverButton = findViewById<Button>(R.id.button_server_toggle)

        serverButton.setOnClickListener {
            if (ServerService.running) {
                stopServices()
                serverButton.text = getText(R.string.server_start)
            } else {
                startServices()
                serverButton.text = getText(R.string.server_stop)
            }
        }

        val typeface = Typeface.createFromAsset(assets, "fonts/Montserrat/Montserrat-Regular.ttf")

        findViewById<TextInputLayout>(R.id.til_token).setTypeface(typeface)
        val tokenInput = findViewById<TextInputEditText>(R.id.input_token)
        tokenInput.setOnKeyListener(View.OnKeyListener { _, _, _ ->
            // TODO: Figure out why this isn't working properly.
            ServerService.token = tokenInput.text.toString()
            false
        })

        findViewById<TextInputLayout>(R.id.til_api_port).setTypeface(typeface)
        val apiPortInput = findViewById<TextInputEditText>(R.id.input_api_port)
        apiPortInput.setOnKeyListener(View.OnKeyListener { _, _, _ ->
            if (!apiPortInput.text.isEmpty() && apiPortInput.text.toString().toInt() <= 65535) {
                ServerService.apiPort = apiPortInput.text.toString().toInt()
            }
            false
        })

        findViewById<TextInputLayout>(R.id.til_ws_port).setTypeface(typeface)
        val wsPortInput = findViewById<TextInputEditText>(R.id.input_ws_port)
        wsPortInput.setOnKeyListener(View.OnKeyListener { _, _, _ ->
            if (!wsPortInput.text.isEmpty() && wsPortInput.text.toString().toInt() <= 65535) {
                ServerService.wsPort = wsPortInput.text.toString().toInt()
            }
            false
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_source -> {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/kshvmdn/rsms")))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.SEND_SMS
        )
        val requestCode = 123
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun startServices() {
        startService(Intent(this, SMSService::class.java))
        thread { startService(Intent(this, ServerService::class.java)) }
    }

    private fun stopServices() {
        stopService(Intent(this, SMSService::class.java))
        stopService(Intent(this, ServerService::class.java))
    }
}
