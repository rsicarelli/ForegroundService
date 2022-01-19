package com.rsicarelli.foregroundservice

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import java.lang.Exception


class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            context.startForegroundService()
            Log.i(TAG, "Starting Service ConnectivityListener")
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    companion object {
        private const val TAG = "some_tag"
    }
}