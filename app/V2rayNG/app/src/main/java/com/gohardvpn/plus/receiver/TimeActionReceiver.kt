package com.gohardvpn.plus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.gohardvpn.plus.AppConfig
import com.gohardvpn.plus.handler.MmkvManager
import com.gohardvpn.plus.service.V2RayVpnService

class TimeActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getStringExtra("action") ?: return

        val selectedServer = MmkvManager.getSelectServer()
        if (selectedServer.isNullOrEmpty()) {
            return
        }

        when (action) {
            "start" -> startVpn(context)
            "stop" -> stopVpn(context)
        }
    }

    private fun startVpn(context: Context) {
        val serviceIntent = Intent(context, V2RayVpnService::class.java).apply {
            putExtra("action", "start")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    private fun stopVpn(context: Context) {
        val serviceIntent = Intent(context, V2RayVpnService::class.java).apply {
            putExtra("action", "stop")
        }
        context.startService(serviceIntent)
    }
}
