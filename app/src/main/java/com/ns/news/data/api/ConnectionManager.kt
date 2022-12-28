package com.news.data.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Build
import java.util.concurrent.atomic.AtomicBoolean

class ConnectionManager (private val context: Context) {

  val isConnected: Boolean
    get() = _isConnected.get()

  private var _isConnected: AtomicBoolean = AtomicBoolean(false)

  init {
    listenToConnectionChanges()
  }

  private fun listenToConnectionChanges() {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCallback = object: ConnectivityManager.NetworkCallback() {
      override fun onAvailable(network: Network) {
        _isConnected = AtomicBoolean(true)
      }

      override fun onLost(network: Network) {
        _isConnected = AtomicBoolean(false)
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      cm.registerDefaultNetworkCallback(networkCallback)
    } else {
      cm.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }
  }
}