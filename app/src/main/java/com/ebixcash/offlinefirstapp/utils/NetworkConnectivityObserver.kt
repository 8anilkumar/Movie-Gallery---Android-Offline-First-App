package com.ebixcash.offlinefirstapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

sealed class ConnectivityStatus {
    object Available : ConnectivityStatus()
    object Unavailable : ConnectivityStatus()
}

class NetworkConnectivityObserver(
    private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observe(): Flow<ConnectivityStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(ConnectivityStatus.Available)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(ConnectivityStatus.Unavailable)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val isConnected = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
                if (isConnected) {
                    trySend(ConnectivityStatus.Available)
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}
